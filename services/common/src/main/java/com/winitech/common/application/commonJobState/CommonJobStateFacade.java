package com.winitech.common.application.commonJobState;

import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.common.domain.commonJobState.CommonJobStateCommand;
import com.winitech.common.domain.commonJobState.CommonJobStateInfo;
import com.winitech.common.domain.commonJobState.CommonJobStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
@Slf4j
@Service
@RequiredArgsConstructor
public class CommonJobStateFacade {
	private final CommonJobStateService commonJobStateService;

	public CommonJobStateInfo registerCommonJobState(CommonJobStateCommand.RegisterRequestCommand commonJobStateCommand) {
		return commonJobStateService.registerCommonJobState(commonJobStateCommand);
	}

	public CommonJobStateInfo modifyCommonJobState(UUID id, CommonJobStateCommand.ModifyRequestCommand commonJobStateCommand) {
		return commonJobStateService.modifyCommonJobState(id, commonJobStateCommand);
	}

	public void removeCommonJobState(UUID id) {
		commonJobStateService.removeCommonJobState(id);
	}

	public CommonJobStateInfo searchCommonJobStateById(UUID id) {
		return commonJobStateService.searchCommonJobStateById(id);
	}

	public CommonJobStateInfo searchCommonJobStateByCommonJobId(UUID commonJobId) {
		return commonJobStateService.searchCommonJobStateByCommonJobId(commonJobId);
	}

	public List<CommonJobStateInfo> getAllCommonJobState() {
		return commonJobStateService.getAllCommonJobState();
	}

	public WiniPageInfo<CommonJobStateInfo> searchCommonJobStatePage(Integer page, Integer pageSize, String searchType, String searchKeyword) {
		return commonJobStateService.searchCommonJobStatePage(page, pageSize, searchType, searchKeyword);
	}

	public void setCurrentRun(UUID commonJobRunId, UUID currentRunId) {
		commonJobStateService.setCurrentRun(commonJobRunId, currentRunId);
	}

	public void setSuccessfulRun(UUID commonJobRunId, UUID lastRunId, OffsetDateTime lastSuccessAt) {
		commonJobStateService.setSuccessfulRun(commonJobRunId, lastRunId, lastSuccessAt);
	}

	public void setFailedRun(UUID commonJobRunId, UUID lastRunId, OffsetDateTime lastFailedAt, String lastMessage) {
		commonJobStateService.setFailedRun(commonJobRunId, lastRunId, lastFailedAt, lastMessage);
	}
}
