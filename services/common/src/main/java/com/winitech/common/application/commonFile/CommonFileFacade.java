package com.winitech.common.application.commonFile;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.winitech.common.domain.common.CommonFileCommand;
import com.winitech.common.domain.common.CommonFileInfo;
import com.winitech.common.domain.common.CommonFileService;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.common.interfaces.inboundAdapter.web.CommonFileDto;
import com.winitech.common.library.WiniCom;
import com.winitech.common.library.WiniFile;
import com.winitech.common.library.core.WiniS3Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import net.coobird.thumbnailator.resizers.configurations.Rendering;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 공통 파일 파사드
 * <pre>
 * com.winitech.common.application.commonFile
 * └ CommonFileFacade.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-16 09:16
 **/
@Slf4j
@Service
//@RequiredArgsConstructor
public class CommonFileFacade {
	private final CommonFileService commonFileService;
	private final WiniS3Client winiS3Client;
	private final String tmpZipDir;
	private final String imageCacheDir;

	private Object thumbnailLock = new Object();
	private int[] thumbnailSize = { 1024, 512, 256, 128, 64, 48, 32 };

    public CommonFileFacade(
			CommonFileService commonFileService,
			WiniS3Client winiS3Client,
			@Value("${winitech.file.tmp-zip-dir:}") String tmpZipDir,
			@Value("${winitech.file.cache.image-dir}") String imageCacheDir
	) {
        this.commonFileService = commonFileService;
        this.winiS3Client = winiS3Client;
		this.tmpZipDir = tmpZipDir;
		this.imageCacheDir = imageCacheDir;
    }

    public CommonFileInfo registerCommonFile(CommonFileCommand commonFileCommand) {
		return commonFileService.registerCommonFile(commonFileCommand);
	}

	public CommonFileInfo modifyCommonFile(UUID id, CommonFileCommand commonFileCommand) {
		return commonFileService.modifyCommonFile(id, commonFileCommand);
	}

	public void removeCommonFile(UUID id) {
		commonFileService.removeCommonFile(id);
	}

	public CommonFileInfo searchCommonFileById(UUID id) {
		return commonFileService.searchCommonFileById(id);
	}

	public CommonFileInfo searchCommonFileByIdWithDisabled(UUID id) {
		return commonFileService.searchCommonFileByIdWithDisabled(id);
	}

	public List<CommonFileInfo> searchCommonFileByIdList(List<UUID> idList) {
		return commonFileService.searchCommonFileByIdList(idList);
	}

	public List<CommonFileInfo> searchCommonFileByIdListWithDisabled(List<UUID> idList) {
		return commonFileService.searchCommonFileByIdListWithDisabled(idList);
	}

	public List<CommonFileInfo> searchCommonFileListByEntity(String entityName, UUID entityId) {
		return commonFileService.searchCommonFileListByEntity(entityName, entityId);
	}

	public List<CommonFileInfo> searchCommonFileListByEntity(Class entityClass, UUID entityId) {
		return commonFileService.searchCommonFileListByEntity(getEntityName(entityClass), entityId);
	}
	
	public List<CommonFileInfo> searchCommonFileListByEntity(String entityName, UUID entityId, String subKey) {
		return commonFileService.searchCommonFileListByEntity(entityName, entityId, subKey);
	}

	public List<CommonFileInfo> searchCommonFileListByEntity(Class entityClass, UUID entityId, String subKey) {
		return commonFileService.searchCommonFileListByEntity(getEntityName(entityClass), entityId, subKey);
	}
	
	public List<CommonFileDto.FileInfoResponse> searchCommonFileDtoListByEntity(String entityName, UUID entityId) {
		return CommonFileDto.FileInfoResponse.from(commonFileService.searchCommonFileListByEntity(entityName, entityId));
	}

	public List<CommonFileDto.FileInfoResponse> searchCommonFileDtoListByEntity(Class entityClass, UUID entityId) {
		return  CommonFileDto.FileInfoResponse.from(commonFileService.searchCommonFileListByEntity(getEntityName(entityClass), entityId));
	}
	
	public List<CommonFileDto.FileInfoResponse> searchCommonFileDtoListByEntity(String entityName, UUID entityId, String subKey) {
		return  CommonFileDto.FileInfoResponse.from(commonFileService.searchCommonFileListByEntity(entityName, entityId, subKey));
	}

	public List<CommonFileDto.FileInfoResponse> searchCommonFileDtoListByEntity(Class entityClass, UUID entityId, String subKey) {
		return CommonFileDto.FileInfoResponse.from(commonFileService.searchCommonFileListByEntity(getEntityName(entityClass), entityId, subKey));
	}
	
