package com.winitech.common.interfaces.inboundAdapter.web;

import com.winitech.common.application.commonFile.CommonFileFacade;
import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.domain.common.CommonFile;
import com.winitech.common.domain.common.CommonFileCommand;
import com.winitech.common.domain.common.CommonFileInfo;
import com.winitech.common.domain.common.CommonFileService;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.common.library.WiniCom;
import com.winitech.common.library.WiniFile;
import com.winitech.common.library.core.WiniS3Client;
import com.winitech.common.response.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 공통 파일 컨트롤러
 * <pre>
 * com.winitech.common.interfaces.inboundAdapter.web
 * └ CommonFileController.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-16 09:14
 **/
@Api(tags = "Common File")
@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/{serviceName}/commonFile")
public class CommonFileController {
	private final CommonFileFacade commonFileFacade;
	private final CommonFileService commonFileService;
	
	private final WiniS3Client winiS3Client;
	private final LoginUserContext loginUserContext;
	
	private String baseObjectDir = "test";

	@PostMapping("/prepareUpload")
	public CommonResponse<CommonFileDto.PrepareUploadResponse> getPrepareUpload(
			@ApiParam(value = "서비스명 (예) docs, code-qna, tech-blog, code-play)", required = true, defaultValue = "현재서비스명") @PathVariable("serviceName") String serviceName,
			@RequestBody CommonFileDto.PrepareUploadRequest request) {
		LocalDateTime now = LocalDateTime.now();
		UUID fileId = WiniCom.getUUIDv7();
		String objectName = fileId.toString().replace("-", "");
		String objectPath = baseObjectDir + "/" + now.getYear() + "/" + now.getMonthValue() + "/" + now.getDayOfMonth() + "/" + objectName;

		WiniFile.checkFileUpload(request.getFileName(), request.getFileSize());

		String fileExt = WiniFile.getFileExtension(request.getFileName()).toLowerCase();
		String mimeType = WiniFile.getMimeType(request.getFileName());

		URL signedUploadUrl = winiS3Client.getSignedUploadUrl(objectPath, request.getFileSize());

		commonFileFacade.registerCommonFile(CommonFileCommand.builder()
				.id(fileId)
				.fileName(request.getFileName())
				.fileExt(fileExt)
				.fileSize(request.getFileSize())
				.mimeType(mimeType)
				.entityName(request.getEntityName())
				.entityId(request.getEntityId())
				.subKey(request.getSubKey())
				.extraInfo(request.getExtraInfo())
				.width(request.getWidth())
				.height(request.getHeight())
				.url(objectPath)
				.userId(loginUserContext.getUserId())
				.fileOrigin(CommonFile.FileOrigin.from(request.getFileOrigin()))
				.build());

		CommonFileDto.PrepareUploadResponse response = CommonFileDto.PrepareUploadResponse.builder()
			.fileId(fileId)
			.uploadUrl(signedUploadUrl.toString())
			.signedFileId(commonFileService.getSignedFileId(fileId))
			.httpMethod(HttpMethod.PUT)
			.mimeType(mimeType)
			.build();		
		
		return CommonResponse.success(response);
	}

	@PostMapping("/prepareImageUpload")
	public CommonResponse<CommonFileDto.PrepareUploadResponse> getPrepareImageUpload(
			@ApiParam(value = "서비스명 (예) docs, code-qna, tech-blog, code-play)", required = true, defaultValue = "현재서비스명") @PathVariable("serviceName") String serviceName,
			@RequestBody CommonFileDto.PrepareUploadRequest request) {
		LocalDateTime now = LocalDateTime.now();
		UUID fileId = WiniCom.getUUIDv7();
		String objectName = fileId.toString().replace("-", "");
		String objectPath = baseObjectDir + "/" + now.getYear() + "/" + now.getMonthValue() + "/" + now.getDayOfMonth() + "/" + objectName;

		WiniFile.checkImageUpload(request.getFileName(), request.getFileSize());

		String fileExt = WiniFile.getFileExtension(request.getFileName()).toLowerCase();
		String mimeType = WiniFile.getMimeType(request.getFileName());

		URL signedUploadUrl = winiS3Client.getSignedUploadUrl(objectPath, request.getFileSize());

		commonFileFacade.registerCommonFile(CommonFileCommand.builder()
				.id(fileId)
				.fileName(request.getFileName())
				.fileExt(fileExt)
				.fileSize(request.getFileSize())
				.mimeType(mimeType)
				.entityName(request.getEntityName())
				.entityId(request.getEntityId())
				.subKey(request.getSubKey())
				.extraInfo(request.getExtraInfo())
				.width(request.getWidth())
				.height(request.getHeight())
				.url(objectPath)
				.userId(loginUserContext.getUserId())
				.fileOrigin(CommonFile.FileOrigin.from(request.getFileOrigin()))
				.build());

		CommonFileDto.PrepareUploadResponse response = CommonFileDto.PrepareUploadResponse.builder()
				.fileId(fileId)
				.uploadUrl(signedUploadUrl.toString())
				.signedFileId(commonFileService.getSignedFileId(fileId))
				.httpMethod(HttpMethod.PUT)
				.mimeType(mimeType)
				.build();

		return CommonResponse.success(response);
	}

