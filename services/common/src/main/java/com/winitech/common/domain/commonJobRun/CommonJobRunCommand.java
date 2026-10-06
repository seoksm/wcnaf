package com.winitech.common.domain.commonJobRun;

import com.fasterxml.classmate.AnnotationOverrides;
import com.winitech.common.domain.commonJob.CommonJobReader;
import com.winitech.common.domain.commonJobTrigger.CommonJobTriggerReader;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.commonJobRun
 * └ CommonJobRun.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:48
 **/
@Getter
@Builder
@ToString
public class CommonJobRunCommand {
	private final UUID id;
	private final UUID commonJobId;
	private final UUID commonJobTriggerId;
	private final OffsetDateTime startedAt;
	private final OffsetDateTime endedAt;
	private final CommonJobRun.JobStatus jobStatus;
	private final String message;
	private final CommonJobRun.SystemStatus systemStatus;

	public CommonJobRun toEntity(CommonJobReader commonJobReader, CommonJobTriggerReader commonJobTriggerReader) {
		return CommonJobRun.builder()
			.id(id)
			.commonJob(commonJobId == null ? null : commonJobReader.getCommonJobById(commonJobId))
			.commonJobTrigger(commonJobTriggerId == null ? null : commonJobTriggerReader.getCommonJobTriggerById(commonJobTriggerId))
			.startedAt(startedAt)
			.endedAt(endedAt)
			.jobStatus(jobStatus)
			.message(message)
			.systemStatus(systemStatus)
			.build();
	}

	@Getter
	@Builder
	@ToString
	public static class SearchRequestCommand {
		private final Integer page;
		private final Integer pageSize;
		private final String searchType;
		private final String searchKeyword;
		private final OffsetDateTime searchStartDateTime;
		private final OffsetDateTime searchEndDateTime;
		private final CommonJobRun.JobStatus searchJobStatus;

		private final UUID commonJobId;
	}

	@Getter
	@Builder
	@ToString
	public static class RegisterRequestCommand {
		private final UUID commonJobId;
		private final UUID commonJobTriggerId;
		private final OffsetDateTime startedAt;
		private final OffsetDateTime endedAt;
		private final CommonJobRun.JobStatus jobStatus;
		private final String message;
	}

	@Getter
	@Builder
	@ToString
	public static class ModifyRequestCommand {
		private final OffsetDateTime endedAt;
		private final CommonJobRun.JobStatus jobStatus;
		private final String message;
	}
}
