package com.winitech.common.infrastructure.commonJob;

import com.winitech.common.domain.commonJob.CommonJob;
import com.winitech.common.domain.commonJob.CommonJobCommand;
import com.winitech.common.domain.commonJob.CommonJobReader;
import com.winitech.common.domain.commonJob.CommonJobStore;
import com.winitech.common.domain.commonJobGroup.CommonJobGroupReader;
import com.winitech.common.exception.InvalidParamException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
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
public class CommonJobStoreImpl implements CommonJobStore {
	private final CommonJobRepository commonJobRepository;
	private final CommonJobReader commonJobReader;
	private final CommonJobGroupReader commonJobGroupReader;

	@Override
	public CommonJob store(CommonJob commonJob) {
		if (commonJob.getStatus() == CommonJob.Status.ENABLE) {
			validate(commonJob);
		}
		
		return commonJobRepository.save(commonJob);
	}

	@Override
	public CommonJob modify(CommonJob commonJob, CommonJobCommand.ModifyRequestCommand command) {
		commonJob.setName(command.getName());
		commonJob.setClassName(command.getClassName());
		commonJob.setSql(command.getSql());
		commonJob.setRemark(command.getRemark());
		commonJob.setJobType(command.getJobType());
		commonJob.setStatus(command.getStatus());
		commonJob.setApplyStatus(CommonJob.ApplyStatus.PENDING);

		if (commonJob.getStatus() == CommonJob.Status.ENABLE) {
			validate(commonJob);
		}

		return commonJobRepository.save(commonJob);
	}

	@Override
	public void remove(UUID commonJobId) {
		CommonJob commonJob = commonJobReader.getCommonJobById(commonJobId);
		commonJob.disable();
		commonJobRepository.save(commonJob);
	}
	
	@Override
	public void setApplyStatus(UUID commonJobId, CommonJob.ApplyStatus applyStatus) {
		CommonJob commonJob = commonJobReader.getCommonJobById(commonJobId);
		commonJob.setApplyStatus(applyStatus);
		
		if (applyStatus == CommonJob.ApplyStatus.SUCCESSFUL) {
			commonJob.setAppliedAt(OffsetDateTime.now());
		} else {
			commonJob.setAppliedAt(null);
		}
		
		commonJobRepository.save(commonJob);
	}
	
	public void markAsAppliedExcept(List<UUID> commonJobIdList) {
		commonJobRepository.markAsAppliedExcept(commonJobIdList);
	}

	private void validate(CommonJob commonJob) {
		if (commonJob.getJobType() == null) {
			throw new InvalidParamException("JobType is null");
		}

		switch (commonJob.getJobType()) {
			case SQL:
				if (commonJob.getSql() == null) {
					throw new InvalidParamException("SQL is null");
				}
				break;
			case JAVA:
				if (commonJob.getClassName() == null) {
					throw new InvalidParamException("ClassName is null");
				}
				break;
			default:
				throw new InvalidParamException("Invalid JobType");
		}
	}
}
