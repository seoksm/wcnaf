package com.winitech.common.domain.common;import java.util.List;
import java.util.UUID;

/**
 * 공통 파일 리더
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonFileReader.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-13 17:33
 **/
public interface CommonFileReader {
    CommonFile getCommonFileById(UUID id);
    CommonFile getCommonFileByIdWithDisabled(UUID id);
    CommonFile getCommonFileByIdIfExists(UUID id);
    CommonFile getCommonFileByIdWithDisabledIfExists(UUID id);
    List<CommonFile> getCommonFileByIdList(List<UUID> idList);
    List<CommonFile> getCommonFileByIdListWithDisabled(List<UUID> idList);
    List<CommonFile> getCommonFileListByEntity(String entityName, UUID entityId);
    List<CommonFile> getCommonFileListByEntity(String entityName, UUID entityId, String subKey);
    List<CommonFile> getCommonFileListByEntityList(String entityName, List<UUID> entityIdList);
    List<CommonFile> getCommonFileListByEntityList(String entityName, List<UUID> entityIdList, String subKey);
}