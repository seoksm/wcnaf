package com.winitech.common.infrastructure.common;

import com.winitech.common.domain.common.CommonMenuAction;
import com.winitech.common.domain.common.CommonMenuActionReader;
import com.winitech.common.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonMenuActionReaderImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 13:33
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonMenuActionReaderImpl implements CommonMenuActionReader {
	private final CommonMenuActionRepository commonMenuActionRepository;

	@Override
	public CommonMenuAction getCommonMenuActionById(UUID id) {
		return commonMenuActionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public CommonMenuAction getCommonMenuActionByIdIfExists(UUID id) {
		return commonMenuActionRepository.findById(id).orElseThrow(null);
	}

//	@Override
//	public List<CommonMenuAction> getCommonMenuActionByName(String name) {
//		return List.of();
//	}

	@Override
	public List<CommonMenuAction> getAllCommonMenuAction() {
		return commonMenuActionRepository.findAll();
	}

	@Override
	public boolean isExistCommonMenuActionById(UUID id) {
		return commonMenuActionRepository.existsById(id);
	}

	@Override
	public List<CommonMenuAction> getListByProgramIdList(List<UUID> programIdList) {
		return commonMenuActionRepository.findByProgramIdIn(programIdList);
	}
}
