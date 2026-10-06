package com.winitech.common.domain.commonJob;

import com.winitech.common.domain.commonJobGroup.CommonJobGroupReader;
import com.winitech.common.domain.commonJobTrigger.CommonJobTrigger;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.commonJob
 * └ CommonJob.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:37
 **/
@Getter
@Builder
@ToString
public class CommonJobCommand {
	private final UUID id;
	private final String name;
	private final String className;
	private final String sql;
	private final String remark;
	private final UUID commonJobGroupId;
	private final CommonJob.JobType jobType;
	private final CommonJob.Status status;
	private final CommonJob.SystemStatus systemStatus;

	public CommonJob toEntity(CommonJobGroupReader commonJobGroupReader) {
		return CommonJob.builder()
			.id(id)
			.name(name)
			.className(className)
			.sql(sql)
			.remark(remark)
			.commonJobGroup(commonJobGroupReader.getCommonJobGroupById(commonJobGroupId))
			.jobType(jobType)
			.status(status)
			.systemStatus(systemStatus)
			.build();
	}

	@Getter
	@Builder
	@ToString
	public static class RegisterRequestCommand {
		private final String name;
		private final String className;
		private final String sql;
		private final String remark;
		private final UUID commonJobGroupId;
		private final CommonJob.JobType jobType;
		private final CommonJob.Status status;
		private final CommonJob.SystemStatus systemStatus;
	}

	@Getter
	@Builder
	@ToString
	public static class RegisterAutoCommand {
		private final String jobGroupName;
		private final String jobName;
		private final String className;
		private final String remark;
		private final Boolean triggerEnabled;
		private final CommonJobTrigger.TriggerType triggerType;
		private final Long triggerSeconds;
		private final String triggerCron;
	}

	@Getter
	@Builder
	@ToString
	public static class ModifyRequestCommand {
		private final String name;
		private final String className;
		private final String sql;
		private final String remark;
		private final UUID commonJobGroupId;
		private final CommonJob.JobType jobType;
		private final CommonJob.Status status;
		private final CommonJob.SystemStatus systemStatus;
	}
}
