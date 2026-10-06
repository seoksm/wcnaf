package com.winitech.common.infrastructure.commonJobRun;

import com.winitech.common.domain.commonJob.CommonJobReader;
import com.winitech.common.domain.commonJobRun.CommonJobRun;
import com.winitech.common.domain.commonJobRun.CommonJobRunCommand;
import com.winitech.common.domain.commonJobRun.CommonJobRunReader;
import com.winitech.common.domain.commonJobRun.CommonJobRunStore;
import com.winitech.common.domain.commonJobTrigger.CommonJobTriggerReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.commonJobRun
 * └ CommonJobRun.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:48
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonJobRunStoreImpl implements CommonJobRunStore {
	private final CommonJobRunRepository commonJobRunRepository;
	private final CommonJobRunReader commonJobRunReader;
	private final CommonJobReader commonJobReader;
	private final CommonJobTriggerReader commonJobTriggerReader; 

	@Override
	public CommonJobRun store(CommonJobRun commonJobRun) {
		return commonJobRunRepository.save(commonJobRun);
	}

	@Override
	public CommonJobRun modify(CommonJobRun commonJobRun, CommonJobRunCommand.ModifyRequestCommand command) {
		//commonJobRun.setCommonJob(command.getCommonJobId() == null ? null : commonJobReader.getCommonJobById(command.getCommonJobId()));
		//commonJobRun.setCommonJobTrigger(command.getCommonJobTriggerId() == null ? null : commonJobTriggerReader.getCommonJobTriggerById(command.getCommonJobTriggerId()));
		//commonJobRun.setStartedAt(command.getStartedAt());
		commonJobRun.setEndedAt(command.getEndedAt());
		commonJobRun.setJobStatus(command.getJobStatus());
		commonJobRun.setMessage(command.getMessage());

		return commonJobRunRepository.save(commonJobRun);
	}

	@Override
	public void remove(UUID commonJobRunId) {
		CommonJobRun commonJobRun = commonJobRunReader.getCommonJobRunById(commonJobRunId);
		commonJobRun.disable();
		commonJobRunRepository.save(commonJobRun);
	}
}
