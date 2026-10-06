package com.winitech.common.interfaces.inboundAdapter.web.commonJobState;

import com.winitech.common.domain.commonJobState.CommonJobStateCommand;
import com.winitech.common.domain.commonJobState.CommonJobStateInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
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
@NoArgsConstructor
public class CommonJobStateDto {
	@ApiModel("CommonJobState management request")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobStateRequest {
		@ApiModelProperty(value = "작업 고유번호", example = "017e8400-e29b-41d4-a716-446655440000", required = true)
		private UUID commonJobId;
		
		@ApiModelProperty(value = "최종 시작일시", example = "2025-03-12T12:34:56Z")
		private OffsetDateTime lastStartedAt;

		@ApiModelProperty(value = "최종 종료일시", example = "2025-03-12T12:34:56Z")
		private OffsetDateTime lastEndedAt;

		@ApiModelProperty(value = "최종 성공일시", example = "2025-03-12T12:34:56Z")
		private OffsetDateTime lastSuccessAt;

		@ApiModelProperty(value = "최종 실패일시", example = "2025-03-12T12:34:56Z")
		private OffsetDateTime lastFailedAt;

		@ApiModelProperty(value = "현재 작업 실행 고유번호", example = "017e8400-e29b-41d4-a716-446655440000")
		private UUID currentRunId;
		
		@ApiModelProperty(value = "최종 작업 실행 고유번호", example = "017e8400-e29b-41d4-a716-446655440000")
		private UUID lastRunId;

		@ApiModelProperty(value = "에러 횟수", example = "0")
		private Integer errCnt;

		@ApiModelProperty(value = "최종 메시지", example = "성공")
		private String lastMessage;
		
		public CommonJobStateCommand toCommand() {
			return CommonJobStateCommand.builder()
				.commonJobId(commonJobId)
				.lastStartedAt(lastStartedAt)
				.lastEndedAt(lastEndedAt)
				.lastSuccessAt(lastSuccessAt)
				.lastFailedAt(lastFailedAt)
				.currentRunId(currentRunId)
				.lastRunId(lastRunId)
				.errCnt(errCnt)
				.lastMessage(lastMessage)
				.build();
		}
	}

	@ApiModel("CommonJobState management register request")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobStateRegisterRequest {
		@ApiModelProperty(value = "작업 고유번호", example = "017e8400-e29b-41d4-a716-446655440000", required = true)
		private UUID commonJobId;

		@ApiModelProperty(value = "최종 시작일시", example = "2025-03-12T12:34:56Z")
		private OffsetDateTime lastStartedAt;

		@ApiModelProperty(value = "최종 종료일시", example = "2025-03-12T12:34:56Z")
		private OffsetDateTime lastEndedAt;

		@ApiModelProperty(value = "최종 성공일시", example = "2025-03-12T12:34:56Z")
		private OffsetDateTime lastSuccessAt;

		@ApiModelProperty(value = "최종 실패일시", example = "2025-03-12T12:34:56Z")
		private OffsetDateTime lastFailedAt;

		@ApiModelProperty(value = "현재 작업 실행 고유번호", example = "017e8400-e29b-41d4-a716-446655440000")
		private UUID currentRunId;

		@ApiModelProperty(value = "최종 작업 실행 고유번호", example = "017e8400-e29b-41d4-a716-446655440000")
		private UUID lastRunId;

		@ApiModelProperty(value = "에러 횟수", example = "0")
		private Integer errCnt;

		@ApiModelProperty(value = "최종 메시지", example = "성공")
		private String lastMessage;
		
		public CommonJobStateCommand.RegisterRequestCommand toCommand() {
			return CommonJobStateCommand.RegisterRequestCommand.builder()
				.commonJobId(commonJobId)
				.lastStartedAt(lastStartedAt)
				.lastEndedAt(lastEndedAt)
				.lastSuccessAt(lastSuccessAt)
				.lastFailedAt(lastFailedAt)
				.currentRunId(currentRunId)
				.lastRunId(lastRunId)
				.errCnt(errCnt)
				.lastMessage(lastMessage)
				.build();
		}
	}

