package com.winitech.common.interfaces.inboundAdapter.web.commonJob;

import com.winitech.common.domain.commonJob.CommonJob;
import com.winitech.common.domain.commonJob.CommonJobCommand;
import com.winitech.common.domain.commonJob.CommonJobInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.commonJob
 * └ CommonJob.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:37
 **/
@NoArgsConstructor
public class CommonJobDto {
	@ApiModel("CommonJob management request")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobRequest {
		@ApiModelProperty(value = "작업 이름", example = "테스트 작업", required = true)
		@NotBlank
		private String name;

		@ApiModelProperty(value = "클래스명", example = "com.winitech.system.infrastructure.job.TestJob")
		private String className;

		@ApiModelProperty(value = "쿼리", example = "INSERT INTO TEST VALUES(1)")
		private String sql;

		@ApiModelProperty(value = "비고", example = "작업에 대한 비고입니다.")
		private String remark;

		@ApiModelProperty(value = "작업 그룹 ID", example = "019587e2-ac46-1111-2222-e171cefc9395", required = true)
		private UUID commonJobGroupId;

		@ApiModelProperty(value = "작업유형", example = "JAVA", required = true, allowableValues = "JAVA, SQL")
		private CommonJob.JobType jobType;
		
		@ApiModelProperty(value = "작업상태", example = "ENABLE", required = true, allowableValues = "ENABLE, DISABLE")
		private CommonJob.Status status;
		
		public CommonJobCommand toCommand() {
			return CommonJobCommand.builder()
				.name(name)
				.className(className)
				.sql(sql)
				.remark(remark)
				.commonJobGroupId(commonJobGroupId)
				.jobType(jobType)
				.status(status)
				.systemStatus(CommonJob.SystemStatus.ENABLE)
				.build();
		}
	}

	@ApiModel("CommonJob management register request")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobRegisterRequest {
		@ApiModelProperty(value = "작업 이름", example = "테스트 작업", required = true)
		@NotBlank
		private String name;

		@ApiModelProperty(value = "클래스명", example = "com.winitech.system.infrastructure.job.TestJob")
		private String className;

		@ApiModelProperty(value = "쿼리", example = "INSERT INTO TEST VALUES(1)")
		private String sql;

		@ApiModelProperty(value = "비고", example = "작업에 대한 비고입니다.")
		private String remark;

		@ApiModelProperty(value = "작업 그룹 ID", example = "019587e2-ac46-1111-2222-e171cefc9395", required = true)
		private UUID commonJobGroupId;

		@ApiModelProperty(value = "작업유형", example = "JAVA", required = true, allowableValues = "JAVA, SQL")
		private CommonJob.JobType jobType;

		@ApiModelProperty(value = "작업상태", example = "ENABLE", required = true, allowableValues = "ENABLE, DISABLE")
		private CommonJob.Status status;

		public CommonJobCommand.RegisterRequestCommand toCommand() {
			return CommonJobCommand.RegisterRequestCommand.builder()
				.name(name)
				.className(className)
				.sql(sql)
				.remark(remark)
				.commonJobGroupId(commonJobGroupId)
				.jobType(jobType)
				.status(status)
				.build();
		}
	}

	@ApiModel("CommonJob management modify request")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobModifyRequest {
		@ApiModelProperty(value = "작업 이름", example = "테스트 작업", required = true)
		@NotBlank
		private String name;

		@ApiModelProperty(value = "클래스명", example = "com.winitech.system.infrastructure.job.TestJob")
		private String className;

		@ApiModelProperty(value = "쿼리", example = "INSERT INTO TEST VALUES(1)")
		private String sql;

		@ApiModelProperty(value = "비고", example = "작업에 대한 비고입니다.")
		private String remark;

		@ApiModelProperty(value = "작업 그룹 ID", example = "019587e2-ac46-1111-2222-e171cefc9395", required = true)
		private UUID commonJobGroupId;

		@ApiModelProperty(value = "작업유형", example = "JAVA", required = true, allowableValues = "JAVA, SQL")
		private CommonJob.JobType jobType;

		@ApiModelProperty(value = "작업상태", example = "ENABLE", required = true, allowableValues = "ENABLE, DISABLE")
		private CommonJob.Status status;
		
		public CommonJobCommand.ModifyRequestCommand toCommand() {
			return CommonJobCommand.ModifyRequestCommand.builder()
				.name(name)
				.className(className)
				.sql(sql)
				.remark(remark)
				.commonJobGroupId(commonJobGroupId)
				.jobType(jobType)
				.status(status)
				.systemStatus(CommonJob.SystemStatus.ENABLE)
				.build();
		}
	}

	@ApiModel("CommonJob management response")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobResponse {
		@ApiModelProperty(value = "CommonJob ID", example = "017e8400-e29b-41d4-a716-446655440000", required = true)
		private UUID id;

		@ApiModelProperty(value = "작업 이름", example = "테스트 작업", required = true)
		private String name;

		@ApiModelProperty(value = "클래스명", example = "com.winitech.system.infrastructure.job.TestJob")
		private String className;

		@ApiModelProperty(value = "쿼리", example = "INSERT INTO TEST VALUES(1)")
		private String sql;

		@ApiModelProperty(value = "비고", example = "작업에 대한 비고입니다.")
		private String remark;

		@ApiModelProperty(value = "작업 그룹 ID", example = "019587e2-ac46-1111-2222-e171cefc9395", required = true)
		private UUID commonJobGroupId;

		@ApiModelProperty(value = "작업유형", example = "JAVA", required = true, allowableValues = "JAVA, SQL")
		private String jobType;

		@ApiModelProperty(value = "작업상태", example = "ENABLE", required = true, allowableValues = "ENABLE, DISABLE")
		private String status;
		
		@ApiModelProperty(value = "적용상태", example = "PENDING", allowableValues = "PENDING, SUCCESSFUL, FAILED")
		private CommonJob.ApplyStatus applyStatus;

		public CommonJobResponse(CommonJobInfo commonJob) {
			this.id = commonJob.getId();
			this.name = commonJob.getName();
			this.className = commonJob.getClassName();
			this.sql = commonJob.getSql();
			this.remark = commonJob.getRemark();
			this.commonJobGroupId = commonJob.getCommonJobGroupInfo() == null ? null : commonJob.getCommonJobGroupInfo().getId();
			this.jobType = commonJob.getJobType() == null ? null : commonJob.getJobType().name();
			this.status = commonJob.getStatus() == null ? null : commonJob.getStatus().name();
			this.applyStatus = commonJob.getApplyStatus() == null ? null : commonJob.getApplyStatus();
		}
	}

	@ApiModel("CommonJob management store response")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobStoreResponse {
		@ApiModelProperty(value = "CommonJob ID", example = "017e8400-e29b-41d4-a716-446655440000", required = true)
		private String id;
	}
}
