package com.winitech.common.domain.commonJob;

import com.winitech.common.domain.commonJobGroup.CommonJobGroupInfo;
import lombok.Getter;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.commonJob
 * └ CommonJob.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:37
 **/
@Getter
public class CommonJobInfo {
	private final UUID id;
	private final String name;
	private final String className;
	private final String sql;
	private final String remark;
	private final CommonJob.ApplyStatus applyStatus;
	private final CommonJobGroupInfo commonJobGroupInfo;
	private final CommonJob.JobType jobType;
	private final CommonJob.Status status;
	private final CommonJob.SystemStatus systemStatus;

	public CommonJobInfo(CommonJob commonJob) {
		this.id = commonJob.getId();
		this.name = commonJob.getName();
		this.className = commonJob.getClassName();
		this.sql = commonJob.getSql();
		this.remark = commonJob.getRemark();
		this.applyStatus = commonJob.getApplyStatus();
		this.commonJobGroupInfo = commonJob.getCommonJobGroup() == null ? null : new CommonJobGroupInfo(commonJob.getCommonJobGroup());
		this.jobType = commonJob.getJobType();
		this.status = commonJob.getStatus();
		this.systemStatus = commonJob.getSystemStatus();
	}
}
