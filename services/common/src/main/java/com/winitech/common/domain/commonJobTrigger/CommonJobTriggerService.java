package com.winitech.common.domain.commonJobTrigger;

import com.winitech.common.library.commonType.WiniPageInfo;

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
public interface CommonJobTriggerService {
	CommonJobTriggerInfo registerCommonJobTrigger(CommonJobTriggerCommand.RegisterRequestCommand commonJobTriggerCommand);
	CommonJobTriggerInfo modifyCommonJobTrigger(UUID id, CommonJobTriggerCommand.ModifyRequestCommand commonJobTriggerCommand);
	void removeCommonJobTrigger(UUID commonJobId, UUID commonJobTriggerId);
	CommonJobTriggerInfo searchCommonJobTriggerById(UUID id);
	List<CommonJobTriggerInfo> getAllCommonJobTriggerByCommonJobId(UUID commonJobId);
	WiniPageInfo<CommonJobTriggerInfo> searchCommonJobTriggerPage(UUID commonJobId, Integer page, Integer pageSize, CommonJobTrigger.Status status, String searchType, String searchKeyword);
	List<CommonJobTriggerInfo> getAllCommonJobTrigger();

	void setApplyStatus(UUID commonJobTriggerId, CommonJobTrigger.ApplyStatus applyStatus);

	OffsetDateTime getLastPendingUpdateAt();
	void markAsAppliedExcept(List<UUID> commonJobTriggerIdList);
}
