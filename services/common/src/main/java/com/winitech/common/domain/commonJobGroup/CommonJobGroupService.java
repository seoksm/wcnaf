package com.winitech.common.domain.commonJobGroup;

import com.winitech.common.library.commonType.WiniPageInfo;

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
public interface CommonJobGroupService {
	CommonJobGroupInfo registerCommonJobGroup(CommonJobGroupCommand.RegisterRequestCommand commonJobGroupCommand);
	CommonJobGroupInfo modifyCommonJobGroup(UUID id, CommonJobGroupCommand.ModifyRequestCommand commonJobGroupCommand);
	void removeCommonJobGroup(UUID id);
	CommonJobGroupInfo searchCommonJobGroupById(UUID id);
	List<CommonJobGroupInfo> getAllCommonJobGroup();
	WiniPageInfo<CommonJobGroupInfo> searchCommonJobGroupPage(Integer page, Integer pageSize, String searchType, String searchKeyword, CommonJobGroup.Status status);
}
