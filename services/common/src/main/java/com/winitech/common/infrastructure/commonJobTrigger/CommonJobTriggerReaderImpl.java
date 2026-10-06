package com.winitech.common.infrastructure.commonJobTrigger;

import com.winitech.common.exception.EntityNotFoundException;

import com.winitech.common.library.WiniCom;
import com.winitech.common.domain.commonJobTrigger.CommonJobTrigger;
import com.winitech.common.domain.commonJobTrigger.CommonJobTriggerInfo;
import com.winitech.common.domain.commonJobTrigger.CommonJobTriggerReader;
import com.winitech.common.library.commonType.WiniPageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.commonJobTrigger
 * └ CommonJobTrigger.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:45
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonJobTriggerReaderImpl implements CommonJobTriggerReader {
	private final CommonJobTriggerRepository commonJobTriggerRepository;
	private final CommonJobTriggerQueryRepository commonJobTriggerQueryRepository;

	@Override
	public CommonJobTrigger getCommonJobTriggerById(UUID id) {
		return commonJobTriggerRepository.findByIdAndSystemStatus(id, CommonJobTrigger.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public CommonJobTrigger getCommonJobTrigger(UUID commonJobId, UUID commonJobTriggerId) {
		return commonJobTriggerRepository.findByCommonJobIdAndIdAndSystemStatus(commonJobId, commonJobTriggerId, CommonJobTrigger.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
	}

	//@Override
	//public CommonJobTrigger getCommonJobTriggerByCommonJobTriggerCode(String commonJobTriggerCode) {
	//	return commonJobTriggerRepository.findByCommonJobTriggerCodeAndSystemStatus(commonJobTriggerCode, CommonJobTrigger.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
	//}

	@Override
	public List<CommonJobTrigger> getAllCommonJobTriggerByCommonJobId(UUID commonJobId) {
		return commonJobTriggerRepository.findAllByCommonJobIdAndSystemStatusOrderByIdDesc(commonJobId, CommonJobTrigger.SystemStatus.ENABLE);
	}

	@Override
	public Page<CommonJobTriggerInfo> getCommonJobTriggerPage(UUID commonJobId, Integer page, Integer pageSize, CommonJobTrigger.Status status, String searchType, String searchKeyword) {
		return commonJobTriggerQueryRepository.findAllPage(commonJobId, status, searchType, searchKeyword, WiniCom.getPageRequest(page, pageSize));
	}

	@Override
	public List<CommonJobTrigger> getAllCommonJobTrigger() {
		return commonJobTriggerRepository.findAllBySystemStatusOrderByIdDesc(CommonJobTrigger.SystemStatus.ENABLE);
	}

	@Override
	public OffsetDateTime getLastPendingUpdateAt() {
		return commonJobTriggerRepository.findLastPendingUpdateAtIncludeDisable();
	}

	//@Override
	//public boolean existCommonJobTriggerByExcludingSelf(String commonJobTriggerCode, UUID commonJobTriggerId)  {
	//	return commonJobTriggerRepository.existsByCommonJobTriggerCodeAndIdNotAndSystemStatus(commonJobTriggerCode, commonJobTriggerId, CommonJobTrigger.SystemStatus.ENABLE);
	//}
}
