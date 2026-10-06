package com.winitech.system.domain.authorizationGroupUser;

import com.winitech.common.exception.EntityNotFoundException;
import com.winitech.common.exception.IllegalStatusException;
import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.system.domain.authorizationGroup.AuthorizationGroup;
import com.winitech.system.domain.authorizationGroup.AuthorizationGroupReader;
import com.winitech.system.domain.program.Program;
import com.winitech.system.domain.programAction.ProgramAction;
import com.winitech.system.domain.programAction.ProgramActionInfo;
import com.winitech.system.domain.user.User;
import com.winitech.system.domain.user.UserReader;
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
 * com.winitech.system.domain.authorizationGroupUser
 * └ AuthorizationGroupUser.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-20 10:09
 **/
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthorizationGroupUserServiceImpl extends EgovAbstractServiceImpl implements AuthorizationGroupUserService {
	private final AuthorizationGroupUserStore authorizationGroupUserStore;
	private final AuthorizationGroupUserReader authorizationGroupUserReader;
	private final AuthorizationGroupReader authorizationGroupReader;
	private final UserReader userReader;

	@Override
	public AuthorizationGroupUserInfo registerAuthorizationGroupUser(UUID authorizationGroupId, AuthorizationGroupUserCommand.RegisterRequestCommand authorizationGroupUserCommand) {
		if (authorizationGroupUserCommand.getUserId() != null && authorizationGroupUserReader.existAuthorizationGroupUserByAuthorizationGroupIdAndUserIdAndExcludingSelf(authorizationGroupId, authorizationGroupUserCommand.getUserId(), UUID.randomUUID())) {
			throw new IllegalStatusException("Already registered User.");
		}

		AuthorizationGroup authorizationGroup = authorizationGroupReader.getAuthorizationGroup(authorizationGroupId);
		
		AuthorizationGroupUser initAuthorizationGroupUser = AuthorizationGroupUser.builder()
				.authorizationGroup(authorizationGroup)
				.user(userReader.getUser(authorizationGroupUserCommand.getUserId()))
				.build();

		AuthorizationGroupUser authorizationGroupUser = authorizationGroupUserStore.store(initAuthorizationGroupUser);
		return new AuthorizationGroupUserInfo(authorizationGroupUser);
	}

	@Override
	public List<AuthorizationGroupUserInfo> registerAuthorizationGroupUserBatch(UUID authorizationGroupId, List<AuthorizationGroupUserCommand.BatchRegisterRequestCommand> commands) {
		AuthorizationGroup authorizationGroup = AuthorizationGroup.builder().id(authorizationGroupId).build();

		List<AuthorizationGroupUser> authorizationGroupUserList = commands.stream()
				.filter(command -> command.getAuthorizationGroupUserStatus() == AuthorizationGroupUser.AuthorizationGroupUserStatus.ENABLE)
				.map(command -> AuthorizationGroupUser.builder()
						.authorizationGroup(authorizationGroup)
						.user(User.builder().id(command.getUserId()).build())
						.build())
				.collect(Collectors.toList());

		final Map<String, AuthorizationGroupUser> prevAuthorizationGroupUserMap = new HashMap<>();
		final Map<String, AuthorizationGroupUser> toDeleteMap = prevAuthorizationGroupUserMap;

		List<UUID> userIdList = commands
				.stream()
				.map(AuthorizationGroupUserCommand.BatchRegisterRequestCommand::getUserId)
				.collect(Collectors.toList());
		
		if (userIdList.size() == 0) {
			return new ArrayList<>();
		}

		// 기존 항목 조회하여 Map에 저장
		authorizationGroupUserReader.getAuthorizationGroupUserByAuthorizationGroupIdAndUserIdList(authorizationGroupId, userIdList).forEach(authorizationGroupUser -> {
			String key = authorizationGroupUser.getUser().getId().toString();
			prevAuthorizationGroupUserMap.put(key, authorizationGroupUser);
		});

		List<AuthorizationGroupUser> toSaveList = new ArrayList<>();
		Set<String> toSaveKeySet = new HashSet<>();

		// 신규 항목 및 수정된 항목을 toSaveList에 저장 
		authorizationGroupUserList.forEach(authorizationGroupUser -> {
			String key = authorizationGroupUser.getUser().getId().toString();

			if (toSaveKeySet.contains(key)) {
				return;
			}

			if (prevAuthorizationGroupUserMap.containsKey(key)) {
				AuthorizationGroupUser prevAuthorizationGroupUser = prevAuthorizationGroupUserMap.get(key);
				toSaveList.add(prevAuthorizationGroupUser);

				// 기존에 있는 항목 중 사용되는 항목은 삭제 대상에서 제외 
				toDeleteMap.remove(key);
			} else {
				toSaveList.add(authorizationGroupUser);
			}

			toSaveKeySet.add(key);
		});

		// 신규 항목 및 수정된 항목 저장
		List<AuthorizationGroupUser> storedAuthorizationGroupUserList = authorizationGroupUserStore.storeAll(toSaveList);

		// 기존 항목중 삭제된 항목 삭제
		toDeleteMap.values().forEach(authorizationGroupUser -> authorizationGroupUserStore.remove(authorizationGroupUser.getId()));

		return storedAuthorizationGroupUserList.stream()
				.map(AuthorizationGroupUserInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public void removeAuthorizationGroupUser(UUID authorizationGroupUserId) {
		authorizationGroupUserStore.remove(authorizationGroupUserId);
	}

	@Override
	public void removeAuthorizationGroupUser(UUID authorizationGroupId, UUID userId) {
		authorizationGroupUserStore.remove(authorizationGroupId, userId);
	}

	@Override
	public AuthorizationGroupUserInfo searchAuthorizationGroupUserById(UUID authorizationGroupUserId) {
		AuthorizationGroupUser authorizationGroupUser = authorizationGroupUserReader.getAuthorizationGroupUserById(authorizationGroupUserId);
		return new AuthorizationGroupUserInfo(authorizationGroupUser);
	}

	@Override
	public AuthorizationGroupUserInfo searchAuthorizationGroupUser(UUID authorizationGroupId, UUID userId) {
		AuthorizationGroupUser authorizationGroupUser = authorizationGroupUserReader.getAuthorizationGroupUser(authorizationGroupId, userId);
		return new AuthorizationGroupUserInfo(authorizationGroupUser);
	}

	@Override
	public List<AuthorizationGroupUserInfo> getAllAuthorizationGroupUser() {
		List<AuthorizationGroupUser> authorizationGroupUserList = authorizationGroupUserReader.getAllAuthorizationGroupUser();
		return authorizationGroupUserList.stream()
				.map(AuthorizationGroupUserInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public WiniPageInfo<AuthorizationGroupUserInfo.PageInfo> searchAuthorizationGroupUserPage(UUID authorizationGroupUserId, String status, String authorizationGroupUserStatus, Integer page, Integer pageSize, String searchType, String searchKeyword) {
		Page<AuthorizationGroupUserInfo.PageInfo> authorizationGroupUserPage = authorizationGroupUserReader.getAuthorizationGroupUserPage(authorizationGroupUserId, status, authorizationGroupUserStatus, page, pageSize, searchType, searchKeyword);

		return new WiniPageInfo<>(authorizationGroupUserPage);
	}

	@Override
	public List<AuthorizationGroupUserInfo> searchAuthorizationGroupUserByAuthorizationGroupIdList(List<UUID> authorizationGroupIdList) {
		return authorizationGroupUserReader.getAuthorizationGroupUserByAuthorizationGroupIdList(authorizationGroupIdList)
				.stream()
				.map(AuthorizationGroupUserInfo::new)
				.collect(Collectors.toList());
	}
}
