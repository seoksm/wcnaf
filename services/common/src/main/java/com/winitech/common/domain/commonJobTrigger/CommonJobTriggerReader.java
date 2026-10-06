package com.winitech.common.domain.commonJobTrigger;

import com.winitech.common.library.commonType.WiniPageInfo;
import org.springframework.data.domain.Page;

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
public interface CommonJobTriggerReader {
	CommonJobTrigger getCommonJobTriggerById(UUID id);
	CommonJobTrigger getCommonJobTrigger(UUID commonJobId, UUID commonJobTriggerId);
	// CommonJobTrigger getCommonJobTriggerByCommonJobTriggerCode(String commonJobTriggerCode);
	List<CommonJobTrigger> getAllCommonJobTriggerByCommonJobId(UUID commonJobId);
	Page<CommonJobTriggerInfo> getCommonJobTriggerPage(UUID commonJobId, Integer page, Integer pageSize, CommonJobTrigger.Status status, String searchType, String searchKeyword);
	List<CommonJobTrigger> getAllCommonJobTrigger();

	OffsetDateTime getLastPendingUpdateAt();

	// boolean existCommonJobTriggerByExcludingSelf(String commonJobTriggerCode, UUID commonJobTriggerId);
}
