package com.winitech.common.infrastructure.commonJobGroup;

import com.winitech.common.domain.commonJobGroup.CommonJobGroup;
import com.winitech.common.domain.commonJobGroup.CommonJobGroupCommand;
import com.winitech.common.domain.commonJobGroup.CommonJobGroupReader;
import com.winitech.common.domain.commonJobGroup.CommonJobGroupStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.commonJobGroup
 * └ CommonJobGroup.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:36
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonJobGroupStoreImpl implements CommonJobGroupStore {
	private final CommonJobGroupRepository commonJobGroupRepository;
	private final CommonJobGroupReader commonJobGroupReader;

	@Override
	public CommonJobGroup store(CommonJobGroup commonJobGroup) {
		return commonJobGroupRepository.save(commonJobGroup);
	}

	@Override
	public CommonJobGroup modify(CommonJobGroup commonJobGroup, CommonJobGroupCommand.ModifyRequestCommand command) {
		commonJobGroup.setName(command.getName());
		commonJobGroup.setRemark(command.getRemark());
		commonJobGroup.setStatus(command.getStatus());

		return commonJobGroupRepository.save(commonJobGroup);
	}

	@Override
	public void remove(UUID commonJobGroupId) {
		CommonJobGroup commonJobGroup = commonJobGroupReader.getCommonJobGroupById(commonJobGroupId);
		commonJobGroup.disable();
		commonJobGroupRepository.save(commonJobGroup);
	}
}
