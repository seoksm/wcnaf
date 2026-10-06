package com.winitech.common.domain.commonJobTrigger;

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
public interface CommonJobTriggerStore {
	CommonJobTrigger store(CommonJobTrigger commonJobTrigger);
	CommonJobTrigger modify(CommonJobTrigger commonJobTrigger, CommonJobTriggerCommand.ModifyRequestCommand commonJobTriggerCommand);
	void remove(UUID commonJobId, UUID commonJobTriggerId);

	void setApplyStatus(UUID commonJobTriggerId, CommonJobTrigger.ApplyStatus applyStatus);
	void markAsAppliedExcept(List<UUID> commonJobTriggerIdList);
}
