package com.winitech.common.domain.commonJobTrigger;

import com.winitech.common.domain.commonJob.CommonJob;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.domain.commonJobTrigger
 * └ CommonJobTrigger.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:45
 **/
@Getter
public class CommonJobTriggerInfo {
	private final UUID id;
	private final CommonJob commonJob;
	private final String name;
	private final String triggerCron;
	private final Long triggerSeconds;
	private final CommonJobTrigger.ApplyStatus applyStatus;
	private final CommonJobTrigger.TriggerType triggerType;
	private final CommonJobTrigger.Status status;
	private final CommonJobTrigger.SystemStatus systemStatus;

	public CommonJobTriggerInfo(CommonJobTrigger commonJobTrigger) {
		this.id = commonJobTrigger.getId();
		this.commonJob = commonJobTrigger.getCommonJob();
		this.name = commonJobTrigger.getName();
		this.triggerCron = commonJobTrigger.getTriggerCron();
		this.triggerSeconds = commonJobTrigger.getTriggerSeconds();
		this.applyStatus = commonJobTrigger.getApplyStatus();
		this.triggerType = commonJobTrigger.getTriggerType();
		this.status = commonJobTrigger.getStatus();
		this.systemStatus = commonJobTrigger.getSystemStatus();
	}

	@Getter
	public static class CheckCronExpression {
		private final Boolean isValid;
		private final String errMsg;
		private final List<OffsetDateTime> exampleList;
		
		public CheckCronExpression(Boolean isValid, String errMsg, List<OffsetDateTime> exampleList) {
			this.isValid = isValid;
			this.errMsg = errMsg;
			this.exampleList = exampleList;
		}		
	}
}
