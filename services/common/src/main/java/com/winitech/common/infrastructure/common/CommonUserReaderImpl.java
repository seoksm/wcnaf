package com.winitech.common.infrastructure.common;

import com.winitech.common.domain.common.CommonUser;
import com.winitech.common.domain.common.CommonUserInfo;
import com.winitech.common.domain.common.CommonUserReader;
import com.winitech.common.exception.EntityNotFoundException;

import com.winitech.common.library.WiniCom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonUserReaderImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-21 12:59
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonUserReaderImpl implements CommonUserReader {
	private final CommonUserRepository commonUserRepository;
	
	private final CommonUserQueryRepository commonUserQueryRepository;

	@Override
	public CommonUser getCommonUserById(UUID id) {
		return commonUserRepository.findByIdAndStatus(id, CommonUser.Status.ENABLE).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public CommonUser getCommonUserByIdIfExists(UUID id) {
		return commonUserRepository.findByIdAndStatus(id, CommonUser.Status.ENABLE).orElse(null);
	}

	@Override
	public List<CommonUser> getCommonUserByName(String fullName) {
		return commonUserRepository.findAllByFullNameAndStatus(fullName, CommonUser.Status.ENABLE);
	}

	@Override
	public List<CommonUser> getAllCommonUser() {
		return commonUserRepository.findAllByStatus(CommonUser.Status.ENABLE);
	}

	@Override
	public boolean isExistCommonUserById(UUID id) {
		return commonUserRepository.existsById(id);
	}

	@Override
	public Page<CommonUserInfo> getCommonUserPage(Integer page, Integer pageSize, String searchType, String searchKeyword, CommonUser.Status userStatus) {
		return commonUserQueryRepository.findAllPage(searchType, searchKeyword, userStatus, WiniCom.getPageRequest(page, pageSize));
	}
}
