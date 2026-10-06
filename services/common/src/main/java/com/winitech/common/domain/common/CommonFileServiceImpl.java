package com.winitech.common.domain.common;

import com.winitech.common.exception.InvalidParamException;
import com.winitech.common.library.WiniFile;
import com.winitech.common.library.WiniSecurity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 공통 파일 서비스 구현체
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonFileServiceImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-13 17:46
 **/

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonFileServiceImpl extends EgovAbstractServiceImpl implements CommonFileService {

	private final CommonFileStore commonFileStore;
	private final CommonFileReader commonFileReader;
	
	@Override
	public CommonFileInfo registerCommonFile(CommonFileCommand commonFileCommand) {
		CommonFile commonFile = commonFileCommand.toEntity();
		commonFileStore.store(commonFile);
		return new CommonFileInfo(commonFile, getSignedFileId(commonFile.getId()));
	}

	@Override
	public CommonFileInfo modifyCommonFile(UUID id, CommonFileCommand commonFileCommand) {
		CommonFile modifyCommonFile = commonFileReader.getCommonFileById(id);
		modifyCommonFile.setEntityName(commonFileCommand.getEntityName());
		modifyCommonFile.setEntityId(commonFileCommand.getEntityId());
		modifyCommonFile.setSubKey(commonFileCommand.getSubKey());
		
		CommonFile commonFile = commonFileStore.store(modifyCommonFile);
		return new CommonFileInfo(commonFile, getSignedFileId(commonFile.getId()));
	}

	@Override
	public void removeCommonFile(UUID id) {
		CommonFile commonFile = commonFileReader.getCommonFileById(id);
		commonFile.disable();
		commonFileStore.store(commonFile);
	}

	@Override
	public CommonFileInfo searchCommonFileById(UUID id) {
		CommonFile commonFile = commonFileReader.getCommonFileById(id);
		return new CommonFileInfo(commonFile, getSignedFileId(commonFile.getId()));
	}

	@Override
	public CommonFileInfo searchCommonFileByIdWithDisabled(UUID id) {
		CommonFile commonFile = commonFileReader.getCommonFileByIdWithDisabled(id);
		return new CommonFileInfo(commonFile, getSignedFileId(commonFile.getId()));
	}

	@Override
	public List<CommonFileInfo> searchCommonFileByIdList(List<UUID> idList) {
		List<CommonFile> commonFileList = commonFileReader.getCommonFileByIdList(idList);

		return commonFileList
				.stream()
				.map((CommonFile commonFile) -> new CommonFileInfo(commonFile, getSignedFileId(commonFile.getId())))
				.collect(Collectors.toList());
	}

	@Override
	public List<CommonFileInfo> searchCommonFileByIdListWithDisabled(List<UUID> idList) {
		List<CommonFile> commonFileList = commonFileReader.getCommonFileByIdListWithDisabled(idList);

		return commonFileList
				.stream()
				.map((CommonFile commonFile) -> new CommonFileInfo(commonFile, getSignedFileId(commonFile.getId())))
				.collect(Collectors.toList());
	}

	@Override
	public List<CommonFileInfo> searchCommonFileListByEntity(String entityName, UUID entityId) {
		return searchCommonFileListByEntity(entityName, entityId, null);
	}

	@Override
	public List<CommonFileInfo> searchCommonFileListByEntity(String entityName, UUID entityId, String subKey) {
		List<CommonFile> commonFileList = commonFileReader.getCommonFileListByEntity(entityName, entityId, subKey);

		return commonFileList
				.stream()
				.map((CommonFile commonFile) -> new CommonFileInfo(commonFile, getSignedFileId(commonFile.getId())))
				.collect(Collectors.toList());
	}

	@Override
	public List<CommonFileInfo> searchCommonFileListByEntityList(String entityName, List<UUID> entityIdList) {
		return searchCommonFileListByEntityList(entityName, entityIdList, null);
	}

	@Override
	public List<CommonFileInfo> searchCommonFileListByEntityList(String entityName, List<UUID> entityIdList, String subKey) {
		List<CommonFile> commonFileList = commonFileReader.getCommonFileListByEntityList(entityName, entityIdList, subKey);

		return commonFileList
				.stream()
				.map((CommonFile commonFile) -> new CommonFileInfo(commonFile, getSignedFileId(commonFile.getId())))
				.collect(Collectors.toList());
	}

	@Override
	public void checkSignedFileId(String signedFileId) {
		WiniFile.checkSignedFileId(signedFileId);
	}

	@Override
	public UUID getFileIdFromSignedFileId(String signedFileId) {
		return WiniFile.getFileIdFromSignedFileId(signedFileId);
	}
	
	@Override
	public String getSignedFileId(UUID fileId) {
		return WiniFile.getSignedFileId(fileId);
	}
	
	@Override
	public String getSignedFileId(UUID fileId, Instant expiredAt) {
		return WiniFile.getSignedFileId(fileId, expiredAt);
	}

	@Override
	public void applyFileIdList(String entityName, UUID entityId, List<String> fileIds) {
		applyFileIdList(entityName, entityId, fileIds, null);
	}

	@Override
	public void applyFileIdList(String entityName, UUID entityId, List<String> fileIds, String subKey) {
		Set<String> fileIdSet = new HashSet(fileIds); 
		Map<String, CommonFile> existingFileMap = new HashMap<>(); 
		
		List<CommonFile> fileList = commonFileReader.getCommonFileListByEntity(entityName, entityId, subKey);
		
		// 삭제할 파일 목록 (기존에 DB에 있지만 적용할 fileId 목록에 없는 경우)
		List<CommonFile> toDeleteList = new ArrayList<>();

		for (CommonFile commonFile : fileList) {
			if (fileIdSet.contains(commonFile.getId().toString())) {
				// 기존에 DB에 있고 적용할 fileId 목록에 있는 경우
				existingFileMap.put(commonFile.getId().toString(), commonFile);
			} else {
				// 기존에 DB에 있지만 적용할 fileId 목록에 없는 경우 
				// 삭제할 목록에 추가
				toDeleteList.add(commonFile);
			}
		}
		
		// 파일 연결
		int fileSortSeq = 1;
		
		List<CommonFile> toSaveList = new ArrayList<>();
		Set<String> processedFileIdSet = new HashSet<>();
		
		for (String fileId : fileIds) {
			if (processedFileIdSet.contains(fileId)) {
				continue;
			}
			
			processedFileIdSet.add(fileId);
			
			CommonFile commonFile;
			
			if (existingFileMap.containsKey(fileId)) {
				// 기존에 있는 파일인 경우 순서만 변경
				
				commonFile = existingFileMap.get(fileId);
			} else {
				commonFile = commonFileReader.getCommonFileByIdWithDisabledIfExists(UUID.fromString(fileId));
				
				if (commonFile == null) {
					// 파일 업로드 정보가 없는 경우 무시
					continue;
				}

				if (commonFile.getEntityName() != null && !entityName.equals(commonFile.getEntityName())) {
					// throw new InvalidParamException("The file is already connected to another entity.");
					// 이미 연결고리가 있는 파일을 다시 연결하려 하였을때 일단 무시
					continue;
				} else if (commonFile.getEntityId() != null && !entityId.equals(commonFile.getEntityId())) {
					// throw new InvalidParamException("The file is already connected to the same entity.");
					// 이미 같은 연결고리가 있는 파일을 다시 연결하려 하였을때 일단 무시
					continue;
				}

				if (commonFile.getStatus() == CommonFile.Status.DISABLE) {
					// 삭제된 파일인 경우 활성화
					commonFile.enable();
				} else {
					// 새로 연결하는 경우
					commonFile.setEntityName(entityName);
					commonFile.setEntityId(entityId);
					commonFile.setSubKey(subKey);
				}
			}

			commonFile.setSortSeq(fileSortSeq);
			
			toSaveList.add(commonFile);
			
			fileSortSeq++;
		}
		
		// 파일 목록 저장
		commonFileStore.storeAll(toSaveList);
		
		// 삭제할 파일 목록 삭제
		commonFileStore.remove(toDeleteList);
	}

	@Override
	public void setImageSize(UUID fileId, Integer width, Integer height) {
		CommonFile commonFile = commonFileReader.getCommonFileById(fileId);
		commonFile.setWidth(width);
		commonFile.setHeight(height);
		commonFileStore.store(commonFile);
	}
}
