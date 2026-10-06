package com.winitech.apiGateway.application.userSessionBlock;

import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.apiGateway.domain.userSessionBlock.UserSessionBlockCommand;
import com.winitech.apiGateway.domain.userSessionBlock.UserSessionBlockInfo;
import com.winitech.apiGateway.domain.userSessionBlock.UserSessionBlockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
/**
 * <pre>
 * com.winitech.apiGateway.domain.userSessionBlock
 * └ UserSessionBlock.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-21 15:03
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionBlockFacade {
	private final UserSessionBlockService userSessionBlockService;

	public UserSessionBlockInfo registerUserSessionBlock(UserSessionBlockCommand.RegisterRequestCommand userSessionBlockCommand) {
		return userSessionBlockService.registerUserSessionBlock(userSessionBlockCommand);
	}

	public UserSessionBlockInfo modifyUserSessionBlock(UUID id, UserSessionBlockCommand.ModifyRequestCommand userSessionBlockCommand) {
		return userSessionBlockService.modifyUserSessionBlock(id, userSessionBlockCommand);
	}

	public void removeUserSessionBlock(UUID id) {
		userSessionBlockService.removeUserSessionBlock(id);
	}
	
	public void removeExpiredAccessToken() {
		userSessionBlockService.removeExpiredAccessToken();
	}

	public UserSessionBlockInfo searchUserSessionBlockById(UUID id) {
		return userSessionBlockService.searchUserSessionBlockById(id);
	}

	public List<UserSessionBlockInfo> getAllUserSessionBlock() {
		return userSessionBlockService.getAllUserSessionBlock();
	}
}