	@ApiOperation(value = "파일 다운로드")
	@GetMapping("/download/{signedFileId}")
	public void downloadFile(
			@ApiParam(value = "서비스명 (예) docs, code-qna, tech-blog, code-play)", required = true, defaultValue = "현재서비스명") @PathVariable("serviceName") String serviceName,
			@ApiParam(value = "서명된 파일 ID", required = true) @PathVariable("signedFileId") String signedFileId,
			@ApiParam(value = "파일 다운로드 할 지 여부") @RequestParam(value = "download", required = false) String download,
			HttpServletRequest req, HttpServletResponse res) throws IOException {
		commonFileService.checkSignedFileId(signedFileId);
		
		UUID fileId = commonFileService.getFileIdFromSignedFileId(signedFileId);
		CommonFileInfo commonFileInfo = commonFileFacade.searchCommonFileById(fileId);
		
		if (commonFileInfo.getEntityName() == null || commonFileInfo.getEntityId() == null) {
			// 연결고리가 없는 경우 업로드한 본인의 파일만 다운로드 가능
			
			if (commonFileInfo.getUserId() == null || ! commonFileInfo.getUserId().equals(loginUserContext.getUserId())) {
				throw new InvalidParamException("Only the owner can download files that have not been saved yet.");
			}
		}
		
		boolean isInline = download == null || "false".equals(download) || "0".equals(download);
	
		res.sendRedirect(winiS3Client.getSignedDownloadUrl(commonFileInfo.getUrl(), commonFileInfo.getMimeType(), isInline, commonFileInfo.getFileName()).toString());
	}

	@ApiOperation(value = "다중 파일 다운로드")
	@PostMapping("/downloadMulti")
	public void downloadMulti(
			@ApiParam(value = "서비스명 (예) docs, code-qna, tech-blog, code-play)", required = true, defaultValue = "현재서비스명") @PathVariable("serviceName") String serviceName,
			@RequestBody CommonFileDto.DownloadMultiRequest request,
			HttpServletRequest req, HttpServletResponse res) throws IOException {
		List<UUID> fileIdList = new ArrayList<>();
		
		for (String signedFileId : request.getSignedFileIdList()) {
			commonFileService.checkSignedFileId(signedFileId);

			UUID fileId = commonFileService.getFileIdFromSignedFileId(signedFileId);
			
			fileIdList.add(fileId);
		}
		
		commonFileFacade.downloadMultiAsZipDirect(res, fileIdList, loginUserContext.getUserId(), request.getFileName());
	}

	@ApiOperation(value = "다중 파일 다운로드")
	@PostMapping("/downloadMultiForm")
	public void downloadMulti(
			@ApiParam(value = "서비스명 (예) docs, code-qna, tech-blog, code-play)", required = true, defaultValue = "현재서비스명") @PathVariable("serviceName") String serviceName,
			@ApiParam(value = "서명된 파일 ID", required = true) @RequestParam("signedFileIdList") List<String> signedFileIdList,
			@RequestParam("fileName") String fileName,
			HttpServletRequest req, HttpServletResponse res) throws IOException {
		List<UUID> fileIdList = new ArrayList<>();

		for (String signedFileId : signedFileIdList) {
			commonFileService.checkSignedFileId(signedFileId);

			UUID fileId = commonFileService.getFileIdFromSignedFileId(signedFileId);

			fileIdList.add(fileId);
		}

		commonFileFacade.downloadMultiAsZipDirect(res, fileIdList, loginUserContext.getUserId(), fileName);
	}

