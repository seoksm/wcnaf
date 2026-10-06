package com.winitech.common.infrastructure.common;

import com.winitech.common.domain.common.CommonMenuAction;
import com.winitech.common.domain.common.CommonMenuActionCommand;
import com.winitech.common.domain.common.CommonMenuActionStore;
import com.winitech.common.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonMenuActionStoreImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 13:33
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonMenuActionStoreImpl implements CommonMenuActionStore {
	private final CommonMenuActionRepository commonMenuActionRepository;

	@Override
	public CommonMenuAction store(CommonMenuAction commonMenuAction) {
		return commonMenuActionRepository.save(commonMenuAction);
	}

	@Override
	public void storeAll(List<CommonMenuAction> toSaveList) {
		commonMenuActionRepository.saveAll(toSaveList);
	}

	@Override
	public CommonMenuAction modify(CommonMenuAction commonMenuAction, CommonMenuActionCommand command) {
		commonMenuAction.setMenuId(command.getMenuId());
		commonMenuAction.setProgramId(command.getProgramId());
		commonMenuAction.setProgramCode(command.getProgramCode());
		commonMenuAction.setActionType(command.getActionType());
		commonMenuAction.setAuthType(command.getAuthType());
		commonMenuAction.setUri(command.getUri());
		
		return commonMenuActionRepository.save(commonMenuAction);
	}

	@Override
	public void remove(UUID commonMenuActionId) {
		commonMenuActionRepository.deleteById(commonMenuActionId);
	}

	@Override
	public void removeAll(List<CommonMenuAction> toDeleteList) {
		commonMenuActionRepository.deleteAll(toDeleteList);
	}
}
