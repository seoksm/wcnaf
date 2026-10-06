package com.winitech.common.domain.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonAuthorizationGroupUserServiceImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 12:44
 **/

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonAuthorizationGroupUserServiceImpl extends EgovAbstractServiceImpl implements CommonAuthorizationGroupUserService {
	private final CommonAuthorizationGroupUserStore commonAuthorizationGroupUserStore;
	private final CommonAuthorizationGroupUserReader commonAuthorizationGroupUserReader;
	
	@Override
	public CommonAuthorizationGroupUserInfo registerCommonAuthorizationGroupUser(CommonAuthorizationGroupUserCommand command) {
		CommonAuthorizationGroupUser initCommonAuthorizationGroupUser = CommonAuthorizationGroupUser.builder()
				.userId(command.getUserId())
				.authorizationGroupId(command.getAuthorizationGroupId())
				.build();
		CommonAuthorizationGroupUser commonAuthorizationGroupUser = commonAuthorizationGroupUserStore.store(initCommonAuthorizationGroupUser);
		return new CommonAuthorizationGroupUserInfo(commonAuthorizationGroupUser);
	}

	@Override
	public CommonAuthorizationGroupUserInfo modifyCommonAuthorizationGroupUser(UUID userId, UUID authorizationGroupId, CommonAuthorizationGroupUserCommand command) {
		CommonAuthorizationGroupUser modifyCommonAuthorizationGroupUser = commonAuthorizationGroupUserReader.getCommonAuthorizationGroupUserById(userId, authorizationGroupId);
		modifyCommonAuthorizationGroupUser.setUserId(command.getUserId());
		modifyCommonAuthorizationGroupUser.setAuthorizationGroupId(command.getAuthorizationGroupId());
		
		CommonAuthorizationGroupUser commonAuthorizationGroupUser = commonAuthorizationGroupUserStore.store(modifyCommonAuthorizationGroupUser);
		return new CommonAuthorizationGroupUserInfo(commonAuthorizationGroupUser);
	}
	
	@Override
	public void removeCommonAuthorizationGroupUser(UUID userId, UUID authorizationGroupId) {
		commonAuthorizationGroupUserStore.remove(userId, authorizationGroupId);
	}

	@Override
	public CommonAuthorizationGroupUserInfo searchCommonAuthorizationGroupUserById(UUID userId, UUID authorizationGroupId) {
		CommonAuthorizationGroupUser commonAuthorizationGroupUser = commonAuthorizationGroupUserReader.getCommonAuthorizationGroupUserById(userId, authorizationGroupId);
		return new CommonAuthorizationGroupUserInfo(commonAuthorizationGroupUser);
	}

	@Override
	public List<CommonAuthorizationGroupUserInfo> getAllCommonAuthorizationGroupUser() {
		return commonAuthorizationGroupUserReader.getAllCommonAuthorizationGroupUser()
				.stream()
				.map(CommonAuthorizationGroupUserInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public CommonAuthorizationGroupUserInfo saveCommonAuthorizationGroupUser(CommonAuthorizationGroupUserCommand command) {
		CommonAuthorizationGroupUser commonAuthorizationGroupUser = commonAuthorizationGroupUserReader.getCommonAuthorizationGroupUserByIdIfExists(command.getUserId(), command.getAuthorizationGroupId());
		
		if (commonAuthorizationGroupUser == null) {
			return registerCommonAuthorizationGroupUser(command);
		} else {
			return modifyCommonAuthorizationGroupUser(command.getUserId(), command.getAuthorizationGroupId(), command);
		}
	}

	@Override
	public void syncAll(List<UUID> authorizationGroupIdList, List<CommonAuthorizationGroupUserCommand> commandList) {
		Map<UUID, List<CommonAuthorizationGroupUser>> authorizationGroupIdToAuthorizationGroupUserListMap = commonAuthorizationGroupUserReader.getListByAuthorizationGroupIdList(authorizationGroupIdList)
				.stream()
				.collect(Collectors.groupingBy(CommonAuthorizationGroupUser::getAuthorizationGroupId));

		Map<UUID, List<CommonAuthorizationGroupUserCommand>> authorizationGroupUserCommandMap = commandList
				.stream()
				.collect(Collectors.groupingBy(CommonAuthorizationGroupUserCommand::getAuthorizationGroupId));
		
		List<CommonAuthorizationGroupUser> toDeleteList = new ArrayList<>();
		List<CommonAuthorizationGroupUser> toSaveList = new ArrayList<>();
		
		authorizationGroupIdList.forEach(authorizationGroupId -> {
			List<CommonAuthorizationGroupUser> authorizationGroupUserList = authorizationGroupIdToAuthorizationGroupUserListMap.get(authorizationGroupId);
			
			if (authorizationGroupUserList == null) {
				authorizationGroupUserList = new ArrayList<>();
			}
			
			List<CommonAuthorizationGroupUserCommand> authorizationGroupUserCommandList = authorizationGroupUserCommandMap.get(authorizationGroupId);

			Map<UUID, List<CommonAuthorizationGroupUser>> userIdToAuthorizationGroupUserMap = authorizationGroupUserList
					.stream()
					.collect(Collectors.groupingBy(CommonAuthorizationGroupUser::getUserId));

			if (authorizationGroupUserCommandList != null) {
				for (CommonAuthorizationGroupUserCommand command : authorizationGroupUserCommandList) {
					if (userIdToAuthorizationGroupUserMap.containsKey(command.getUserId())) {
						// 기존에 있는 데이터이면 유지
						List<CommonAuthorizationGroupUser> list = userIdToAuthorizationGroupUserMap.get(command.getUserId());

						toSaveList.add(list.get(0));

						if (list.size() > 1) {
							toDeleteList.addAll(list.subList(1, list.size()));
						}

						userIdToAuthorizationGroupUserMap.remove(command.getUserId());
					} else {
						// 기존에 없는 신규 데이터인 경우
						toSaveList.add(command.toEntity());
					}
				}
			}
			
			userIdToAuthorizationGroupUserMap.forEach((userId, list) -> {
				toDeleteList.addAll(list);
			});
		});
		
		commonAuthorizationGroupUserStore.storeAll(toSaveList);
		commonAuthorizationGroupUserStore.removeAll(toDeleteList);
	}
}
