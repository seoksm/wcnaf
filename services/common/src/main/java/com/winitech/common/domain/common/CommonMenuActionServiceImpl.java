package com.winitech.common.domain.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonMenuActionServiceImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 12:44
 **/

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonMenuActionServiceImpl extends EgovAbstractServiceImpl implements CommonMenuActionService {
	private final CommonMenuActionStore commonMenuActionStore;
	private final CommonMenuActionReader commonMenuActionReader;

	@Override
	public CommonMenuActionInfo registerCommonMenuAction(CommonMenuActionCommand command) {
		CommonMenuAction initCommonMenuAction = CommonMenuAction.builder()
				.menuId(command.getMenuId())
				.programId(command.getProgramId())
				.programCode(command.getProgramCode())
				.actionType(command.getActionType())
				.authType(command.getAuthType())
				.uri(command.getUri())
				.build();
		CommonMenuAction commonMenuAction = commonMenuActionStore.store(initCommonMenuAction);
		return new CommonMenuActionInfo(commonMenuAction);
	}

	@Override
	public CommonMenuActionInfo modifyCommonMenuAction(UUID id, CommonMenuActionCommand command) {
		CommonMenuAction modifyCommonMenuAction = commonMenuActionReader.getCommonMenuActionById(id);
		modifyCommonMenuAction.setMenuId(command.getMenuId());
		modifyCommonMenuAction.setProgramId(command.getProgramId());
		modifyCommonMenuAction.setProgramCode(command.getProgramCode());
		modifyCommonMenuAction.setActionType(command.getActionType());
		modifyCommonMenuAction.setAuthType(command.getAuthType());
		modifyCommonMenuAction.setUri(command.getUri());

		CommonMenuAction commonMenuAction = commonMenuActionStore.store(modifyCommonMenuAction);
		return new CommonMenuActionInfo(commonMenuAction);
	}

	@Override
	public void removeCommonMenuAction(UUID id) {
		commonMenuActionStore.remove(id);
	}

	@Override
	public CommonMenuActionInfo searchCommonMenuActionById(UUID id) {
		CommonMenuAction commonMenuAction = commonMenuActionReader.getCommonMenuActionById(id);
		return new CommonMenuActionInfo(commonMenuAction);
	}

//	@Override
//	public List<CommonMenuActionInfo> searchCommonMenuActionByName(String name) {
//		return commonMenuActionReader.searchCommonMenuActionByName(name)
//				.stream()
//				.map(CommonMenuActionInfo::new)
//				.collect(Collectors.toList());
//	}

	@Override
	public List<CommonMenuActionInfo> getAllCommonMenuAction() {
		return commonMenuActionReader.getAllCommonMenuAction()
				.stream()
				.map(CommonMenuActionInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public CommonMenuActionInfo saveCommonMenuAction(CommonMenuActionCommand command) {
		CommonMenuAction commonMenuAction = commonMenuActionReader.getCommonMenuActionByIdIfExists(command.getId());

		if (commonMenuAction == null) {
			return registerCommonMenuAction(command);
		} else {
			return modifyCommonMenuAction(command.getId(), command);
		}
	}

	@Override
	public void syncAll(List<UUID> programIdList, List<CommonMenuActionCommand> commandList) {
		Map<UUID, List<CommonMenuAction>> programIdToMenuActionListMap = commonMenuActionReader.getListByProgramIdList(programIdList)
				.stream()
				.collect(Collectors.groupingBy(CommonMenuAction::getProgramId));

		Map<UUID, List<CommonMenuActionCommand>> menuActionCommandMap = commandList
				.stream()
				.collect(Collectors.groupingBy(CommonMenuActionCommand::getProgramId));

		List<CommonMenuAction> toDeleteList = new ArrayList<>();
		List<CommonMenuAction> toSaveList = new ArrayList<>();

		programIdList.forEach(programId -> {
			List<CommonMenuAction> MenuActionList = programIdToMenuActionListMap.get(programId);

			if (MenuActionList == null) {
				MenuActionList = new ArrayList<>();
			}

			List<CommonMenuActionCommand> menuActionCommandList = menuActionCommandMap.get(programId);
			
			Map<String, List<CommonMenuAction>> keyToMenuActionMap = MenuActionList
					.stream()
					.map(it -> {
						String key = it.getMenuId() + "_" + it.getActionType() + "_" + it.getAuthType() + "_" + it.getUri();
						
						return new HashMap.SimpleEntry<>(key, it);
					})
					.collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

			if (menuActionCommandList != null) {
				for (CommonMenuActionCommand command : menuActionCommandList) {
					String key = command.getMenuId() + "_" + command.getActionType() + "_" + command.getAuthType() + "_" + command.getUri();
					
					if (keyToMenuActionMap.containsKey(key)) {
						// 기존에 있는 데이터이면 유지
						List<CommonMenuAction> list = keyToMenuActionMap.get(key);

						toSaveList.add(list.get(0));

						if (list.size() > 1) {
							toDeleteList.addAll(list.subList(1, list.size()));
						}

						keyToMenuActionMap.remove(key);
					} else {
						// 기존에 없는 신규 데이터인 경우
						toSaveList.add(command.toEntity());
					}
				}
			}

			keyToMenuActionMap.forEach((key, list) -> {
				toDeleteList.addAll(list);
			});
		});

		commonMenuActionStore.storeAll(toSaveList);
		commonMenuActionStore.removeAll(toDeleteList);
	}
}
