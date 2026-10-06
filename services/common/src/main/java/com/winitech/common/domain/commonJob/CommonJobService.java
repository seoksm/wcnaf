package com.winitech.common.domain.commonJob;

import com.winitech.common.library.commonType.WiniPageInfo;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.commonJob
 * └ CommonJob.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:37
 **/
public interface CommonJobService {
	CommonJobInfo registerCommonJob(CommonJobCommand.RegisterRequestCommand commonJobCommand);
	void registerAuto(List<CommonJobCommand.RegisterAutoCommand> registerAutoCommand);
	CommonJobInfo modifyCommonJob(UUID id, CommonJobCommand.ModifyRequestCommand commonJobCommand);
	void removeCommonJob(UUID id);
	CommonJobInfo searchCommonJobById(UUID id);
	List<CommonJobInfo> getAllCommonJob();
	List<CommonJobInfo> getActiveCommonJob();
	WiniPageInfo<CommonJobInfo> searchCommonJobPage(UUID commonJobGroupId, Integer page, Integer pageSize, String searchType, String searchKeyword, CommonJob.Status status, CommonJob.JobType jobType);
	
	void setApplyStatus(UUID commonJobId, CommonJob.ApplyStatus applyStatus);
	OffsetDateTime getLastPendingUpdateAt();
	void markAsAppliedExcept(List<UUID> commonJobIdList);

	CommonJobInfo searchCommonJobByName(String jobGroupName, String jobName);
}