	@ApiOperation(value = "썸네일 이미지 미리보기")
	@GetMapping("/preview/{signedFileId}")
	public void thumbnail(
			@ApiParam(value = "서비스명 (예) docs, code-qna, tech-blog, code-play)", required = true, defaultValue = "현재서비스명") @PathVariable("serviceName") String serviceName,
			@ApiParam(value = "서명된 파일 ID", required = true) @PathVariable("signedFileId") String signedFileId,
			@ApiParam(value = "썸네일 이미지 너비") @RequestParam(value = "w", required = false) Integer width,
			@ApiParam(value = "썸네일 이미지 높이") @RequestParam(value = "h", required = false) Integer height,
			HttpServletRequest req, HttpServletResponse res) throws IOException {
		commonFileService.checkSignedFileId(signedFileId);

		UUID  fileId = commonFileService.getFileIdFromSignedFileId(signedFileId);
		CommonFileInfo commonFileInfo = commonFileFacade.searchCommonFileByIdWithDisabled(fileId);

		if (commonFileInfo.getStatus() == CommonFile.Status.DISABLE && commonFileInfo.getFileOrigin() != CommonFile.FileOrigin.WINI_EDITOR) {
			throw new InvalidParamException("The image is disabled");
		}

		if (! commonFileInfo.getMimeType().startsWith("image/")) {
			throw new InvalidParamException("Only image files can be previewed");
		}
		
		if (commonFileInfo.getWidth() != null && commonFileInfo.getWidth().equals(0)) {
			throw new InvalidParamException("Only image files can be previewed");
		}

		commonFileFacade.sendThumbImage(req, res, commonFileInfo, width, height);
	}
	
	@ApiOperation(value = "썸네일 이미지 미리보기 준비")
	@GetMapping("/preparePreview/{fileId}")
	public CommonResponse<CommonFileDto.PreparePreviewResponse> getSignedEditorImageUrl(
			@ApiParam(value = "서비스명 (예) docs, code-qna, tech-blog, code-play)", required = true, defaultValue = "현재서비스명") @PathVariable("serviceName") String serviceName,
			@ApiParam(value = "파일 ID", required = true) @PathVariable("fileId") UUID fileId,
			HttpServletRequest req, HttpServletResponse res) throws IOException {
		CommonFileInfo commonFileInfo;
		
		// TODO : 연관 리소스별 권한 체크(EntityName, EnttiyId)하고 무작정 보여주기 없애기

		commonFileInfo = commonFileFacade.searchCommonFileByIdWithDisabled(fileId);
		
		if (commonFileInfo.getStatus() == CommonFile.Status.DISABLE && commonFileInfo.getFileOrigin() != CommonFile.FileOrigin.WINI_EDITOR) {
			throw new InvalidParamException("The image is disabled");
		}
		
		// TODO : `commonFileInfo.getEntityName()`과 `commonFileInfo.getEntityId()`로 권한 체크

		if (! commonFileInfo.getMimeType().startsWith("image/")) {
			throw new InvalidParamException("Only image files can be previewed");
		}
		
		// TODO : 썸네일 이미지 생성 로직 추가

		CommonFileDto.PreparePreviewResponse response = CommonFileDto.PreparePreviewResponse.builder()
				.fileId(fileId)
				.signedFileId(commonFileService.getSignedFileId(fileId))
				.build();

		return CommonResponse.success(response);
	}

	@ApiOperation(value = "썸네일 이미지 미리보기 준비")
	@PostMapping("/preparePreview/")
	public CommonResponse<CommonFileDto.PreparePreviewListResponse> getSignedEditorImageUrl(
			@ApiParam(value = "서비스명 (예) docs, code-qna, tech-blog, code-play)", required = true, defaultValue = "현재서비스명") @PathVariable("serviceName") String serviceName,
			@ApiParam(value = "파일 ID", required = true) @RequestBody CommonFileDto.PreparePreviewListRequest request,
			HttpServletRequest req, HttpServletResponse res) throws IOException {
		List<CommonFileInfo> commonFileInfoList;
		
		// TODO : 연관 리소스별 권한 체크(EntityName, EnttiyId)하고 무작정 보여주기 없애기

		commonFileInfoList = commonFileFacade.searchCommonFileByIdListWithDisabled(request.getFileIdList());
		
		// TODO : `commonFileInfo.getEntityName()`과 `commonFileInfo.getEntityId()`로 권한 체크

		List<CommonFileDto.PreparePreviewResponse> preparedPreviewList = commonFileInfoList
				.stream()
				.filter(commonFileInfo -> {
					if (commonFileInfo.getStatus() == CommonFile.Status.DISABLE && commonFileInfo.getFileOrigin() != CommonFile.FileOrigin.WINI_EDITOR) {
						//비활성화된 파일이면서 WINI_EDITOR가 아니면 핕터링
						return false;
					}

					if (!commonFileInfo.getMimeType().startsWith("image/")) {
						// 이미지가 아니면 핕러
						return false;
					}
					
					return true;
				})
				.map(commonFileInfo -> {
					return CommonFileDto.PreparePreviewResponse.builder()
							.fileId(commonFileInfo.getId())
							.signedFileId(commonFileService.getSignedFileId(commonFileInfo.getId()))
							.build();
				})
				.collect(Collectors.toList());
		
		// TODO : 썸네일 이미지 생성 로직 추가
		
		CommonFileDto.PreparePreviewListResponse response = CommonFileDto.PreparePreviewListResponse.builder()
				.preparedPreviewList(preparedPreviewList)
				.build();

		return CommonResponse.success(response);
	}
}
