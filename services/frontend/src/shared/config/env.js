/**
 * 환경 설정
 */

/**
 * 로컬 환경 여부
 */
export const isLocalhost = () => {
  if (typeof window === 'undefined') return false;
  return window.location.hostname === 'localhost';
};

/**
 * 개발 환경 여부
 */
export const isDevelopment = () => {
  return import.meta.env.VITE_NODE_ENV === 'DEV';
};

/**
 * 프로덕션 환경 여부
 */
export const isProduction = () => {
  return import.meta.env.VITE_NODE_ENV === 'PROP';
};

export const isNodeEnv = () => {
  return import.meta.env.VITE_NODE_ENV;
};
// 암호화 여부
export const isEncryption = () => {
  return import.meta.env.VITE_ENCRYPTION === 'true';
};

// 연결된 서버 주소
export const getInternalUrl = () => {
  return import.meta.env.VITE_INTERNAL_URL;
};

/**
 * 환경 상수
 */
export const ENV = {
  IS_LOCALHOST: isLocalhost(),
  IS_DEV: isDevelopment(),
  IS_PROD: isProduction(),
  IS_ENV: isNodeEnv(),
  INTERNAL_URL: getInternalUrl(),
};
