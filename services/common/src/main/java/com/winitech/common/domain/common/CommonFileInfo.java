package com.winitech.common.domain.common;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.Getter;

import javax.persistence.Column;

/**
 * 공통 파일 정보
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonFileInfo.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-13 17:31
 **/
@Getter
public class CommonFileInfo {
	private final UUID id;
	private final String fileName;
	private final String fileExt;
	private final Long fileSize;
	private final String mimeType;
	
	private final CommonFile.FileOrigin fileOrigin;
	private final CommonFile.StorageType storageType;
	private final String url;
	private final UUID userId;
	private final String entityName;
	
	private final UUID entityId;
	
	private final Integer width;
	private final Integer height;
	
	private final String subKey;
	
	private final String signedFileId;
	
	private final CommonFile.Status status;
	
	public String getSignedDownloadUrl() {
		return "/api/v1/common/commonFile/download/" + signedFileId;
	}
	
	public CommonFileInfo(CommonFile commonFile, String signedFileId) {
		this.id = commonFile.getId();
		this.fileName = commonFile.getFileName();
		this.fileExt = commonFile.getFileExt();
		this.fileSize = commonFile.getFileSize();
		this.mimeType = commonFile.getMimeType();

		this.fileOrigin = commonFile.getFileOrigin();
		this.storageType = commonFile.getStorageType();
		this.url = commonFile.getUrl();
		this.userId = commonFile.getUserId();
		this.entityName = commonFile.getEntityName();
		
		this.entityId = commonFile.getEntityId();
		
		this.width = commonFile.getWidth();
		this.height = commonFile.getHeight();
		
		this.subKey = commonFile.getSubKey();
		
		this.signedFileId = signedFileId;
		
		this.status = commonFile.getStatus();
	}
}
