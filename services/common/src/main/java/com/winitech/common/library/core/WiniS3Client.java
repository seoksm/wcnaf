package com.winitech.common.library.core;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.winitech.common.exception.InvalidParamException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * S3 업로더 클래스
 * <pre>
 * com.winitech.common.library.core
 * └ S3Uploader.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 24-12-13
 **/
@Slf4j
@RequiredArgsConstructor
@Service
public class WiniS3Client {
	@Resource
	private AmazonS3Client amazonS3Client;

	@Value("${cloud.aws.s3.bucket:}")
	private String bucket;

	/**
	 * S3로 다이렉트 업로드를 위해 서명된 URL을 반환합니다.
	 * @param objectPath 업로드 오브젝트 경로 (S3 Object name)
	 * @param fileLength 업로드할 파일의 크기
	 * @return 서명된 URL
	 */
	public URL getSignedUploadUrl(String objectPath, long fileLength) {
		if (objectPath == null || objectPath.isEmpty()) {
			throw new InvalidParamException("업로드 경로가 없습니다.");
		}
		
		if (objectPath.contains("..")) {
			throw new InvalidParamException("업로드 경로가 잘못되었습니다.");
		}
		
		GeneratePresignedUrlRequest presignedUrlRequest = new GeneratePresignedUrlRequest(bucket, objectPath, HttpMethod.PUT);
		presignedUrlRequest.setExpiration(Date.from(Instant.now().plusSeconds(60 * 10))); // 서명 만료일자 10분
		presignedUrlRequest.putCustomRequestHeader("Content-Length", String.valueOf(fileLength));
		
		return amazonS3Client.generatePresignedUrl(presignedUrlRequest);
	}

	/**
	 * S3로 다이렉트 다운로드를 위해 서명된 URL을 반환합니다.
	 * @param objectPath 다운로드 오브젝트 경로 (S3 Object name)
	 * @return 서명된 URL
	 */
	public URL getSignedDownloadUrl(String objectPath, String mimeType, boolean isInline, String fileName) {
		if (objectPath == null || objectPath.isEmpty()) {
			throw new InvalidParamException("다운로드 경로가 없습니다.");
		}
		
		if (objectPath.contains("..")) {
			throw new InvalidParamException("다운로드 경로가 잘못되었습니다.");
		}
		
		GeneratePresignedUrlRequest presignedUrlRequest = new GeneratePresignedUrlRequest(bucket, objectPath, HttpMethod.GET);
		presignedUrlRequest.setExpiration(Date.from(Instant.now().plusSeconds(60 * 10))); // 서명 만료일자 10분

		ResponseHeaderOverrides headers = null;
		
		if (mimeType != null) {
			if (headers == null) {
				headers = new ResponseHeaderOverrides();
			}
			
			headers.setContentType(mimeType);
		}

		if (fileName != null) {
			if (headers == null) {
				headers = new ResponseHeaderOverrides();
			}
			
			// 사용 브라우저는 Chrome 만 가정해서 테스트
			headers.setContentDisposition((isInline ? "inline" : "attachment") + "; filename=\"" + URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20") + "\"");
		}

		if (headers != null) {
			presignedUrlRequest.setResponseHeaders(headers);
		}
		
		// TODO : PresignedUrl 캐시 (현재 매번 S3에서 다운로드 함)
		return amazonS3Client.generatePresignedUrl(presignedUrlRequest);
	}

	/**
	 * S3에 업로드된 파일을 삭제합니다.
	 * @param objectPath 삭제할 오브젝트 경로 (S3 Object name)
	 * @return 삭제 성공 여부
	 */
	public boolean deleteFile(String objectPath) {
		amazonS3Client.deleteObject(bucket, objectPath);
		
		return true;
	}

	/**
	 * S3에 업로드된 파일을 바이트 배열로 다운로드합니다.
	 * @param objectPath 다운로드할 오브젝트 경로 (S3 Object name)
	 * @return 다운로드된 파일의 바이트 배열
	 * @throws IOException
	 */
	public byte[] directDownloadBytes(String objectPath) throws IOException {
		return directDownload(objectPath).readAllBytes();
	}

	/**
	 * S3에 업로드된 파일을 InputStream으로 다운로드합니다.
	 * @param objectPath 다운로드할 오브젝트 경로 (S3 Object name)
	 * @return 다운로드된 파일의 InputStream
	 */
	public InputStream directDownload(String objectPath) {
		return amazonS3Client.getObject(bucket, objectPath).getObjectContent();
	}

}
