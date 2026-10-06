package com.winitech.common.interfaces.inboundAdapter.web;

import com.winitech.common.domain.common.CommonFile;
import com.winitech.common.domain.common.CommonFileInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.common.interfaces.inboundAdapter.web
 * └ CommonFileDto.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-16 09:24
 **/
@NoArgsConstructor
public class CommonFileDto {

	@ApiModel("Request of prepare upload")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PrepareUploadRequest {
		@ApiModelProperty(value = "파일명", required = true)
		private String fileName;
		@ApiModelProperty(value = "파일 크기", required = true)
		private Long fileSize;
		@ApiModelProperty(value = "연결 엔티티명")
		private String entityName = null;
		@ApiModelProperty(value = "연결 엔티티 고유번호")
		private UUID entityId = null;
		@ApiModelProperty(value = "보조 키")
		private String subKey = null;
		@ApiModelProperty(value = "추가 정보")
		private String extraInfo = null;
		@ApiModelProperty(value = "가로 픽셀수")
		private Integer width = null;
		@ApiModelProperty(value = "세로 픽셀수")
		private Integer height = null;
		@ApiModelProperty(value = "파일 기원")
		private String fileOrigin = null;
	}

	@ApiModel("Response of prepare upload")
	@Getter
	@ToString
	@NoArgsConstructor
	public static class PrepareUploadResponse {
		@ApiModelProperty(value = "파일 고유번호")
		private UUID fileId;
		@ApiModelProperty(value = "서명된 파일 고유번호", notes = "서명된 파일 고유번호는 다운로드 URL을 생성할 때 사용됩니다.")
		private String signedFileId;
		@ApiModelProperty(value = "업로드 URL")
		private String uploadUrl;
		@ApiModelProperty(value = "HTTP 메소드")
		private HttpMethod httpMethod;
		@ApiModelProperty(value = "MIME 타입")
		private String mimeType;
		
		@Builder
		public PrepareUploadResponse(UUID fileId, String uploadUrl, String signedFileId, HttpMethod httpMethod, String mimeType) {
			this.fileId = fileId;
			this.uploadUrl = uploadUrl;
			this.signedFileId = signedFileId;
			this.httpMethod = httpMethod;
			this.mimeType = mimeType;
		}
	}

	@ApiModel("Response of prepare upload")
	@Getter
	@ToString
	@NoArgsConstructor
	public static class PreparePreviewResponse {
		@ApiModelProperty(value = "파일 고유번호")
		private UUID fileId;
		@ApiModelProperty(value = "서명된 파일 고유번호", notes = "서명된 파일 고유번호는 다운로드 URL을 생성할 때 사용됩니다.")
		private String signedFileId;
		
		@Builder
		public PreparePreviewResponse(UUID fileId, String signedFileId) {
			this.fileId = fileId;
			this.signedFileId = signedFileId;
		}
	}

	@ApiModel("Request of prepare upload")
	@Getter
	@ToString
	@NoArgsConstructor
	public static class PreparePreviewListRequest {
		@ApiModelProperty(value = "파일 고유번호")
		private List<UUID> fileIdList;
		
		@Builder
		public PreparePreviewListRequest(List<UUID> fileIdList) {
			this.fileIdList = fileIdList;
		}
	}

	@ApiModel("Response of prepare upload")
	@Getter
	@ToString
	@NoArgsConstructor
	public static class PreparePreviewListResponse {
		@ApiModelProperty(value = "서명된 파일 고유번호", notes = "서명된 파일 고유번호는 다운로드 URL을 생성할 때 사용됩니다.")
		private List<PreparePreviewResponse> preparedPreviewList;
		
		@Builder
		public PreparePreviewListResponse(List<PreparePreviewResponse> preparedPreviewList) {
			this.preparedPreviewList = preparedPreviewList;
		}
	}

	@ApiModel("Response of prepare upload")
	@Getter
	@ToString
	@NoArgsConstructor
	public static class FileInfoResponse {
		@ApiModelProperty(value = "파일 고유번호")
		private UUID fileId;
		@ApiModelProperty(value = "서명된 파일 고유번호", notes = "서명된 파일 고유번호는 다운로드 URL을 생성할 때 사용됩니다.")
		private String signedFileId;
		@ApiModelProperty(value = "파일명", required = true)
		private String fileName;
		@ApiModelProperty(value = "파일 크기", required = true)
		private Long fileSize;
		@ApiModelProperty(value = "MIME 타입")
		private String mimeType;

		@Builder
		public FileInfoResponse(UUID fileId, String signedFileId, String fileName, Long fileSize, String mimeType) {
			this.fileId = fileId;
			this.signedFileId = signedFileId;
			this.fileName = fileName;
			this.fileSize = fileSize;
			this.mimeType = mimeType;
		}

		public FileInfoResponse(CommonFileInfo info) {
			this.fileId = info.getId();
			this.signedFileId = info.getSignedFileId();
			this.fileName = info.getFileName();
			this.fileSize = info.getFileSize();
			this.mimeType = info.getMimeType();
		}

		public static FileInfoResponse from(CommonFileInfo fileInfo) {
			return new FileInfoResponse(fileInfo);
		}
		
		public static List<CommonFileDto.FileInfoResponse> from(List<CommonFileInfo> fileInfoList) {
			return fileInfoList.stream()
					.map(CommonFileDto.FileInfoResponse::new)
					.collect(Collectors.toList());
		}
	}

	@ApiModel("Request of download of multiple files")
	@Getter
	@ToString
	@NoArgsConstructor
	public static class DownloadMultiRequest {
		@ApiModelProperty(value = "파일 고유번호 목록")
		private List<String> signedFileIdList;
		@ApiModelProperty(value = "파일명")
		private String fileName;
	}
}
