package com.winitech.common.infrastructure.common;

import com.winitech.common.domain.common.CommonFile;
import com.winitech.common.domain.common.CommonFileCommand;
import com.winitech.common.domain.common.CommonFileStore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonFileStoreImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-13 17:49
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonFileStoreImpl implements CommonFileStore {
	private final CommonFileRepository commonFileRepository;

	@Override
	public CommonFile store(CommonFile commonFile) {
		return commonFileRepository.save(commonFile);
	}

	@Override
	public List<CommonFile> storeAll(List<CommonFile> commonFileList) {
		return commonFileRepository.saveAll(commonFileList);
	}

	@Override
	public CommonFile modify(CommonFile commonFile, CommonFileCommand command) {
		commonFile.setEntityName(command.getEntityName());
		commonFile.setEntityId(command.getEntityId());

		return commonFileRepository.save(commonFile);
	}

	@Override
	public void remove(CommonFile commonFile) {
		commonFile.disable();
		
		commonFileRepository.save(commonFile);
	}

	@Override
	public void remove(List<CommonFile> commonFileList) {
		commonFileList.forEach(CommonFile::disable);
		
		commonFileRepository.saveAll(commonFileList);
	}
}
