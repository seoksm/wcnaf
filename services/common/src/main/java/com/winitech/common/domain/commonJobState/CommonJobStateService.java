package com.winitech.common.domain.commonJobState;

import com.winitech.common.library.commonType.WiniPageInfo;

import java.time.OffsetDateTime;
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
public interface CommonJobStateService {
	CommonJobStateInfo registerCommonJobState(CommonJobStateCommand.RegisterRequestCommand commonJobStateCommand);
	CommonJobStateInfo modifyCommonJobState(UUID id, CommonJobStateCommand.ModifyRequestCommand commonJobStateCommand);
	void removeCommonJobState(UUID id);
	CommonJobStateInfo searchCommonJobStateById(UUID id);
	CommonJobStateInfo searchCommonJobStateByCommonJobId(UUID commonJobId);
	List<CommonJobStateInfo> getAllCommonJobState();
	WiniPageInfo<CommonJobStateInfo> searchCommonJobStatePage(Integer page, Integer pageSize, String searchType, String searchKeyword);

	void setCurrentRun(UUID commonJobRunId, UUID currentRunId);
	void setSuccessfulRun(UUID commonJobRunId, UUID lastRunId, OffsetDateTime lastSuccessAt);
	void setFailedRun(UUID commonJobRunId, UUID lastRunId, OffsetDateTime lastFailedAt, String lastMessage);
}
