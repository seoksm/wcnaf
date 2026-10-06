package com.winitech.common.domain.commonJobGroup;

import lombok.Getter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.domain.commonJobGroup
 * └ CommonJobGroup.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:36
 **/
@Getter
public class CommonJobGroupInfo {
	private final UUID id;
	private final String name;
	private final String remark;
	private final CommonJobGroup.Status status;
	private final CommonJobGroup.SystemStatus systemStatus;

	public CommonJobGroupInfo(CommonJobGroup commonJobGroup) {
		this.id = commonJobGroup.getId();
		this.name = commonJobGroup.getName();
		this.remark = commonJobGroup.getRemark();
		this.status = commonJobGroup.getStatus();
		this.systemStatus = commonJobGroup.getSystemStatus();
	}
}
