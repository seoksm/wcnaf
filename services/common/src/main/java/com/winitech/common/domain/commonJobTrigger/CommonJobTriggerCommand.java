package com.winitech.common.domain.commonJobTrigger;

import com.winitech.common.domain.commonJob.CommonJobReader;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.commonJobTrigger
 * └ CommonJobTrigger.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:45
 **/
@Getter
@Builder
@ToString
public class CommonJobTriggerCommand {
	private final UUID id;
	private final UUID commonJobId;
	private final String name;
	private final String triggerCron;
	private final Long triggerSeconds;
	private final CommonJobTrigger.TriggerType triggerType;
	private final CommonJobTrigger.Status status;
	private final CommonJobTrigger.SystemStatus systemStatus;

	public CommonJobTrigger toEntity(CommonJobReader commonJobReader) {
		return CommonJobTrigger.builder()
			.id(id)
			.commonJob(commonJobId == null ? null : commonJobReader.getCommonJobById(commonJobId))
			.name(name)
			.triggerCron(triggerCron)
			.triggerSeconds(triggerSeconds)
			.triggerType(triggerType)
			.status(status)
			.systemStatus(systemStatus)
			.build();
	}

	@Getter
	@Builder
	@ToString
	public static class RegisterRequestCommand {
		private final UUID commonJobId;
		private final String name;
		private final String triggerCron;
		private final Long triggerSeconds;
		private final CommonJobTrigger.TriggerType triggerType;
		private final CommonJobTrigger.Status status;
	}

	@Getter
	@Builder
	@ToString
	public static class ModifyRequestCommand {
		private final UUID commonJobId;
		private final String name;
		private final String triggerCron;
		private final Long triggerSeconds;
		private final CommonJobTrigger.TriggerType triggerType;
		private final CommonJobTrigger.Status status;
	}
}