	public List<CommonFileInfo> searchCommonFileListByEntityList(String entityName, List<UUID> entityIdList) {
		return commonFileService.searchCommonFileListByEntityList(entityName, entityIdList);
	}
	
	public List<CommonFileInfo> searchCommonFileListByEntityList(Class entityClass, List<UUID> entityIdList) {
		return commonFileService.searchCommonFileListByEntityList(getEntityName(entityClass), entityIdList);
	}
	
	public List<CommonFileInfo> searchCommonFileListByEntityList(String entityName, List<UUID> entityIdList, String subKey) {
		return commonFileService.searchCommonFileListByEntityList(entityName, entityIdList, subKey);
	}
	
	public List<CommonFileInfo> searchCommonFileListByEntityList(Class entityClass, List<UUID> entityIdList, String subKey) {
		return commonFileService.searchCommonFileListByEntityList(getEntityName(entityClass), entityIdList, subKey);
	}
	
	public Map<UUID, List<CommonFileInfo>> searchCommonFileListMapByEntityList(Class entityClass, List<UUID> entityIdList) {
		return searchCommonFileListMapByEntityList(getEntityName(entityClass), entityIdList);
	}

	public Map<UUID, List<CommonFileInfo>> searchCommonFileListMapByEntityList(String entityName, List<UUID> entityIdList) {
		return commonFileService.searchCommonFileListByEntityList(entityName, entityIdList)
				.stream()
				.collect(Collectors.groupingBy(CommonFileInfo::getEntityId));
	}

	public Map<UUID, List<CommonFileInfo>> searchCommonFileListMapByEntityList(Class entityClass, List<UUID> entityIdList, String subKey) {
		return searchCommonFileListMapByEntityList(getEntityName(entityClass), entityIdList, subKey);
	}

	public Map<UUID, List<CommonFileInfo>> searchCommonFileListMapByEntityList(String entityName, List<UUID> entityIdList, String subKey) {
		return commonFileService.searchCommonFileListByEntityList(entityName, entityIdList, subKey)
				.stream()
				.collect(Collectors.groupingBy(CommonFileInfo::getEntityId));
	}

	public Map<UUID, List<CommonFileDto.FileInfoResponse>> searchCommonFileDtoListMapByEntityList(Class entityClass, List<UUID> entityIdList) {
		return searchCommonFileDtoListMapByEntityList(getEntityName(entityClass), entityIdList);
	}

	public Map<UUID, List<CommonFileDto.FileInfoResponse>> searchCommonFileDtoListMapByEntityList(String entityName, List<UUID> entityIdList) {
		return commonFileService.searchCommonFileListByEntityList(entityName, entityIdList)
				.stream()
				.collect(Collectors.groupingBy(CommonFileInfo::getEntityId, Collectors.mapping(CommonFileDto.FileInfoResponse::new, Collectors.toList())));
	}

	public Map<UUID, List<CommonFileDto.FileInfoResponse>> searchCommonFileDtoListMapByEntityList(Class entityClass, List<UUID> entityIdList, String subKey) {
		return searchCommonFileDtoListMapByEntityList(getEntityName(entityClass), entityIdList, subKey);
	}

	public Map<UUID, List<CommonFileDto.FileInfoResponse>> searchCommonFileDtoListMapByEntityList(String entityName, List<UUID> entityIdList, String subKey) {
		return commonFileService.searchCommonFileListByEntityList(entityName, entityIdList, subKey)
				.stream()
				.collect(Collectors.groupingBy(CommonFileInfo::getEntityId, Collectors.mapping(CommonFileDto.FileInfoResponse::new, Collectors.toList())));
	}

