package com.winitech.common.interfaces.inboundAdapter.web.commonJobGroup;

import com.winitech.common.domain.commonJobGroup.CommonJobGroup;
import com.winitech.common.domain.commonJobGroup.CommonJobGroupCommand;
import com.winitech.common.domain.commonJobGroup.CommonJobGroupInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.commonJobGroup
 * └ CommonJobGroup.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:36
 **/
@NoArgsConstructor
public class CommonJobGroupDto {
	@ApiModel("CommonJobGroup management request")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobGroupRequest {
		@ApiModelProperty(value = "작업 그룹 이름", example = "테스트 작업 그룹", required = true)
		@NotBlank
		private String name;

		@ApiModelProperty(value = "비고", example = "작업에 대한 비고입니다.")
		private String remark;

		@ApiModelProperty(value = "상태", example = "ENABLE", required = true, allowableValues = "ENABLE, DISABLE")
		private CommonJobGroup.Status status;
		
		public CommonJobGroupCommand toCommand() {
			return CommonJobGroupCommand.builder()
				.name(name)
				.remark(remark)
				.status(status)
				.build();
		}
	}

	@ApiModel("CommonJobGroup management register request")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobGroupRegisterRequest {
		@ApiModelProperty(value = "작업 그룹 이름", example = "테스트 작업 그룹", required = true)
		@NotBlank
		private String name;

		@ApiModelProperty(value = "비고", example = "작업에 대한 비고입니다.")
		private String remark;

		@ApiModelProperty(value = "상태", example = "ENABLE", required = true, allowableValues = "ENABLE, DISABLE")
		private CommonJobGroup.Status status;
		
		public CommonJobGroupCommand.RegisterRequestCommand toCommand() {
			return CommonJobGroupCommand.RegisterRequestCommand.builder()
				.name(name)
				.remark(remark)
				.status(status)
				.build();
		}
	}

	@ApiModel("CommonJobGroup management modify request")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobGroupModifyRequest {
		@ApiModelProperty(value = "작업 그룹 이름", example = "테스트 작업 그룹", required = true)
		@NotBlank
		private String name;

		@ApiModelProperty(value = "비고", example = "작업에 대한 비고입니다.")
		private String remark;

		@ApiModelProperty(value = "상태", example = "ENABLE", required = true, allowableValues = "ENABLE, DISABLE")
		private CommonJobGroup.Status status;
		
		public CommonJobGroupCommand.ModifyRequestCommand toCommand() {
			return CommonJobGroupCommand.ModifyRequestCommand.builder()
				.name(name)
				.remark(remark)
				.status(status)
				.build();
		}
	}

	@ApiModel("CommonJobGroup management response")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobGroupResponse {
		@ApiModelProperty(value = "CommonJobGroup ID", example = "017e8400-e29b-41d4-a716-446655440000", required = true)
		private UUID commonJobGroupId;

		@ApiModelProperty(value = "작업 그룹 이름", example = "테스트 작업 그룹", required = true)
		private String name;

		@ApiModelProperty(value = "비고", example = "작업에 대한 비고입니다.")
		private String remark;

		@ApiModelProperty(value = "상태", example = "ENABLE", required = true, allowableValues = "ENABLE, DISABLE")
		private CommonJobGroup.Status status;
		
		public CommonJobGroupResponse(CommonJobGroupInfo commonJobGroup) {
			this.commonJobGroupId = commonJobGroup.getId();
			this.name = commonJobGroup.getName();
			this.remark = commonJobGroup.getRemark();
			this.status = commonJobGroup.getStatus();
		}
	}

	@ApiModel("CommonJobGroup management store response")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CommonJobGroupStoreResponse {
		@ApiModelProperty(value = "CommonJobGroup ID", example = "017e8400-e29b-41d4-a716-446655440000", required = true)
		private UUID commonJobGroupId;
	}
}
