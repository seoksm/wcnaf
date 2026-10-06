package com.winitech.common.application.commonFile;

import com.winitech.common.domain.common.CommonUser;
import com.winitech.common.domain.common.CommonUserCommand;
import com.winitech.common.domain.common.CommonUserInfo;
import com.winitech.common.domain.common.CommonUserService;
import com.winitech.common.library.commonType.WiniPageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.application.commonFile
 * └ CommonUserFacade.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-21 14:11
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class CommonUserFacade {
	private final CommonUserService commonUserService;

	public CommonUserInfo registerCommonUser(CommonUserCommand commonUserCommand) {
		return commonUserService.registerCommonUser(commonUserCommand);
	}

	public CommonUserInfo modifyCommonUser(UUID id, CommonUserCommand commonUserCommand) {
		return commonUserService.modifyCommonUser(id, commonUserCommand);
	}

	public void removeCommonUser(UUID id) {
		commonUserService.removeCommonUser(id);
	}

	public CommonUserInfo searchCommonUserById(UUID id) {
		return commonUserService.searchCommonUserById(id);
	}

	public List<CommonUserInfo> searchCommonUserByName(String commonUserName) {
		return commonUserService.searchCommonUserByName(commonUserName);
	}

	public List<CommonUserInfo> getAllCommonUser() {
		return commonUserService.getAllCommonUser();
	}

	public CommonUserInfo saveCommonUser(CommonUserCommand commonUserCommand) {
		return commonUserService.saveCommonUser(commonUserCommand);
	}

	public WiniPageInfo<CommonUserInfo> searchAllUser(Integer page, Integer pageSize, String searchType, String searchKeyword, CommonUser.Status userStatus) {
		return commonUserService.searchAllUser(page, pageSize, searchType, searchKeyword, userStatus);
	}
}
