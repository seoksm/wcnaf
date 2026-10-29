import { randomBytes } from 'node:crypto';
import { request } from 'node:https';
import type { IncomingMessage, ServerResponse } from 'node:http';
import type { Plugin } from 'vite';

const PREFIX = '/__kakao-test';
const PAGE = '/src/pages/kakao_login_test.html';
const COOKIE = 'wcnaf_kakao_test';
const TTL = 10 * 60 * 1000;
type Session = { state?: string; accessToken?: string; expiresAt: number; error?: string };
type KakaoResponse = { access_token?: string; expires_in?: number; id?: number };
type Transport = (url: string, body?: URLSearchParams, token?: string) => Promise<KakaoResponse>;

const requestKakao: Transport = (url, body, token) => new Promise((resolve, reject) => {
  const req = request(url, {
    method: body ? 'POST' : 'GET',
    headers: {
      ...(body ? { 'Content-Type': 'application/x-www-form-urlencoded' } : {}),
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    timeout: 10000,
  }, (res) => {
    let text = '';
    res.setEncoding('utf8');
    res.on('data', (chunk) => { text += chunk; });
    res.on('error', reject);
    res.on('end', () => {
      try {
        const data = JSON.parse(text);
        if ((res.statusCode || 500) >= 400) {
          const code = data.error_code || data.code || res.statusCode;
          reject(new Error(`카카오 요청 실패 (${code}). 앱 키, Client secret, Redirect URI 설정을 확인하세요.`));
        } else resolve(data);
      } catch { reject(new Error('카카오 응답을 처리하지 못했습니다. 다시 시도하세요.')); }
    });
  });
  req.on('timeout', () => req.destroy());
  req.on('error', () => reject(new Error('카카오 서버에 연결하지 못했습니다. 다시 시도하세요.')));
  req.end(body?.toString());
});

// 실제 서비스 세션과 분리한 개발 서버 테스트이며, 토큰은 서버 메모리에만 둔다.
export function kakaoTestPlugin(env: Record<string, string>, transport: Transport = requestKakao): Plugin {
  const sessions = new Map<string, Session>();
  return {
    name: 'kakao-login-test',
    apply: 'serve',
    configureServer(server) {
      server.middlewares.use((req, res, next) => {
        if (!req.url?.startsWith(`${PREFIX}/`)) return next();
        void handle(req, res).catch(() => json(res, 500, { message: '테스트 요청 처리에 실패했습니다. 다시 로그인하세요.' }));
      });
    },
  };

  function json(res: ServerResponse, status: number, data: unknown) {
    res.writeHead(status, { 'Content-Type': 'application/json; charset=utf-8', 'Cache-Control': 'no-store' });
    res.end(JSON.stringify(data));
  }

  async function handle(req: IncomingMessage, res: ServerResponse) {
    res.setHeader('Cache-Control', 'no-store');
    res.setHeader('Referrer-Policy', 'no-referrer');
    if (env.KAKAO_TEST_ENABLED !== 'true') return json(res, 503, { message: '.env.local에 KAKAO_TEST_ENABLED=true와 카카오 테스트 설정을 입력한 뒤 개발 서버를 재시작하세요.' });
    let origin: URL;
    try {
      origin = new URL(env.KAKAO_TEST_ORIGIN);
      if (!['http:', 'https:'].includes(origin.protocol) || origin.origin !== env.KAKAO_TEST_ORIGIN) throw new Error();
    } catch { return json(res, 503, { message: 'KAKAO_TEST_ORIGIN에 실제 개발 서버 주소를 경로와 마지막 / 없이 입력하세요.' }); }
    if (req.headers.host !== origin.host) return json(res, 403, { message: `설정된 테스트 주소(${origin.origin})로 접속하세요.` });
    if (!/^[a-f0-9]{32}$/i.test(env.KAKAO_TEST_REST_API_KEY || '')) return json(res, 503, { message: 'KAKAO_TEST_REST_API_KEY에 해당 앱의 실제 REST API 키를 입력하세요.' });
    const url = new URL(req.url!, origin);
    const redirectUri = `${origin.origin}${PREFIX}/callback`;
    const cookieOptions = `Path=${PREFIX}; HttpOnly; SameSite=Lax${origin.protocol === 'https:' ? '; Secure' : ''}`;
    const setCookie = (id: string, age: number) => res.setHeader('Set-Cookie', `${COOKIE}=${id}; Max-Age=${age}; ${cookieOptions}`);
    for (const [id, value] of sessions) if (value.expiresAt <= Date.now()) sessions.delete(id);
    const id = req.headers.cookie?.split(';').map((part) => part.trim()).find((part) => part.startsWith(`${COOKIE}=`))?.slice(COOKIE.length + 1);
    const session = id ? sessions.get(id) : undefined;
    const goBack = () => { res.writeHead(303, { Location: PAGE }); res.end(); };

    if (url.pathname === `${PREFIX}/status` && req.method === 'GET') {
      const error = session?.error;
      if (session) delete session.error;
      return json(res, 200, { authenticated: Boolean(session?.accessToken), error, redirectUri, origin: origin.origin });
    }
    if (url.pathname === `${PREFIX}/callback` && req.method === 'GET') {
      if (!session?.state || session.state !== url.searchParams.get('state')) {
        res.writeHead(400, { 'Content-Type': 'text/plain; charset=utf-8' });
        return res.end('로그인 요청 확인에 실패했습니다. 테스트 페이지에서 다시 로그인하세요.');
      }
      delete session.state;
      if (url.searchParams.has('error')) {
        session.error = '카카오 로그인이 취소되었거나 거부되었습니다. 다시 시도하세요.';
        return goBack();
      }
      const code = url.searchParams.get('code');
      if (!code) { session.error = '인가 코드가 없습니다. 다시 로그인하세요.'; return goBack(); }
      try {
        const body = new URLSearchParams({ grant_type: 'authorization_code', client_id: env.KAKAO_TEST_REST_API_KEY, redirect_uri: redirectUri, code });
        if (env.KAKAO_TEST_CLIENT_SECRET) body.set('client_secret', env.KAKAO_TEST_CLIENT_SECRET);
        const result = await transport('https://kauth.kakao.com/oauth/token', body);
        if (!result.access_token || !result.expires_in || result.expires_in <= 0) throw new Error('유효한 토큰이 발급되지 않았습니다.');
        const newId = randomBytes(32).toString('hex');
        const duration = Math.min(result.expires_in * 1000, 30 * 60 * 1000);
        sessions.delete(id!);
        sessions.set(newId, { accessToken: result.access_token, expiresAt: Date.now() + duration });
        setCookie(newId, Math.floor(duration / 1000));
      } catch (error) { session.error = (error as Error).message; }
      return goBack();
    }
    if (req.method !== 'POST' || req.headers.origin !== origin.origin) return json(res, 403, { message: '테스트 페이지에서 요청하세요.' });
    if (url.pathname === `${PREFIX}/start`) {
      if (sessions.size >= 1000) return json(res, 429, { message: '테스트 요청이 많습니다. 잠시 후 다시 시도하세요.' });
      if (id) sessions.delete(id);
      const newId = randomBytes(32).toString('hex');
      const state = randomBytes(32).toString('hex');
      sessions.set(newId, { state, expiresAt: Date.now() + TTL });
      setCookie(newId, TTL / 1000);
      const params = new URLSearchParams({ client_id: env.KAKAO_TEST_REST_API_KEY, redirect_uri: redirectUri, response_type: 'code', state });
      return json(res, 200, { authorizeUrl: `https://kauth.kakao.com/oauth/authorize?${params}` });
    }
    if (!session?.accessToken) return json(res, 401, { message: '로그인 세션이 없거나 만료되었습니다. 다시 로그인하세요.' });
    try {
      if (url.pathname === `${PREFIX}/me`) {
        const user = await transport('https://kapi.kakao.com/v2/user/me', undefined, session.accessToken);
        if (!user.id) throw new Error('사용자 정보를 확인하지 못했습니다. 다시 로그인하세요.');
        return json(res, 200, user);
      }
      if (url.pathname === `${PREFIX}/logout`) {
        await transport('https://kapi.kakao.com/v1/user/logout', new URLSearchParams(), session.accessToken);
        sessions.delete(id!);
        setCookie('', 0);
        return json(res, 200, { message: '카카오 테스트 로그아웃을 완료했습니다.' });
      }
    } catch (error) {
      if (url.pathname.endsWith('/me')) { sessions.delete(id!); setCookie('', 0); }
      return json(res, 502, { message: (error as Error).message });
    }
    return json(res, 404, { message: '지원하지 않는 테스트 요청입니다.' });
  }
}
