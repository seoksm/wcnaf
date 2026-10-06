package com.winitech.common.interfaces.inboundAdapter.web.commonJobTrigger;

import com.winitech.common.domain.commonJobTrigger.CommonJobTrigger;
import com.winitech.common.domain.commonJobTrigger.CommonJobTriggerCommand;
import com.winitech.common.domain.commonJobTrigger.CommonJobTriggerInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.commonJobTrigger
 * └ CommonJobTrigger.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:45
 **/
@NoArgsConstructor
public class CommonJobTriggerDto {
	@ApiModel("CommonJobTrigger management request")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobTriggerRequest {
		@ApiModelProperty(value = "작업 고유번호", example = "017e8400-e29b-41d4-a716-446655440000", required = true)
		private UUID commonJobId;
		
		@ApiModelProperty(value = "작업 트리거 이름", example = "매일 오후 2시에 진행시켜", required = true)
		@NotBlank
		private String name;

		@ApiModelProperty(value = "CRON 표현식", example = "0 0 14 * * ?", notes = "초 분 시 일 월 요일 년(선택)")
		private String triggerCron;

		@ApiModelProperty(value = "초단위 간격", example = "60", notes = "60 => 매분, 3600 => 매시간")
		private Long triggerSeconds;

		@ApiModelProperty(value = "트리거 유형", example = "CRON", required = true, allowableValues = "CRON, SECONDS")
		private CommonJobTrigger.TriggerType triggerType;

		@ApiModelProperty(value = "상태", example = "ENABLE", required = true, allowableValues = "ENABLE, DISABLE")
		private CommonJobTrigger.Status status;
		
		public CommonJobTriggerCommand toCommand() {
			return CommonJobTriggerCommand.builder()
				.commonJobId(commonJobId)
				.name(name)
				.triggerCron(triggerCron)
				.triggerSeconds(triggerSeconds)
				.triggerType(triggerType)
				.status(status)
				.build();
		}
	}

	@ApiModel("CommonJobTrigger management register request")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobTriggerRegisterRequest {
		/*@ApiModelProperty(value = "작업 고유번호", example = "017e8400-e29b-41d4-a716-446655440000", required = true)
		private UUID commonJobId;*/

		@ApiModelProperty(value = "작업 트리거 이름", example = "매일 오후 2시에 진행시켜", required = true)
		@NotBlank
		private String name;

		@ApiModelProperty(value = "CRON 표현식", example = "0 0 14 * * ?", notes = "초 분 시 일 월 요일 년(선택)")
		private String triggerCron;

		@ApiModelProperty(value = "초단위 간격", example = "60", notes = "60 => 매분, 3600 => 매시간")
		private Long triggerSeconds;

		@ApiModelProperty(value = "트리거 유형", example = "CRON", required = true, allowableValues = "CRON, SECONDS")
		private CommonJobTrigger.TriggerType triggerType;

		@ApiModelProperty(value = "상태", example = "ENABLE", required = true, allowableValues = "ENABLE, DISABLE")
		private CommonJobTrigger.Status status;
		
		public CommonJobTriggerCommand.RegisterRequestCommand toCommand(UUID commonJobId) {
			return CommonJobTriggerCommand.RegisterRequestCommand.builder()
				.commonJobId(commonJobId)
				.name(name)
				.triggerCron(triggerCron)
				.triggerSeconds(triggerSeconds)
				.triggerType(triggerType)
				.status(status)
				.build();
		}
	}