	@ApiModel("CommonJobState management modify request")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobStateModifyRequest {
		@ApiModelProperty(value = "작업 고유번호", example = "017e8400-e29b-41d4-a716-446655440000", required = true)
		private UUID commonJobId;

		@ApiModelProperty(value = "최종 시작일시", example = "2025-03-12T12:34:56Z")
		private OffsetDateTime lastStartedAt;

		@ApiModelProperty(value = "최종 종료일시", example = "2025-03-12T12:34:56Z")
		private OffsetDateTime lastEndedAt;

		@ApiModelProperty(value = "최종 성공일시", example = "2025-03-12T12:34:56Z")
		private OffsetDateTime lastSuccessAt;

		@ApiModelProperty(value = "최종 실패일시", example = "2025-03-12T12:34:56Z")
		private OffsetDateTime lastFailedAt;

		@ApiModelProperty(value = "현재 작업 실행 고유번호", example = "017e8400-e29b-41d4-a716-446655440000")
		private UUID currentRunId;

		@ApiModelProperty(value = "최종 작업 실행 고유번호", example = "017e8400-e29b-41d4-a716-446655440000")
		private UUID lastRunId;

		@ApiModelProperty(value = "에러 횟수", example = "0")
		private Integer errCnt;

		@ApiModelProperty(value = "최종 메시지", example = "성공")
		private String lastMessage;
		
		public CommonJobStateCommand.ModifyRequestCommand toCommand() {
			return CommonJobStateCommand.ModifyRequestCommand.builder()
				.commonJobId(commonJobId)
				.lastStartedAt(lastStartedAt)
				.lastEndedAt(lastEndedAt)
				.lastSuccessAt(lastSuccessAt)
				.lastFailedAt(lastFailedAt)
				.currentRunId(currentRunId)
				.lastRunId(lastRunId)
				.errCnt(errCnt)
				.lastMessage(lastMessage)
				.build();
		}
	}

	@ApiModel("CommonJobState management response")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobStateResponse {
		@ApiModelProperty(value = "CommonJobState ID", example = "017e8400-e29b-41d4-a716-446655440000", required = true)
		private UUID commonJobStateId;

		@ApiModelProperty(value = "작업 고유번호", example = "017e8400-e29b-41d4-a716-446655440000", required = true)
		private UUID commonJobId;

		@ApiModelProperty(value = "최종 시작일시", example = "2025-03-12T12:34:56Z")
		private OffsetDateTime lastStartedAt;

		@ApiModelProperty(value = "최종 종료일시", example = "2025-03-12T12:34:56Z")
		private OffsetDateTime lastEndedAt;

		@ApiModelProperty(value = "최종 성공일시", example = "2025-03-12T12:34:56Z")
		private OffsetDateTime lastSuccessAt;

		@ApiModelProperty(value = "최종 실패일시", example = "2025-03-12T12:34:56Z")
		private OffsetDateTime lastFailedAt;

		@ApiModelProperty(value = "현재 작업 실행 고유번호", example = "017e8400-e29b-41d4-a716-446655440000")
		private UUID currentRunId;

		@ApiModelProperty(value = "최종 작업 실행 고유번호", example = "017e8400-e29b-41d4-a716-446655440000")
		private UUID lastRunId;

		@ApiModelProperty(value = "에러 횟수", example = "0")
		private Integer errCnt;

		@ApiModelProperty(value = "최종 메시지", example = "성공")
		private String lastMessage;
		
		@ApiModelProperty(value = "데이터 수정 시각", example = "2025-03-12T12:34:56Z")
		private OffsetDateTime updateAt;
		
		public CommonJobStateResponse(CommonJobStateInfo commonJobState) {
			this.commonJobStateId = commonJobState.getId();
			this.commonJobId = commonJobState.getCommonJob().getId();
			this.lastStartedAt = commonJobState.getLastStartedAt();
			this.lastEndedAt = commonJobState.getLastEndedAt();
			this.lastSuccessAt = commonJobState.getLastSuccessAt();
			this.lastFailedAt = commonJobState.getLastFailedAt();
			this.currentRunId = commonJobState.getCurrentRun() == null ? null : commonJobState.getCurrentRun().getId();
			this.lastRunId = commonJobState.getLastRun() == null ? null : commonJobState.getLastRun().getId();
			this.errCnt = commonJobState.getErrCnt();
			this.lastMessage = commonJobState.getLastMessage();
			this.updateAt = commonJobState.getUpdateAt();
		}
	}

	@ApiModel("CommonJobState management store response")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobStateStoreResponse {
		@ApiModelProperty(value = "CommonJobState ID", example = "017e8400-e29b-41d4-a716-446655440000", required = true)
		private UUID commonJobStateId;
	}
}
