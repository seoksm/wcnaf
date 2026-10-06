package com.winitech.common.domain.commonJobGroup;

import io.swagger.annotations.ApiParam;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

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
@Getter
@Builder
@ToString
public class CommonJobGroupCommand {
	private final UUID id;
	private final String name;
	private final String remark;
	private final CommonJobGroup.Status status;
	private final CommonJobGroup.SystemStatus systemStatus;

	public CommonJobGroup toEntity() {
		return CommonJobGroup.builder()
			.id(id)
			.name(name)
			.remark(remark)
			.status(status)
			.systemStatus(systemStatus)
			.build();
	}

	@Getter
	@Builder
	@ToString
	public static class RegisterRequestCommand {
		private final String name;
		private final String remark;
		private final CommonJobGroup.Status status;
	}

	@Getter
	@Builder
	@ToString
	public static class ModifyRequestCommand {
		private final String name;
		private final String remark;
		private final CommonJobGroup.Status status;
	}
}
