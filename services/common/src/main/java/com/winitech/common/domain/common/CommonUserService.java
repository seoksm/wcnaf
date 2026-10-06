package com.winitech.common.domain.common;

import com.winitech.common.library.commonType.WiniPageInfo;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonUserService.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-21 12:44
 **/
public interface CommonUserService {
	CommonUserInfo registerCommonUser(CommonUserCommand commonUserCommand);

	CommonUserInfo modifyCommonUser(UUID id, CommonUserCommand commonUserCommand);

	void removeCommonUser(UUID id);

	CommonUserInfo searchCommonUserById(UUID id);

	List<CommonUserInfo> searchCommonUserByName(String commonUserName);

	List<CommonUserInfo> getAllCommonUser();

	CommonUserInfo saveCommonUser(CommonUserCommand commonUserCommand);

	WiniPageInfo<CommonUserInfo> searchAllUser(Integer page, Integer pageSize, String searchType, String searchKeyword, CommonUser.Status userStatus);
}
