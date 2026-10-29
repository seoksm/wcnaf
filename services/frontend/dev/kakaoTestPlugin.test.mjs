import test from 'node:test';
import assert from 'node:assert/strict';
import { kakaoTestPlugin } from './kakaoTestPlugin.ts';

const origin = 'http://localhost:5173';
const env = { KAKAO_TEST_ENABLED: 'true', KAKAO_TEST_ORIGIN: origin, KAKAO_TEST_REST_API_KEY: 'a'.repeat(32), KAKAO_TEST_CLIENT_SECRET: 'test-secret' };

function setup(settings = env, transport = async () => ({})) {
  let middleware;
  kakaoTestPlugin(settings, transport).configureServer({ middlewares: { use: (handler) => { middleware = handler; } } });
  return (path, { method = 'GET', cookie = '', requestOrigin = origin } = {}) => new Promise((resolve) => {
    const headers = {};
    let status;
    const response = {
      setHeader: (key, value) => { headers[key.toLowerCase()] = value; },
      writeHead: (code, values = {}) => { status = code; for (const [key, value] of Object.entries(values)) headers[key.toLowerCase()] = value; },
      end: (body) => resolve({ status, headers, body, data: headers['content-type']?.includes('application/json') ? JSON.parse(body) : undefined }),
    };
    middleware({ url: `/__kakao-test/${path}`, method, headers: { host: 'localhost:5173', origin: requestOrigin, cookie } }, response, () => resolve({ status: 404 }));
  });
}

const cookieOf = (response) => response.headers['set-cookie'].split(';')[0];
const stateOf = (response) => new URL(response.data.authorizeUrl).searchParams.get('state');

test('미설정·자리표시자 키와 다른 origin의 요청을 차단한다', async () => {
  assert.equal((await setup({})('status')).status, 503);
  assert.equal((await setup({ ...env, KAKAO_TEST_REST_API_KEY: '${REST_API_KEY}' })('start', { method: 'POST' })).status, 503);
  assert.equal((await setup()('start', { method: 'POST', requestOrigin: 'https://other.example' })).status, 403);
});

test('정상 콜백은 토큰을 서버에 보관하고 세션을 교체하며 조회와 로그아웃을 지원한다', async () => {
  const calls = [];
  const send = setup(env, async (url, body, token) => {
    calls.push({ url, body, token });
    return url.includes('/oauth/token') ? { access_token: 'private-token', expires_in: 3600 } : { id: 123 };
  });
  const start = await send('start', { method: 'POST' });
  const cookie = cookieOf(start);
  const state = stateOf(start);
  assert.equal(new URL(start.data.authorizeUrl).searchParams.get('redirect_uri'), `${origin}/__kakao-test/callback`);
  assert.match(start.headers['set-cookie'], /HttpOnly; SameSite=Lax/);
  const callback = await send(`callback?code=one-use-code&state=${state}`, { cookie });
  assert.equal(callback.status, 303);
  assert.equal(calls[0].body.get('client_secret'), 'test-secret');
  const authenticatedCookie = cookieOf(callback);
  assert.notEqual(authenticatedCookie, cookie);
  const status = await send('status', { cookie: authenticatedCookie });
  assert.equal(status.data.authenticated, true);
  assert.ok(!JSON.stringify(status).includes('private-token'));
  assert.equal((await send(`callback?code=one-use-code&state=${state}`, { cookie })).status, 400);
  assert.equal((await send('me', { method: 'POST', cookie: authenticatedCookie })).data.id, 123);
  assert.equal(calls[1].token, 'private-token');
  assert.equal((await send('logout', { method: 'POST', cookie: authenticatedCookie })).status, 200);
  assert.equal((await send('me', { method: 'POST', cookie: authenticatedCookie })).status, 401);
});

test('state 불일치는 토큰 교환 전에 차단하며 로그인 취소를 전달한다', async () => {
  let called = false;
  const send = setup(env, async () => { called = true; return {}; });
  const start = await send('start', { method: 'POST' });
  const cookie = cookieOf(start);
  assert.equal((await send('callback?code=test&state=wrong', { cookie })).status, 400);
  assert.equal(called, false);
  assert.equal((await send(`callback?error=access_denied&state=${stateOf(start)}`, { cookie })).status, 303);
  assert.match((await send('status', { cookie })).data.error, /취소/);
});

test('토큰 발급 실패와 유효하지 않은 토큰의 조회 실패를 처리한다', async () => {
  let failToken = true;
  const send = setup(env, async (url) => {
    if (url.includes('/oauth/token') && !failToken) return { access_token: 'expired-token', expires_in: 60 };
    throw new Error('카카오 요청 실패 (KOE010)');
  });
  const first = await send('start', { method: 'POST' });
  await send(`callback?code=test&state=${stateOf(first)}`, { cookie: cookieOf(first) });
  assert.match((await send('status', { cookie: cookieOf(first) })).data.error, /KOE010/);
  failToken = false;
  const second = await send('start', { method: 'POST' });
  const callback = await send(`callback?code=test&state=${stateOf(second)}`, { cookie: cookieOf(second) });
  const cookie = cookieOf(callback);
  assert.equal((await send('me', { method: 'POST', cookie })).status, 502);
  assert.equal((await send('status', { cookie })).data.authenticated, false);
});

test('만료된 인증 요청과 인증 세션을 재사용할 수 없다', async (context) => {
  const now = Date.now();
  const clock = context.mock.method(Date, 'now', () => now);
  const send = setup(env, async () => ({ access_token: 'token', expires_in: 60 }));
  const pending = await send('start', { method: 'POST' });
  clock.mock.mockImplementation(() => now + 11 * 60 * 1000);
  assert.equal((await send(`callback?code=test&state=${stateOf(pending)}`, { cookie: cookieOf(pending) })).status, 400);
  const start = await send('start', { method: 'POST' });
  const callback = await send(`callback?code=test&state=${stateOf(start)}`, { cookie: cookieOf(start) });
  clock.mock.mockImplementation(() => now + 13 * 60 * 1000);
  assert.equal((await send('me', { method: 'POST', cookie: cookieOf(callback) })).status, 401);
});
