package com.winitech.system.domain.code;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Builder
@ToString
public class CodeCommand {

	private UUID upperCodeId;
	private String code;
	private String codeName;
	private String codeDescription;
	private Integer codeDepthNo;
	private Integer codeOrderNo;
	private Code.UseStatus codeUseStatus;

	public Code toEntity(Code parent) {
		return Code.builder()
				.code(code)
				.name(codeName)
				.description(codeDescription)
				.depthNo(codeDepthNo)
				.orderNo(codeOrderNo)
				.useStatus(codeUseStatus)
				.parent(parent)
				.build();
	}

	@Getter
	@Builder
	public static class UpdateCommand {
		@Setter
		private UUID codeId;
		private String codeName;
		private String codeDescription;
		private Integer codeOrderNo;
		private Code.UseStatus codeUseStatus;
	}
}
