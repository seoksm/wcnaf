package com.winitech.common.infrastructure.commonJobState;

import com.winitech.common.exception.EntityNotFoundException;

import com.winitech.common.library.WiniCom;
import com.winitech.common.domain.commonJobState.CommonJobState;
import com.winitech.common.domain.commonJobState.CommonJobStateInfo;
import com.winitech.common.domain.commonJobState.CommonJobStateReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.commonJobState
 * └ CommonJobState.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:47
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonJobStateReaderImpl implements CommonJobStateReader {
	private final CommonJobStateRepository commonJobStateRepository;
	private final CommonJobStateQueryRepository commonJobStateQueryRepository;

	@Override
	public CommonJobState getCommonJobStateById(UUID commonJobStateId) {
		return commonJobStateRepository.findByIdAndSystemStatus(commonJobStateId, CommonJobState.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public CommonJobState getCommonJobStateByCommonJobId(UUID commonJobId) {
		List<CommonJobState> stateList = commonJobStateRepository.findAllByCommonJobIdAndSystemStatusOrderByIdDesc(commonJobId, CommonJobState.SystemStatus.ENABLE);

		if (stateList.size() == 0) {
			throw new EntityNotFoundException();
		}
		
		return stateList.get(0);
	}

	//@Override
	//public CommonJobState getCommonJobStateByCommonJobStateCode(String commonJobStateCode) {
	//	return commonJobStateRepository.findByCommonJobStateCodeAndSystemStatus(commonJobStateCode, CommonJobState.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
	//}

	@Override
	public List<CommonJobState> getAllCommonJobState() {
		return commonJobStateRepository.findAllBySystemStatusOrderByIdDesc(CommonJobState.SystemStatus.ENABLE);
	}

	@Override
	public Page<CommonJobStateInfo> getCommonJobStatePage(Integer page, Integer pageSize, String searchType, String searchKeyword) {
		return commonJobStateQueryRepository.findAllPage(searchType, searchKeyword, WiniCom.getPageRequest(page, pageSize));
	}

	@Override
	public boolean existCommonJobStateByCommonJobId(UUID commonJobId) {
		return commonJobStateRepository.existsByCommonJobIdAndSystemStatus(commonJobId, CommonJobState.SystemStatus.ENABLE);
	}

	//@Override
	//public boolean existCommonJobStateByExcludingSelf(String commonJobStateCode, UUID commonJobStateId)  {
	//	return commonJobStateRepository.existsByCommonJobStateCodeAndIdNotAndSystemStatus(commonJobStateCode, commonJobStateId, CommonJobState.SystemStatus.ENABLE);
	//}
}
