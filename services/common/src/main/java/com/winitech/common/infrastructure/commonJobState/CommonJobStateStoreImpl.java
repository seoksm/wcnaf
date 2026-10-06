package com.winitech.common.infrastructure.commonJobState;

import com.winitech.common.domain.commonJob.CommonJobReader;
import com.winitech.common.domain.commonJobRun.CommonJobRunReader;
import com.winitech.common.domain.commonJobState.CommonJobState;
import com.winitech.common.domain.commonJobState.CommonJobStateCommand;
import com.winitech.common.domain.commonJobState.CommonJobStateReader;
import com.winitech.common.domain.commonJobState.CommonJobStateStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.Offset;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;
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
public class CommonJobStateStoreImpl implements CommonJobStateStore {
	private final CommonJobStateRepository commonJobStateRepository;
	private final CommonJobStateReader commonJobStateReader;
	private final CommonJobReader commonJobReader;
	private final CommonJobRunReader commonJobRunReader;

	@Override
	public CommonJobState store(CommonJobState commonJobState) {
		return commonJobStateRepository.save(commonJobState);
	}

	@Override
	public CommonJobState modify(CommonJobState commonJobState, CommonJobStateCommand.ModifyRequestCommand command) {
		// commonJobState.setCommonJob(command.getCommonJobId() == null ? null : commonJobReader.getCommonJobById(command.getCommonJobId()));
		commonJobState.setLastStartedAt(command.getLastStartedAt());
		commonJobState.setLastEndedAt(command.getLastEndedAt());
		commonJobState.setLastSuccessAt(command.getLastSuccessAt());
		commonJobState.setLastFailedAt(command.getLastFailedAt());
		commonJobState.setCurrentRun(command.getCurrentRunId() == null ? null : commonJobRunReader.getCommonJobRunById(command.getCurrentRunId()));
		commonJobState.setLastRun(command.getLastRunId() == null ? null : commonJobRunReader.getCommonJobRunById(command.getLastRunId()));
		commonJobState.setErrCnt(command.getErrCnt());
		commonJobState.setLastMessage(command.getLastMessage());

		return commonJobStateRepository.save(commonJobState);
	}

	@Override
	public void remove(UUID commonJobStateId) {
		CommonJobState commonJobState = commonJobStateReader.getCommonJobStateById(commonJobStateId);
		commonJobState.disable();
		commonJobStateRepository.save(commonJobState);
	}

	@Override
	public void setCurrentRun(UUID commonJobId, UUID currentRunId) {
		ensureJobStateExists(commonJobId);
		
		commonJobStateRepository.updateCurrentRun(commonJobId, currentRunId, OffsetDateTime.now());
	}

	@Override
	public void setSuccessfulRun(UUID commonJobId, UUID lastRunId, OffsetDateTime lastSuccessAt) {
		ensureJobStateExists(commonJobId);

		commonJobStateRepository.updateSuccessfulRun(commonJobId, lastRunId, lastSuccessAt);
	}

	@Override
	public void setFailedRun(UUID commonJobId, UUID lastRunId, OffsetDateTime lastFailedAt, String lastMessage) {
		ensureJobStateExists(commonJobId);

		commonJobStateRepository.updateFailedRun(commonJobId, lastRunId, lastFailedAt, lastMessage);
	}

	private void ensureJobStateExists(UUID commonJobId) {
		if (! commonJobStateReader.existCommonJobStateByCommonJobId(commonJobId)) {
			CommonJobState initCommonJobState = CommonJobState.builder()
					.commonJob(commonJobId == null ? null : commonJobReader.getCommonJobById(commonJobId))
					.systemStatus(CommonJobState.SystemStatus.ENABLE)
					.build();

			store(initCommonJobState);
		}
	}
}
