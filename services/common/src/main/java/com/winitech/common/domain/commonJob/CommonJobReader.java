package com.winitech.common.domain.commonJob;

import org.springframework.data.domain.Page;

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
public interface CommonJobReader {
	CommonJob getCommonJobById(UUID commonJobId);
	CommonJob getCommonJobByName(String jobGroupName, String jobName);
	// CommonJob getCommonJobByCommonJobCode(String commonJobCode);
	List<CommonJob> getAllCommonJob();
	List<CommonJob> getActiveCommonJob();
	Page<CommonJobInfo> getCommonJobPage(UUID commonJobGroupId, Integer page, Integer pageSize, String searchType, String searchKeyword, CommonJob.Status status, CommonJob.JobType jobType);

	List<String> getExistingClassNameListByClassNameAndJobGroupName(List<String> classNameList);

	OffsetDateTime getLastPendingUpdateAt();
	
	boolean existCommonJobByExcludingSelf(String jobGroupName, String jobName, UUID commonJobId);
}
