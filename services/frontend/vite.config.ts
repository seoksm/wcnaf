import { defineConfig } from 'vite';
import type { Plugin } from 'vite';
import react from '@vitejs/plugin-react-swc';
import * as fs from 'node:fs';
import * as path from 'node:path';
import * as http from 'node:http';
import viteTsconfigPaths from 'vite-tsconfig-paths';
import tailwindcss from '@tailwindcss/vite';
import svgr from 'vite-plugin-svgr';

// 카메라(getUserMedia) 등 보안 컨텍스트가 필요한 기능은 HTTPS가 있어야 LAN(모바일)에서 동작한다.
// .tools/certs/에 mkcert로 만든 인증서가 있으면 https로 띄우고, 없으면 기존처럼 http로 동작한다.
const certPath = path.resolve(__dirname, '.tools/certs/dev-cert.pem');
const keyPath = path.resolve(__dirname, '.tools/certs/dev-key.pem');
const httpsOptions =
  fs.existsSync(certPath) && fs.existsSync(keyPath)
    ? { cert: fs.readFileSync(certPath), key: fs.readFileSync(keyPath) }
    : undefined;

// S3(MinIO) presigned PUT 업로드 전용 수제 프록시 - Vite의 내장 server.proxy(http-proxy 기반)는
// 약 350~400KB를 넘는 PUT 바디를 중계할 때 500을 던지는 문제가 있어(실측 확인, 실제 사진
// 크기에서 재현됨) 못 쓴다. Node 기본 http 모듈로 요청/응답 스트림을 그대로 파이프해 그 버그를
// 피한다. HTTPS 페이지에서 HTTP인 MinIO로 바로 나가면 브라우저가 mixed content로 막으므로
// (/api를 프록시하는 것과 동일한 이유), 같은 오리진(이 vite 서버) 경유로 우회한다.
const S3_PROXY_PREFIX = '/s3-proxy';
const S3_TARGET_HOST = '192.168.15.80'; // application-docker.properties의 customEndpoint와 동일해야 함(서명에 host 포함)
const S3_TARGET_PORT = 29000;

function s3ProxyPlugin(): Plugin {
  return {
    name: 's3-presigned-upload-proxy',
    configureServer(server) {
      server.middlewares.use(S3_PROXY_PREFIX, (req, res) => {
        const proxyReq = http.request(
          {
            hostname: S3_TARGET_HOST,
            port: S3_TARGET_PORT,
            path: req.url,
            method: req.method,
            headers: { ...req.headers, host: `${S3_TARGET_HOST}:${S3_TARGET_PORT}` },
          },
          (proxyRes) => {
            res.writeHead(proxyRes.statusCode || 502, proxyRes.headers);
            proxyRes.pipe(res);
          },
        );
        proxyReq.on('error', (err) => {
          if (!res.headersSent) res.writeHead(502, { 'Content-Type': 'text/plain' });
          res.end(`s3-proxy error: ${err.message}`);
        });
        req.pipe(proxyReq);
      });
    },
  };
}

export default defineConfig({
  base: '/',
  server: {
    host: '0.0.0.0',
    https: httpsOptions,
    // 프론트를 https로 띄우면 브라우저가 http인 api-gateway로의 요청을 mixed content로 막는다.
    // 브라우저는 항상 이 vite 서버(같은 오리진)로만 통신하고, /api 요청은 Node(vite) 쪽에서
    // 대신 http://localhost:28099로 프록시한다 - 서버 간 통신이라 mixed content 제약이 없다.
    proxy: {
      '/api': {
        target: 'http://localhost:28099',
        changeOrigin: true,
      },
      // 사진 업로드(S3 presigned PUT)는 이 방식(server.proxy)이 아니라 아래 s3ProxyPlugin의
      // configureServer 수제 프록시로 처리한다(이유는 s3ProxyPlugin 주석 참고).
    },
  },
  plugins: [
    react(),
    s3ProxyPlugin(),
    viteTsconfigPaths(),
    tailwindcss(),
    svgr({
      svgrOptions: {
        plugins: ['@svgr/plugin-svgo', '@svgr/plugin-jsx'],
        svgo: true,
        svgoConfig: {
          plugins: [
            {
              name: 'preset-default',
              params: {
                overrides: {
                  removeViewBox: false,
                },
              },
            },
            {
              name: 'prefixIds',
              params: {
                prefix: (_node, info) => {
                  const filePath = info?.path;
                  if (filePath) {
                    const baseName = path.basename(
                      filePath,
                      path.extname(filePath),
                    );
                    return `wini-${baseName}`;
                  }
                  return 'wini-icon';
                },
              },
            },
          ],
        },
      },
    }),
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
    extensions: ['.js', '.jsx', '.ts', '.tsx'],
  },
  optimizeDeps: {},
});
