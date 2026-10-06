package com.winitech.common.infrastructure.common;

import com.winitech.common.domain.common.CommonFile;
import com.winitech.common.domain.common.CommonFileReader;
import com.winitech.common.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonFileReaderImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-13 17:49
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonFileReaderImpl implements CommonFileReader {
	private final CommonFileRepository commonFileRepository;

	@Override
	public CommonFile getCommonFileById(UUID id) {
		CommonFile probe = new CommonFile();
		probe.setId(id);
		probe.setStatus(CommonFile.Status.ENABLE);
		return commonFileRepository.findOne(Example.of(probe)).orElseThrow(EntityNotFoundException::new);
	}
	
	@Override
	public CommonFile getCommonFileByIdWithDisabled(UUID id) {
		return commonFileRepository.findById(id).orElseThrow(EntityNotFoundException::new);
	}
	
	@Override
	public CommonFile getCommonFileByIdIfExists(UUID id) {
		CommonFile probe = new CommonFile();
		probe.setId(id);
		probe.setStatus(CommonFile.Status.ENABLE);
		return commonFileRepository.findOne(Example.of(probe)).orElse(null);
	}
	
	@Override
	public CommonFile getCommonFileByIdWithDisabledIfExists(UUID id) {
		return commonFileRepository.findById(id).orElse(null);
	}

	@Override
	public List<CommonFile> getCommonFileByIdList(List<UUID> idList) {
		return commonFileRepository.findByIdListAndStatus(idList, CommonFile.Status.ENABLE);
	}
	
	@Override
	public List<CommonFile> getCommonFileByIdListWithDisabled(List<UUID> idList) {
		return commonFileRepository.findAllById(idList);
	}

	@Override
	public List<CommonFile> getCommonFileListByEntity(String entityName, UUID entityId) {
		return commonFileRepository.findByEntityAndStatus(entityName, entityId, null, CommonFile.Status.ENABLE);
	}

	@Override
	public List<CommonFile> getCommonFileListByEntity(String entityName, UUID entityId, String subKey) {
		return commonFileRepository.findByEntityAndStatus(entityName, entityId, subKey, CommonFile.Status.ENABLE);
	}

	@Override
	public List<CommonFile> getCommonFileListByEntityList(String entityName, List<UUID> entityIdList) {
		return commonFileRepository.findByEntityListAndStatus(entityName, entityIdList, null, CommonFile.Status.ENABLE);
	}
	
	@Override
	public List<CommonFile> getCommonFileListByEntityList(String entityName, List<UUID> entityIdList, String subKey) {
		return commonFileRepository.findByEntityListAndStatus(entityName, entityIdList, subKey, CommonFile.Status.ENABLE);
	}
}
