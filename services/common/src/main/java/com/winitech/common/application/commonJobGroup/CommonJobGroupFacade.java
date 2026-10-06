package com.winitech.common.application.commonJobGroup;

import com.winitech.common.domain.commonJobGroup.CommonJobGroup;
import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.common.domain.commonJobGroup.CommonJobGroupCommand;
import com.winitech.common.domain.commonJobGroup.CommonJobGroupInfo;
import com.winitech.common.domain.commonJobGroup.CommonJobGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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
@Service
@RequiredArgsConstructor
public class CommonJobGroupFacade {
	private final CommonJobGroupService commonJobGroupService;

	public CommonJobGroupInfo registerCommonJobGroup(CommonJobGroupCommand.RegisterRequestCommand commonJobGroupCommand) {
		return commonJobGroupService.registerCommonJobGroup(commonJobGroupCommand);
	}

	public CommonJobGroupInfo modifyCommonJobGroup(UUID id, CommonJobGroupCommand.ModifyRequestCommand commonJobGroupCommand) {
		return commonJobGroupService.modifyCommonJobGroup(id, commonJobGroupCommand);
	}

	public void removeCommonJobGroup(UUID id) {
		commonJobGroupService.removeCommonJobGroup(id);
	}

	public CommonJobGroupInfo searchCommonJobGroupById(UUID id) {
		return commonJobGroupService.searchCommonJobGroupById(id);
	}

	public List<CommonJobGroupInfo> getAllCommonJobGroup() {
		return commonJobGroupService.getAllCommonJobGroup();
	}

	public WiniPageInfo<CommonJobGroupInfo> searchCommonJobGroupPage(Integer page, Integer pageSize, String searchType, String searchKeyword, CommonJobGroup.Status status) {
		return commonJobGroupService.searchCommonJobGroupPage(page, pageSize, searchType, searchKeyword, status);
	}
}