	/**
	 * 임시 zip 파일을 만들고 다운로드
	 * @param res HttpServletResponse
	 * @param fileIdList 파일 ID 목록
	 * @param userId 사용자 ID
	 * @throws IOException
	 */
	public void downloadMultiAsZip(HttpServletResponse res, List<UUID> fileIdList, UUID userId, String fileName) throws IOException {
		// TODO : 다운로드된 파일 캐시 처리
		// TODO : 생성된 ZIP 파일 캐시 처리
		// TODO : 큐방식으로 처리

		String tmpZipFilePath = prepareDownloadMultiAsZip(fileIdList, userId);
		
		File zipFile = new File(tmpZipFilePath);
		
		try {
			try (FileInputStream is = new FileInputStream(zipFile)) {
				String encodedFileName = URLEncoder.encode(WiniFile.getFileNameWithoutExtension(fileName) + "_" + UUID.randomUUID() + ".zip", StandardCharsets.UTF_8).replace("+", "%20");

				res.setContentType("application/zip");
				res.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName);
				res.setHeader("Content-Length", String.valueOf(zipFile.length()));

				OutputStream os = res.getOutputStream();

				WiniCom.copyStream(is, os);

				os.flush();
			}
		} finally {
			Files.deleteIfExists(zipFile.toPath());
		}
	}
	
	/**
	 * 임시 파일을 만들지 않고 여러 파일을 Zip으로 묶어서 다운로드
	 * 다운로드 중간에 오류났을때 처리가 곤린함
	 * @param res HttpServletResponse
	 * @param fileIdList 파일 ID 목록
	 * @param userId 사용자 ID
	 * @throws IOException 
	 */
	public void downloadMultiAsZipDirect(HttpServletResponse res, List<UUID> fileIdList, UUID userId, String fileName) throws IOException {
		List<CommonFileInfo> commonFileInfoList = new ArrayList<>();

		if (tmpZipDir == null || tmpZipDir.isEmpty()) {
			throw new InvalidParamException("The temporary directory for creating the zip file is not set.");
		}
		
		long totalFileSize = 0;
		int fileCount = 0;
		
		// 파일 다운로드 가능성 체크
		for (UUID fileId : fileIdList) {
			CommonFileInfo commonFileInfo = searchCommonFileById(fileId);

			if (commonFileInfo.getEntityName() == null || commonFileInfo.getEntityId() == null) {
				// 연결고리가 없는 경우 업로드한 본인의 파일만 다운로드 가능

				if (commonFileInfo.getUserId() == null || ! commonFileInfo.getUserId().equals(userId)) {
					throw new InvalidParamException("Only the owner can download files that have not been saved yet.");
				}
			}
			
			totalFileSize += commonFileInfo.getFileSize();
			fileCount++;

			commonFileInfoList.add(commonFileInfo);
		}
		
		if (totalFileSize > WiniFile.getAllowedMultiDownloadMaxSize()) {
			throw new InvalidParamException("The total file size exceeds the maximum download size.");
		}
		
		if (fileCount > WiniFile.getAllowedMultiDownloadMaxCount()) {
			throw new InvalidParamException("The number of files exceeds the maximum download count.");
		}
		
		String encodedFileName = URLEncoder.encode(WiniFile.getFileNameWithoutExtension(fileName) + "_" + UUID.randomUUID() + ".zip", StandardCharsets.UTF_8).replace("+", "%20");

		// 파일 다운로드 및 zip 파일 생성 (스트리밍 방식으로 다운로드 시킬수도 있지만 서버에서 생성하는것으로 함)
		res.setContentType("application/zip");
		res.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName);

		OutputStream os = res.getOutputStream();

		try (ZipOutputStream zipOutputStream = new ZipOutputStream(os)) {
			for (CommonFileInfo commonFileInfo : commonFileInfoList) {
				ZipEntry zipEntry = new ZipEntry(commonFileInfo.getFileName());
				zipOutputStream.putNextEntry(zipEntry);

				try {
					try (InputStream s3InputStream = winiS3Client.directDownload(commonFileInfo.getUrl())) {
						WiniCom.copyStream(s3InputStream, zipOutputStream);						
					}
				} catch (IOException ex) {
					zipOutputStream.write("An error occurred while preparing the file.".getBytes(StandardCharsets.UTF_8));
				} catch (AmazonS3Exception ex) {
					zipOutputStream.write("An error occurred while preparing the file (2)".getBytes(StandardCharsets.UTF_8));
				}

				zipOutputStream.closeEntry();
			}
		}

		os.flush();
	}

	/**
	 * 임시 zip 파일을 만들기
	 * @param fileIdList 파일 ID 목록
	 * @param userId 사용자 ID
	 * @return 임시 zip 파일 경로
	 * @throws IOException
	 */
	public String prepareDownloadMultiAsZip(List<UUID> fileIdList, UUID userId) throws IOException {
		List<CommonFileInfo> commonFileInfoList = new ArrayList<>();

		if (tmpZipDir == null || tmpZipDir.isEmpty()) {
			throw new InvalidParamException("The temporary directory for creating the zip file is not set.");
		}

		// TODO : 다운로드된 파일 캐시 처리

		long totalFileSize = 0;
		int fileCount = 0;

		// 파일 다운로드 가능성 체크
		for (UUID fileId : fileIdList) {
			CommonFileInfo commonFileInfo = searchCommonFileById(fileId);

			if (commonFileInfo.getEntityName() == null || commonFileInfo.getEntityId() == null) {
				// 연결고리가 없는 경우 업로드한 본인의 파일만 다운로드 가능

				if (commonFileInfo.getUserId() == null || ! commonFileInfo.getUserId().equals(userId)) {
					throw new InvalidParamException("Only the owner can download files that have not been saved yet.");
				}
			}

			totalFileSize += commonFileInfo.getFileSize();
			fileCount++;

			commonFileInfoList.add(commonFileInfo);
		}

		if (totalFileSize > WiniFile.getAllowedMultiDownloadMaxSize()) {
			throw new InvalidParamException("The total file size exceeds the maximum download size.");
		}

		if (fileCount > WiniFile.getAllowedMultiDownloadMaxCount()) {
			throw new InvalidParamException("The number of files exceeds the maximum download count.");
		}

		UUID uuid = UUID.randomUUID();
		String zipFileName = uuid.toString();

		String zipDir = tmpZipDir.endsWith("/") ? tmpZipDir : tmpZipDir + "/";

		String tmpZipFilePath = zipDir + zipFileName + ".zip";

		File zipFile = new File(tmpZipFilePath);
		zipFile.deleteOnExit();

		try {
			// 파일 다운로드 및 zip 파일 생성 (스트리밍 방식으로 다운로드 시킬수도 있지만 서버에서 생성하는것으로 함)
			try (FileOutputStream zipFileOutputStream = new FileOutputStream(zipFile);
				 ZipOutputStream zipOutputStream = new ZipOutputStream(zipFileOutputStream)) {

				for (CommonFileInfo commonFileInfo : commonFileInfoList) {
					ZipEntry zipEntry = new ZipEntry(commonFileInfo.getFileName());
					zipOutputStream.putNextEntry(zipEntry);

					try {
						try (InputStream s3InputStream = winiS3Client.directDownload(commonFileInfo.getUrl())) {
							WiniCom.copyStream(s3InputStream, zipOutputStream);
						}
					} catch (IOException ex) {
						zipOutputStream.write("An error occurred while preparing the file.".getBytes(StandardCharsets.UTF_8));
					} catch (AmazonS3Exception ex) {
						zipOutputStream.write("An error occurred while preparing the file (2)".getBytes(StandardCharsets.UTF_8));
					}

					zipOutputStream.closeEntry();
				}
			}
		} catch (IOException ex) {
			if (zipFile.exists()) {
				zipFile.delete();
			}
		}

		return tmpZipFilePath;
	}

	public void applyFileIdList(Class entityClass, UUID entityId, List<String> fileIds) {
		applyFileIdList(getEntityName(entityClass), entityId, fileIds);
	}

	public void applyFileIdList(String entityName, UUID entityId, List<String> fileIds) {
		commonFileService.applyFileIdList(entityName, entityId, fileIds);
	}

	public void applyFileIdList(Class entityClass, UUID entityId, List<String> fileIds, String subKey) {
		applyFileIdList(getEntityName(entityClass), entityId, fileIds, subKey);
	}

	public void applyFileIdList(String entityName, UUID entityId, List<String> fileIds, String subKey) {
		commonFileService.applyFileIdList(entityName, entityId, fileIds, subKey);
	}

	private static String getEntityName(Class entityClass) {
		return entityClass.getSimpleName().toLowerCase();
	}

	public void setImageSize(UUID fileId, Integer width, Integer height) {
		commonFileService.setImageSize(fileId, width, height);
	}

	public void sendThumbImage(HttpServletRequest req, HttpServletResponse res, CommonFileInfo commonFileInfo, Integer width, Integer height) throws IOException {
		boolean hasWidth = width != null && width > 0;
		boolean hasHeight = height != null && height > 0;

		if (hasWidth || hasHeight) {
			Integer imageWidth = commonFileInfo.getWidth();
			Integer imageHeight = commonFileInfo.getHeight();

			byte[] imageBytes = null;

			if (imageWidth == null || imageHeight == null) {
				// 이미지 크기를 알 수 없는 경우 이미지 크기를 실제 파일을 다운받아서 계산

				imageBytes = winiS3Client.directDownloadBytes(commonFileInfo.getUrl());

				try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
					BufferedImage image = ImageIO.read(inputStream);

					if (image == null) {
						// 이미지가 아닌 경우 0x0 크기로 저장
						imageWidth = 0;
						imageHeight = 0;
					} else {
						imageWidth = image.getWidth();
						imageHeight = image.getHeight();
					}

					setImageSize(commonFileInfo.getId(), imageWidth, imageHeight);
				}
			}

			if (! hasWidth) {
				// 너비가 없는 경우 높이를 기준으로 비율 계산
				width = (int) (imageWidth * (height / (double) imageHeight));
			} else if (! hasHeight) {
				// 높이가 없는 경우 너비를 기준으로 비율 계산
				height = (int) (imageHeight * (width / (double) imageWidth));
			}

			boolean isSquareThumbnail = width.equals(height);

			double thumbnailRatio = (double) width / height;

			int thumbnailSizeIndex = -1;
			for (int i = 0; i < thumbnailSize.length; i++) {
				if (thumbnailSize[i] < width || thumbnailSize[i] < height) {
					break;
				}

				thumbnailSizeIndex++;
			}

			if (thumbnailSizeIndex < 0) {
				thumbnailSizeIndex = 0;
			}

			width = (int) Math.round(thumbnailSize[thumbnailSizeIndex] * thumbnailRatio);
			height = thumbnailSize[thumbnailSizeIndex];

//			System.out.println("thumbnailSizeIndex: " + thumbnailSizeIndex + ", width: " + width + ", height: " + height);

			if (imageWidth > width || imageHeight > height) {
				// 썸네일 생성이 필요한 경우에만 생성
				String outputFormat = "png";
				int imageType = BufferedImage.TYPE_INT_ARGB;

				if (commonFileInfo.getMimeType().equals("image/jpeg")) {
					outputFormat = "jpg";
					imageType = BufferedImage.TYPE_INT_RGB;
				}

				String cacheFileName = commonFileInfo.getId() + "_" + width + "x" + height + "." + outputFormat;
				String cacheDir = imageCacheDir + "/" + cacheFileName.substring(5, 7) + "/";
				String cacheFilePath = cacheDir + cacheFileName;

				String etag = "W/" + commonFileInfo.getId() + "-" + width + "-" + height;

				// 캐시 체크
				if (StringUtils.hasText(req.getHeader("If-None-Match")) && req.getHeader("If-None-Match").equals(etag)) {
					res.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
					return;
				}

				if (! Paths.get(cacheFilePath).toFile().exists()) {
					// 캐시 파일이 없는 경우 캐시 파일 생성

					synchronized (thumbnailLock) {
						// 썸네일이 여러 쓰레드에서 생성되는 것을 막기위해 락을 걸고 작업

						if (! Paths.get(cacheFilePath).toFile().exists()) {
							// cacheDir이 없는지 체크하고, 없는 경우 디렉터리 생성
							if (!Paths.get(cacheDir).toFile().exists()) {
								Paths.get(cacheDir).toFile().mkdirs();
							}

							if (imageBytes == null) {
								imageBytes = winiS3Client.directDownloadBytes(commonFileInfo.getUrl());
							}

							try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
								Thumbnails.Builder<? extends InputStream> builder = Thumbnails.of(inputStream)
										.size(width, height)
										.outputFormat(outputFormat)
										.imageType(imageType)
										.rendering(Rendering.QUALITY)
										.useExifOrientation(true);// 자동 이미지 회전

								if (isSquareThumbnail) {
									builder.crop(Positions.CENTER);
								}

								builder.toFile(cacheFilePath);
							}

							// TODO: 생성된 썸네일 자동 정리
							// TODO: 썸네일 생성을 별도 서비스로 분리?
						}
					}
				}

				res.setHeader("ETag", etag);
				res.setDateHeader("Expires", System.currentTimeMillis() + 1000 * 60 * 60);

				sendCacheFile(req, res, commonFileInfo, cacheFilePath);

				return;
			}
		}

		res.sendRedirect(winiS3Client.getSignedDownloadUrl(commonFileInfo.getUrl(), commonFileInfo.getMimeType(), true, commonFileInfo.getFileName()).toString());
	}

	private void sendCacheFile(HttpServletRequest req, HttpServletResponse res, CommonFileInfo commonFileInfo, String cacheFilePath) throws IOException {
		try (InputStream cacheInputStream = new FileInputStream(new File(cacheFilePath))) {
			res.setContentType(commonFileInfo.getMimeType());
			WiniFile.setDispositionInline(commonFileInfo.getFileName(), req, res);
			WiniCom.copyStream(cacheInputStream, res.getOutputStream());
			res.flushBuffer();
		}
	}
}
