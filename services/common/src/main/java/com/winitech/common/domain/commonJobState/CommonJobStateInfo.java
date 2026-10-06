package com.winitech.common.domain.commonJobState;

import com.winitech.common.domain.commonJob.CommonJobInfo;
import com.winitech.common.domain.commonJobRun.CommonJobRunInfo;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.domain.commonJobState
 * └ CommonJobState.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:47
 **/
@Getter
public class CommonJobStateInfo {
	private final UUID id;
	private final CommonJobInfo commonJob;
	private final OffsetDateTime lastStartedAt;
	private final OffsetDateTime lastEndedAt;
	private final OffsetDateTime lastSuccessAt;
	private final OffsetDateTime lastFailedAt;
	private final CommonJobRunInfo currentRun;
	private final CommonJobRunInfo lastRun;
	private final Integer errCnt;
	private final String lastMessage;
	private final CommonJobState.SystemStatus systemStatus;
	private final OffsetDateTime updateAt;

	public CommonJobStateInfo(CommonJobState commonJobState) {
		this.id = commonJobState.getId();
		this.commonJob = new CommonJobInfo(commonJobState.getCommonJob());
		this.lastStartedAt = commonJobState.getLastStartedAt();
		this.lastEndedAt = commonJobState.getLastEndedAt();
		this.lastSuccessAt = commonJobState.getLastSuccessAt();
		this.lastFailedAt = commonJobState.getLastFailedAt();
		this.currentRun = commonJobState.getCurrentRun() == null ? null : new CommonJobRunInfo(commonJobState.getCurrentRun());
		this.lastRun = commonJobState.getLastRun() == null ? null : new CommonJobRunInfo(commonJobState.getLastRun());
		this.errCnt = commonJobState.getErrCnt();
		this.lastMessage = commonJobState.getLastMessage();
		this.systemStatus = commonJobState.getSystemStatus();
		this.updateAt = commonJobState.getUpdateAt();
	}
}
