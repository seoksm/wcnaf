package com.winitech.common.domain.common;

import com.winitech.common.library.commonType.WiniPageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonUserServiceImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-21 12:44
 **/

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonUserServiceImpl extends EgovAbstractServiceImpl implements CommonUserService {

	private final CommonUserStore commonUserStore;
	private final CommonUserReader commonUserReader;

	@Override
	public CommonUserInfo registerCommonUser(CommonUserCommand commonUserCommand) {
		CommonUser initCommonUser = CommonUser.builder()
				.id(commonUserCommand.getId())
				.username(commonUserCommand.getUsername())
				.firstName(commonUserCommand.getFirstName())
				.lastName(commonUserCommand.getLastName())
				.fullName(commonUserCommand.getFullName())
				.email(commonUserCommand.getEmail())
				.phoneNumber(commonUserCommand.getPhoneNumber())
				.employeeNo(commonUserCommand.getEmployeeNo())
				.dutyName(commonUserCommand.getDutyName())
				.departmentName(commonUserCommand.getDepartmentName())
				.status(CommonUser.Status.ENABLE)
				.build();
		CommonUser commonUser = commonUserStore.store(initCommonUser);
		return new CommonUserInfo(commonUser);
	}

	@Override
	public CommonUserInfo modifyCommonUser(UUID id, CommonUserCommand commonUserCommand) {
		CommonUser modifyCommonUser = commonUserReader.getCommonUserById(id);
		modifyCommonUser.setUsername(commonUserCommand.getUsername());
		modifyCommonUser.setFirstName(commonUserCommand.getFirstName());
		modifyCommonUser.setLastName(commonUserCommand.getLastName());
		modifyCommonUser.setFullName(commonUserCommand.getFullName());
		modifyCommonUser.setEmail(commonUserCommand.getEmail());
		modifyCommonUser.setPhoneNumber(commonUserCommand.getPhoneNumber());
		modifyCommonUser.setEmployeeNo(commonUserCommand.getEmployeeNo());
		modifyCommonUser.setDutyName(commonUserCommand.getDutyName());
		modifyCommonUser.setDepartmentName(commonUserCommand.getDepartmentName());
		modifyCommonUser.setStatus(CommonUser.Status.ENABLE);
		
		CommonUser commonUser = commonUserStore.store(modifyCommonUser);
		return new CommonUserInfo(commonUser);
	}

	@Override
	public void removeCommonUser(UUID id) {
		CommonUser commonUser = commonUserReader.getCommonUserById(id);
		commonUser.disable();
		commonUserStore.store(commonUser);
	}

	@Override
	public CommonUserInfo searchCommonUserById(UUID id) {
		CommonUser commonUser = commonUserReader.getCommonUserById(id);
		return new CommonUserInfo(commonUser);
	}

	@Override
	public List<CommonUserInfo> searchCommonUserByName(String name) {
		return commonUserReader.getCommonUserByName(name)
				.stream()
				.map(CommonUserInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public List<CommonUserInfo> getAllCommonUser() {
		List<CommonUser> commonUserList = commonUserReader.getAllCommonUser();
		return commonUserList.stream().map(CommonUserInfo::new).collect(Collectors.toList());
	}

	@Override
	public CommonUserInfo saveCommonUser(CommonUserCommand commonUserCommand) {
		CommonUser commonUser = commonUserReader.getCommonUserByIdIfExists(commonUserCommand.getId());
		
		if (commonUser == null) {
			return registerCommonUser(commonUserCommand);
		} else {
			return modifyCommonUser(commonUser.getId(), commonUserCommand);
		}
	}

	@Override
	public WiniPageInfo<CommonUserInfo> searchAllUser(Integer page, Integer pageSize, String searchType, String searchKeyword, CommonUser.Status userStatus) {
		Page<CommonUserInfo> commonUserPage = commonUserReader.getCommonUserPage(page, pageSize, searchType, searchKeyword, userStatus);
		
		return new WiniPageInfo<>(commonUserPage);
	}
}
