package com.winitech.common.domain.commonJobGroup;

import com.winitech.common.exception.EntityNotFoundException;
import com.winitech.common.exception.IllegalStatusException;
import com.winitech.common.library.commonType.WiniPageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.domain.commonJobGroup
 * └ CommonJobGroup.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:36
 **/
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommonJobGroupServiceImpl extends EgovAbstractServiceImpl implements CommonJobGroupService {
	private final CommonJobGroupStore commonJobGroupStore;
	private final CommonJobGroupReader commonJobGroupReader;

	@Override
	public CommonJobGroupInfo registerCommonJobGroup(CommonJobGroupCommand.RegisterRequestCommand commonJobGroupCommand) {
		if (commonJobGroupCommand.getName() != null && commonJobGroupReader.existCommonJobGroupByNameAndExcludingSelf(commonJobGroupCommand.getName(), UUID.randomUUID())) {
			throw new IllegalStatusException("Already registered CommonJobGroup code.");
		}

		CommonJobGroup initCommonJobGroup = CommonJobGroup.builder()
				.name(commonJobGroupCommand.getName())
				.remark(commonJobGroupCommand.getRemark())
				.status(commonJobGroupCommand.getStatus())

				.systemStatus(CommonJobGroup.SystemStatus.ENABLE)
				.build();

		CommonJobGroup commonJobGroup = commonJobGroupStore.store(initCommonJobGroup);
		return new CommonJobGroupInfo(commonJobGroup);
	}

	@Override
	public CommonJobGroupInfo modifyCommonJobGroup(UUID id, CommonJobGroupCommand.ModifyRequestCommand commonJobGroupCommand) {
		if (Boolean.TRUE.equals(commonJobGroupReader.existCommonJobGroupByNameAndExcludingSelf(commonJobGroupCommand.getName(), id))) {
			throw new IllegalStatusException("Already registered CommonJobGroup code.");
		}

		CommonJobGroup modifyCommonJobGroup = commonJobGroupReader.getCommonJobGroupById(id);

		CommonJobGroup commonJobGroup = commonJobGroupStore.modify(modifyCommonJobGroup, commonJobGroupCommand);
		return new CommonJobGroupInfo(commonJobGroup);
	}

	@Override
	public void removeCommonJobGroup(UUID id) {
		commonJobGroupStore.remove(id);
	}

	@Override
	public CommonJobGroupInfo searchCommonJobGroupById(UUID id) {
		CommonJobGroup commonJobGroup = commonJobGroupReader.getCommonJobGroupById(id);
		return new CommonJobGroupInfo(commonJobGroup);
	}

	@Override
	public List<CommonJobGroupInfo> getAllCommonJobGroup() {
		List<CommonJobGroup> commonJobGroupList = commonJobGroupReader.getAllCommonJobGroup();
		return commonJobGroupList.stream()
				.map(CommonJobGroupInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public WiniPageInfo<CommonJobGroupInfo> searchCommonJobGroupPage(Integer page, Integer pageSize, String searchType, String searchKeyword, CommonJobGroup.Status status) {
		Page<CommonJobGroupInfo> commonJobGroupPage = commonJobGroupReader.getCommonJobGroupPage(page, pageSize, searchType, searchKeyword, status);
		
		return new WiniPageInfo<>(commonJobGroupPage);
	}
}
