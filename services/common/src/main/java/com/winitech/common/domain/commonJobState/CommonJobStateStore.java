package com.winitech.common.domain.commonJobState;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.commonJobState
 * └ CommonJobState.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:47
 **/
public interface CommonJobStateStore {
	CommonJobState store(CommonJobState commonJobState);
	CommonJobState modify(CommonJobState commonJobState, CommonJobStateCommand.ModifyRequestCommand commonJobStateCommand);
	void remove(UUID commonJobStateId);
	
	void setCurrentRun(UUID commonJobId, UUID currentRunId);
	void setSuccessfulRun(UUID commonJobId, UUID lastRunId, OffsetDateTime lastSuccessAt);
	void setFailedRun(UUID commonJobId, UUID lastRunId, OffsetDateTime lastFailedAt, String lastMessage);
}