	@ApiModel("CommonJobTrigger management modify request")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobTriggerModifyRequest {
		/*@ApiModelProperty(value = "작업 고유번호", example = "017e8400-e29b-41d4-a716-446655440000", required = true)
		private UUID commonJobId;*/

		@ApiModelProperty(value = "작업 트리거 이름", example = "매일 오후 2시에 진행시켜", required = true)
		@NotBlank
		private String name;

		@ApiModelProperty(value = "CRON 표현식", example = "0 0 14 * * ?", notes = "초 분 시 일 월 요일 년(선택)")
		private String triggerCron;

		@ApiModelProperty(value = "초단위 간격", example = "60", notes = "60 => 매분, 3600 => 매시간")
		private Long triggerSeconds;

		@ApiModelProperty(value = "트리거 유형", example = "CRON", required = true, allowableValues = "CRON, SECONDS")
		private CommonJobTrigger.TriggerType triggerType;

		@ApiModelProperty(value = "상태", example = "ENABLE", required = true, allowableValues = "ENABLE, DISABLE")
		private CommonJobTrigger.Status status;
		
		public CommonJobTriggerCommand.ModifyRequestCommand toCommand(UUID commonJobId) {
			return CommonJobTriggerCommand.ModifyRequestCommand.builder()
				.commonJobId(commonJobId)
				.name(name)
				.triggerCron(triggerCron)
				.triggerSeconds(triggerSeconds)
				.triggerType(triggerType)
				.status(status)
				.build();
		}
	}

	@ApiModel("CommonJobTrigger management response")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobTriggerResponse {
		@ApiModelProperty(value = "CommonJobTrigger ID", example = "017e8400-e29b-41d4-a716-446655440000", required = true)
		private UUID commonJobTriggerId;
		
		@ApiModelProperty(value = "작업 고유번호", example = "017e8400-e29b-41d4-a716-446655440000")
		private UUID commonJobId;

		@ApiModelProperty(value = "작업 트리거 이름", example = "매일 오후 2시에 진행시켜")
		private String name;

		@ApiModelProperty(value = "CRON 표현식", example = "0 0 14 * * ?", notes = "초 분 시 일 월 요일 년(선택)")
		private String triggerCron;

		@ApiModelProperty(value = "초단위 간격", example = "60", notes = "60 => 매분, 3600 => 매시간")
		private Long triggerSeconds;

		@ApiModelProperty(value = "트리거 유형", example = "CRON", allowableValues = "CRON, SECONDS")
		private CommonJobTrigger.TriggerType triggerType;

		@ApiModelProperty(value = "상태", example = "ENABLE", allowableValues = "ENABLE, DISABLE")
		private CommonJobTrigger.Status status;
		
		@ApiModelProperty(value = "적용상태", example = "PENDING", allowableValues = "PENDING, SUCCESSFUL, FAILED")
		private CommonJobTrigger.ApplyStatus applyStatus;
		
		public CommonJobTriggerResponse(CommonJobTriggerInfo commonJobTrigger) {
			this.commonJobTriggerId = commonJobTrigger.getId();
			this.commonJobId = commonJobTrigger.getCommonJob().getId();
			this.name = commonJobTrigger.getName();
			this.triggerCron = commonJobTrigger.getTriggerCron();
			this.triggerSeconds = commonJobTrigger.getTriggerSeconds();
			this.triggerType = commonJobTrigger.getTriggerType();
			this.status = commonJobTrigger.getStatus();
			this.applyStatus = commonJobTrigger.getApplyStatus();
		}
	}

	@ApiModel("CommonJobTrigger management store response")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobTriggerStoreResponse {
		@ApiModelProperty(value = "CommonJobTrigger ID", example = "017e8400-e29b-41d4-a716-446655440000", required = true)
		private UUID commonJobTriggerId;
	}

	@ApiModel("Check Cron expression result")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CheckCronExpressionResult {
		@ApiModelProperty(value = "적합여부", example = "true", allowableValues = "true, false")
		private Boolean isValid;
		@ApiModelProperty(value = "에러메시지", example = "문법이 잘못되었습니다.")
		private String errMsg;
		@ApiModelProperty(value = "날짜 예제 목록", example = "[\"2024-01-01T00:00:00Z\"]", allowableValues = "true, false")
		private List<OffsetDateTime> exampleList;
		
		public CheckCronExpressionResult(CommonJobTriggerInfo.CheckCronExpression checkCronExpression) {
			this.isValid = checkCronExpression.getIsValid();
			this.errMsg = checkCronExpression.getErrMsg();
			this.exampleList = checkCronExpression.getExampleList();
		}
	}
}
