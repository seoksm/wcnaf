package com.winitech.common.domain.common;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * 공통 파일 커맨드 
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonFileCommand.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-13 16:45
 **/
@Getter
@Builder
@ToString
public class CommonFileCommand {
	private final UUID id;
	private final String fileName;
	private final String fileExt;
	private final Long fileSize;
	private final String mimeType;
	private final CommonFile.FileOrigin fileOrigin; 
	private final String url;
	private final UUID userId;
	private final String entityName;
	private final UUID entityId;
	private final String subKey;
	private final String extraInfo;
	private final Integer width;
	private final Integer height;

	public CommonFile toEntity() {
		return CommonFile.builder()
				.id(id)
				.fileName(fileName)
				.fileExt(fileExt)
				.fileSize(fileSize)
				.mimeType(mimeType)
				.fileOrigin(fileOrigin == null ? CommonFile.FileOrigin.ETC : fileOrigin)
				.storageType(CommonFile.StorageType.S3)
				.url(url)
				.userId(userId)
				.entityName(entityName)
				.entityId(entityId)
				.subKey(subKey)
				.extraInfo(extraInfo)
				.width(width)
				.height(height)
				.build();
	}
}
