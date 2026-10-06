package com.winitech.common.domain.common;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonFileService.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-13 17:42
 **/
public interface CommonFileService {
	CommonFileInfo registerCommonFile(CommonFileCommand commonFileCommand);
	CommonFileInfo modifyCommonFile(UUID id, CommonFileCommand commonFileCommand);
	void removeCommonFile(UUID id);
	CommonFileInfo searchCommonFileById(UUID id);
	CommonFileInfo searchCommonFileByIdWithDisabled(UUID id);
	List<CommonFileInfo> searchCommonFileByIdList(List<UUID> idList);
	List<CommonFileInfo> searchCommonFileByIdListWithDisabled(List<UUID> idList);
	List<CommonFileInfo> searchCommonFileListByEntity(String entityName, UUID entityId);
	List<CommonFileInfo> searchCommonFileListByEntity(String entityName, UUID entityId, String subKey);
	List<CommonFileInfo> searchCommonFileListByEntityList(String entityName, List<UUID> entityIdList);
	List<CommonFileInfo> searchCommonFileListByEntityList(String entityName, List<UUID> entityIdList, String subKey);

	void checkSignedFileId(String signedFileId);
	UUID getFileIdFromSignedFileId(String signedFileId);
	String getSignedFileId(UUID fileId);
	String getSignedFileId(UUID fileId, Instant expiredAt);

	void applyFileIdList(String entityName, UUID entityId, List<String> fileIds);
	void applyFileIdList(String entityName, UUID entityId, List<String> fileIds, String subKey);

	void setImageSize(UUID fileId, Integer width, Integer height);
}
