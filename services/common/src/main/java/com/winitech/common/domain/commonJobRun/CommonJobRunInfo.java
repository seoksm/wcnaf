package com.winitech.common.domain.commonJobRun;

import com.winitech.common.domain.commonJob.CommonJob;
import com.winitech.common.domain.commonJobTrigger.CommonJobTrigger;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.domain.commonJobRun
 * └ CommonJobRun.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:48
 **/
@Getter
public class CommonJobRunInfo {
	private final UUID id;
	private final CommonJob commonJob;
	private final CommonJobTrigger commonJobTrigger;
	private final OffsetDateTime startedAt;
	private final OffsetDateTime endedAt;
	private final CommonJobRun.JobStatus jobStatus;
	private final String message;
	private final CommonJobRun.SystemStatus systemStatus;

	public CommonJobRunInfo(CommonJobRun commonJobRun) {
		this.id = commonJobRun.getId();
		this.commonJob = commonJobRun.getCommonJob();
		this.commonJobTrigger = commonJobRun.getCommonJobTrigger();
		this.startedAt = commonJobRun.getStartedAt();
		this.endedAt = commonJobRun.getEndedAt();
		this.jobStatus = commonJobRun.getJobStatus();
		this.message = commonJobRun.getMessage();
		this.systemStatus = commonJobRun.getSystemStatus();
	}
}
