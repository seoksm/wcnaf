import { OpenCommunicator } from '@/shared/api';

const DEV_UPLOAD_PROXY_PREFIX = '/s3-proxy';

export const PHOTO_UPLOAD_ERROR_CODE = {
  PROXY_REQUIRED: 'PHOTO_UPLOAD_PROXY_REQUIRED',
};

const uploadClient = new OpenCommunicator().client;

export const preparePhotoUpload = async (connector, file) => {
  const response = await connector.client.post(
    '/api/v1/smart-asset/commonFile/prepareUpload',
    {
      fileName: file.name,
      fileSize: file.size,
    },
  );

  return response.data;
};

export const uploadPhotoToPresignedUrl = async (uploadUrl, file) => {
  const requestUrl = resolvePhotoUploadUrl(uploadUrl);

  await uploadClient.put(requestUrl, file, {
    headers: {
      'Content-Type': 'application/octet-stream',
    },
  });
};

const resolvePhotoUploadUrl = (uploadUrl) => {
  const parsedUploadUrl = new URL(uploadUrl);
  const configuredProxyPrefix =
    import.meta.env.VITE_S3_UPLOAD_PROXY_PREFIX?.trim();
  const proxyPrefix = import.meta.env.DEV
    ? DEV_UPLOAD_PROXY_PREFIX
    : configuredProxyPrefix;

  if (proxyPrefix) {
    const normalizedPrefix = `/${proxyPrefix.replace(/^\/+|\/+$/g, '')}`;
    return `${window.location.origin}${normalizedPrefix}${parsedUploadUrl.pathname}${parsedUploadUrl.search}`;
  }

  if (
    window.location.protocol === 'https:' &&
    parsedUploadUrl.protocol === 'http:'
  ) {
    const error = new Error(
      'HTTPS 운영 환경의 사진 업로드 프록시가 설정되지 않았습니다. 관리자에게 문의해 주세요.',
    );
    error.code = PHOTO_UPLOAD_ERROR_CODE.PROXY_REQUIRED;
    throw error;
  }

  return uploadUrl;
};
