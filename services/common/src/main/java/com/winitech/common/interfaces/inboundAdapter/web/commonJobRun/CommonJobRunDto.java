package com.winitech.common.interfaces.inboundAdapter.web.commonJobRun;

import com.winitech.common.domain.commonJobGroup.CommonJobGroupCommand;
import com.winitech.common.domain.commonJobRun.CommonJobRun;
import com.winitech.common.domain.commonJobRun.CommonJobRunCommand;
import com.winitech.common.domain.commonJobRun.CommonJobRunInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
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
@NoArgsConstructor
public class CommonJobRunDto {
	@ApiModel("CommonJobGroup management search request")
	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobRunSearchRequest {
		@ApiParam(value = "페이지 번호 (0부터 시작)", example = "0")
		Integer page;

		@ApiParam(value = "페이지 사이즈", example = "10")
		Integer pageSize;

//		@ApiParam(value = "검색 타입", allowableValues = "name,code")
//		String searchType;
//
//		@ApiParam(value = "검색어")
//		String searchKeyword;

		@ApiParam(value = "검색 시작일시", example = "2025-03-13T00:00:00Z")
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
		OffsetDateTime searchStartDateTime;

		@ApiParam(value = "검색 종료일시", example = "2025-03-13T23:59:59Z")
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
		OffsetDateTime searchEndDateTime;
		
		@ApiParam(value = "작업 실행 상태")
		CommonJobRun.JobStatus searchJobStatus;

		public CommonJobRunCommand.SearchRequestCommand toCommand(UUID commonJobId) {
			return CommonJobRunCommand.SearchRequestCommand.builder()
					.page(page)
					.pageSize(pageSize)
//					.searchType(searchType)
//					.searchKeyword(searchKeyword)
					.searchStartDateTime(searchStartDateTime)
					.searchEndDateTime(searchEndDateTime)
					.searchJobStatus(searchJobStatus)
					.commonJobId(commonJobId)
					.build();
		}
	}

	@ApiModel("CommonJobRun management request")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobRunRequest {
		@ApiModelProperty(value = "작업 고유 번호", example = "017e8400-e29b-41d4-a716-446655440000", required = true)
		private UUID commonJobId;

		@ApiModelProperty(value = "작업 트리거 고유번호", example = "117e8400-e29b-41d4-a716-446655441111", required = true)
		private UUID commonJobTriggerId;

		@ApiModelProperty(value = "시작일시", example = "2025-03-12T01:02:03Z", required = true)
		private OffsetDateTime startedAt;

		@ApiModelProperty(value = "종료일시", example = "2025-03-13T01:02:03Z")
		private OffsetDateTime endedAt;
		
		@ApiModelProperty(value = "작업 실행 상태", example = "PENDING", required = true)
		private CommonJobRun.JobStatus jobStatus;

		@ApiModelProperty(value = "메시지", example = "속성 예제")
		private String message;
		
		public CommonJobRunCommand toCommand() {
			return CommonJobRunCommand.builder()
				.commonJobId(commonJobId)
				.commonJobTriggerId(commonJobTriggerId)
				.startedAt(startedAt)
				.endedAt(endedAt)
				.jobStatus(jobStatus)
				.message(message)
				.build();
		}
	}

	@ApiModel("CommonJobRun management register request")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobRunRegisterRequest {
		@ApiModelProperty(value = "작업 고유 번호", example = "017e8400-e29b-41d4-a716-446655440000", required = true)
		private UUID commonJobId;

		@ApiModelProperty(value = "작업 트리거 고유번호", example = "117e8400-e29b-41d4-a716-446655441111", required = true)
		private UUID commonJobTriggerId;

		@ApiModelProperty(value = "시작일시", example = "2025-03-12T01:02:03Z", required = true)
		private OffsetDateTime startedAt;

		@ApiModelProperty(value = "종료일시", example = "2025-03-13T01:02:03Z")
		private OffsetDateTime endedAt;
		
		@ApiModelProperty(value = "작업 실행 상태", example = "PENDING", required = true)
		private CommonJobRun.JobStatus jobStatus;

		@ApiModelProperty(value = "메시지", example = "속성 예제")
		private String message;
		
		public CommonJobRunCommand.RegisterRequestCommand toCommand() {
			return CommonJobRunCommand.RegisterRequestCommand.builder()
				.commonJobId(commonJobId)
				.commonJobTriggerId(commonJobTriggerId)
				.startedAt(startedAt)
				.endedAt(endedAt)
				.jobStatus(jobStatus)
				.message(message)
				.build();
		}
	}

	@ApiModel("CommonJobRun management modify request")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobRunModifyRequest {
		@ApiModelProperty(value = "종료일시", example = "2025-03-13T01:02:03Z")
		private OffsetDateTime endedAt;

		@ApiModelProperty(value = "작업 실행 상태", example = "PENDING", required = true)
		private CommonJobRun.JobStatus jobStatus;

		@ApiModelProperty(value = "메시지", example = "속성 예제")
		private String message;
		
		public CommonJobRunCommand.ModifyRequestCommand toCommand() {
			return CommonJobRunCommand.ModifyRequestCommand.builder()
				.endedAt(endedAt)
				.jobStatus(jobStatus)
				.message(message)
				.build();
		}
	}

	@ApiModel("CommonJobRun management response")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobRunResponse {
		@ApiModelProperty(value = "CommonJobRun ID", example = "017e8400-e29b-41d4-a716-446655440000")
		private UUID commonJobRunId;

		@ApiModelProperty(value = "작업 고유 번호", example = "017e8400-e29b-41d4-a716-446655440000")
		private UUID commonJobId;

		@ApiModelProperty(value = "작업 트리거 고유번호", example = "117e8400-e29b-41d4-a716-446655441111")
		private UUID commonJobTriggerId;

		@ApiModelProperty(value = "시작일시", example = "2025-03-12T01:02:03Z")
		private OffsetDateTime startedAt;

		@ApiModelProperty(value = "종료일시", example = "2025-03-13T01:02:03Z")
		private OffsetDateTime endedAt;
		
		@ApiModelProperty(value = "작업 실행 상태", example = "PENDING")
		private CommonJobRun.JobStatus jobStatus;

		@ApiModelProperty(value = "메시지", example = "속성 예제")
		private String message;
		
		public CommonJobRunResponse(CommonJobRunInfo commonJobRun) {
			this.commonJobRunId = commonJobRun.getId();
			this.commonJobId = commonJobRun.getCommonJob().getId();
			this.commonJobTriggerId = commonJobRun.getCommonJobTrigger() == null ? null : commonJobRun.getCommonJobTrigger().getId();
			this.startedAt = commonJobRun.getStartedAt();
			this.endedAt = commonJobRun.getEndedAt();
			this.jobStatus = commonJobRun.getJobStatus();
			this.message = commonJobRun.getMessage();
		}
	}

	@ApiModel("CommonJobRun management store response")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobRunStoreResponse {
		@ApiModelProperty(value = "CommonJobRun ID", example = "017e8400-e29b-41d4-a716-446655440000", required = true)
		private UUID commonJobRunId;
	}
}
