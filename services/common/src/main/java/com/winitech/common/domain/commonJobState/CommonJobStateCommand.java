package com.winitech.common.domain.commonJobState;

import com.winitech.common.domain.commonJob.CommonJobReader;
import com.winitech.common.domain.commonJobRun.CommonJobRunReader;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

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
@Getter
@Builder
@ToString
public class CommonJobStateCommand {
	private final UUID id;
	private final UUID commonJobId;
	private final OffsetDateTime lastStartedAt;
	private final OffsetDateTime lastEndedAt;
	private final OffsetDateTime lastSuccessAt;
	private final OffsetDateTime lastFailedAt;
	private final UUID currentRunId;
	private final UUID lastRunId;
	private final Integer errCnt;
	private final String lastMessage;
	private final CommonJobState.SystemStatus systemStatus;

	public CommonJobState toEntity(CommonJobReader commonJobReader, CommonJobRunReader commonJobRunReader) {
		return CommonJobState.builder()
			.id(id)
			.commonJob(commonJobReader.getCommonJobById(commonJobId))
			.lastStartedAt(lastStartedAt)
			.lastEndedAt(lastEndedAt)
			.lastSuccessAt(lastSuccessAt)
			.lastFailedAt(lastFailedAt)
			.currentRun(commonJobRunReader.getCommonJobRunById(currentRunId))
			.lastRun(commonJobRunReader.getCommonJobRunById(lastRunId))
			.errCnt(errCnt)
			.lastMessage(lastMessage)
			.systemStatus(systemStatus)
			.build();
	}

	@Getter
	@Builder
	@ToString
	public static class RegisterRequestCommand {
		private final UUID commonJobId;
		private final OffsetDateTime lastStartedAt;
		private final OffsetDateTime lastEndedAt;
		private final OffsetDateTime lastSuccessAt;
		private final OffsetDateTime lastFailedAt;
		private final UUID currentRunId;
		private final UUID lastRunId;
		private final Integer errCnt;
		private final String lastMessage;
	}

	@Getter
	@Builder
	@ToString
	public static class ModifyRequestCommand {
		private final UUID commonJobId;
		private final OffsetDateTime lastStartedAt;
		private final OffsetDateTime lastEndedAt;
		private final OffsetDateTime lastSuccessAt;
		private final OffsetDateTime lastFailedAt;
		private final UUID currentRunId;
		private final UUID lastRunId;
		private final Integer errCnt;
		private final String lastMessage;
	}
}
