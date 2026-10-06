package com.winitech.common.domain.commonJob;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.commonJob
 * └ CommonJob.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:37
 **/
public interface CommonJobStore {
	CommonJob store(CommonJob commonJob);
	CommonJob modify(CommonJob commonJob, CommonJobCommand.ModifyRequestCommand commonJobCommand);
	void remove(UUID commonJobId);

	void setApplyStatus(UUID commonJobId, CommonJob.ApplyStatus applyStatus);
	void markAsAppliedExcept(List<UUID> commonJobIdList);
}
