package com.winitech.common.infrastructure.commonJobGroup;

import com.winitech.common.exception.EntityNotFoundException;

import com.winitech.common.library.WiniCom;
import com.winitech.common.domain.commonJobGroup.CommonJobGroup;
import com.winitech.common.domain.commonJobGroup.CommonJobGroupInfo;
import com.winitech.common.domain.commonJobGroup.CommonJobGroupReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.domain.commonJobGroup
 * └ CommonJobGroup.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:36
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonJobGroupReaderImpl implements CommonJobGroupReader {
	private final CommonJobGroupRepository commonJobGroupRepository;
	private final CommonJobGroupQueryRepository commonJobGroupQueryRepository;

	@Override
	public CommonJobGroup getCommonJobGroupById(UUID commonJobGroupId) {
		return commonJobGroupRepository.findByIdAndSystemStatus(commonJobGroupId, CommonJobGroup.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
	}

	//@Override
	//public CommonJobGroup getCommonJobGroupByCommonJobGroupCode(String commonJobGroupCode) {
	//	return commonJobGroupRepository.findByCommonJobGroupCodeAndSystemStatus(commonJobGroupCode, CommonJobGroup.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
	//}

	@Override
	public List<CommonJobGroup> getAllCommonJobGroup() {
		return commonJobGroupRepository.findAllBySystemStatusOrderByIdDesc(CommonJobGroup.SystemStatus.ENABLE);
	}

	@Override
	public Page<CommonJobGroupInfo> getCommonJobGroupPage(Integer page, Integer pageSize, String searchType, String searchKeyword, CommonJobGroup.Status status) {
		return commonJobGroupQueryRepository.findAllPage(searchType, searchKeyword, status, WiniCom.getPageRequest(page, pageSize));
	}

	@Override
	public List<CommonJobGroup> getCommonJobGroupByName(List<String> jobGroupNameList) {
		return commonJobGroupRepository.findAllByNameInAndSystemStatus(jobGroupNameList, CommonJobGroup.SystemStatus.ENABLE);
	}

	@Override
	public boolean existCommonJobGroupByNameAndExcludingSelf(String name, UUID commonJobGroupId) {
		return commonJobGroupRepository.existsByNameAndIdNotAndSystemStatus(name, commonJobGroupId, CommonJobGroup.SystemStatus.ENABLE);
	}
}
