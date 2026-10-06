package com.winitech.common.infrastructure.commonJob;

import com.winitech.common.exception.EntityNotFoundException;

import com.winitech.common.library.WiniCom;
import com.winitech.common.domain.commonJob.CommonJob;
import com.winitech.common.domain.commonJob.CommonJobInfo;
import com.winitech.common.domain.commonJob.CommonJobReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

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
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonJobReaderImpl implements CommonJobReader {
	private final CommonJobRepository commonJobRepository;
	private final CommonJobQueryRepository commonJobQueryRepository;

	@Override
	public CommonJob getCommonJobById(UUID commonJobId) {
		return commonJobRepository.findByIdAndSystemStatus(commonJobId, CommonJob.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public CommonJob getCommonJobByName(String jobGroupName, String jobName) {
		return commonJobRepository.findByNameAndSystemStatus(jobGroupName, jobName, CommonJob.SystemStatus.ENABLE)
				.orElseThrow(EntityNotFoundException::new);
	}

	//@Override
	//public CommonJob getCommonJobByCommonJobCode(String commonJobCode) {
	//	return commonJobRepository.findByCommonJobCodeAndSystemStatus(commonJobCode, CommonJob.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
	//}

	@Override
	public List<CommonJob> getAllCommonJob() {
		return commonJobRepository.findAllBySystemStatusOrderByIdDesc(CommonJob.SystemStatus.ENABLE);
	}

	@Override
	public List<CommonJob> getActiveCommonJob() {
		return commonJobRepository.findActiveCommonJob();
	}

	@Override
	public Page<CommonJobInfo> getCommonJobPage(UUID commonJobGroupId, Integer page, Integer pageSize, String searchType, String searchKeyword, CommonJob.Status status, CommonJob.JobType jobType) {
		return commonJobQueryRepository.findAllPage(commonJobGroupId, searchType, searchKeyword, WiniCom.getPageRequest(page, pageSize), status, jobType);
	}

	@Override
	public List<String> getExistingClassNameListByClassNameAndJobGroupName(List<String> classNameList) {
		return commonJobRepository.findAllByClassNameInAndSystemStatus(classNameList, CommonJob.SystemStatus.ENABLE);
	}

	@Override
	public OffsetDateTime getLastPendingUpdateAt() {
		return commonJobRepository.findLastPendingUpdateAtIncludeDisable();
	}

	@Override
	public boolean existCommonJobByExcludingSelf(String jobGroupName, String jobName, UUID commonJobId)  {
		return commonJobRepository.existsByNameAndIdNotAndSystemStatus(jobGroupName, jobName, commonJobId, CommonJob.SystemStatus.ENABLE);
	}
}
