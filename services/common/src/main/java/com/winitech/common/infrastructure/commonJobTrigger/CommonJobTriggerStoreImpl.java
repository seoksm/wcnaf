package com.winitech.common.infrastructure.commonJobTrigger;

import com.winitech.common.domain.commonJob.CommonJob;
import com.winitech.common.domain.commonJob.CommonJobReader;
import com.winitech.common.domain.commonJobTrigger.CommonJobTrigger;
import com.winitech.common.domain.commonJobTrigger.CommonJobTriggerCommand;
import com.winitech.common.domain.commonJobTrigger.CommonJobTriggerReader;
import com.winitech.common.domain.commonJobTrigger.CommonJobTriggerStore;
import com.winitech.common.exception.InvalidParamException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.commonJobTrigger
 * └ CommonJobTrigger.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:45
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonJobTriggerStoreImpl implements CommonJobTriggerStore {
	private final CommonJobTriggerRepository commonJobTriggerRepository;
	private final CommonJobTriggerReader commonJobTriggerReader;
	private final CommonJobReader commonJobReader;

	@Override
	public CommonJobTrigger store(CommonJobTrigger commonJobTrigger) {
		if (commonJobTrigger.getStatus() == CommonJobTrigger.Status.ENABLE) {
			validate(commonJobTrigger);
		}
		
		return commonJobTriggerRepository.save(commonJobTrigger);
	}

	@Override
	public CommonJobTrigger modify(CommonJobTrigger commonJobTrigger, CommonJobTriggerCommand.ModifyRequestCommand command) {
		if (command.getTriggerType() == null) throw new InvalidParamException("TriggerType is null");
		if (command.getTriggerType() == CommonJobTrigger.TriggerType.CRON && ! CronExpression.isValidExpression(command.getTriggerCron())) {
			throw new InvalidParamException("CronExpression is invalid");
		}		
		
		// commonJobTrigger.setCommonJob(command.getCommonJobId() == null ? null : commonJobReader.getCommonJobById(command.getCommonJobId()));
		commonJobTrigger.setName(command.getName());
		commonJobTrigger.setTriggerCron(command.getTriggerCron());
		commonJobTrigger.setTriggerSeconds(command.getTriggerSeconds());
		commonJobTrigger.setTriggerType(command.getTriggerType());
		commonJobTrigger.setStatus(command.getStatus());
		commonJobTrigger.setApplyStatus(CommonJobTrigger.ApplyStatus.PENDING);

		if (commonJobTrigger.getStatus() == CommonJobTrigger.Status.ENABLE) {
			validate(commonJobTrigger);
		}
		
		return commonJobTriggerRepository.save(commonJobTrigger);
	}

	@Override
	public void remove(UUID commonJobId, UUID commonJobTriggerId) {
		CommonJobTrigger commonJobTrigger = commonJobTriggerReader.getCommonJobTrigger(commonJobId, commonJobTriggerId);
		commonJobTrigger.disable();
		commonJobTriggerRepository.save(commonJobTrigger);
	}

	@Override
	public void setApplyStatus(UUID commonJobTriggerId, CommonJobTrigger.ApplyStatus applyStatus) {
		CommonJobTrigger commonJobTrigger = commonJobTriggerReader.getCommonJobTriggerById(commonJobTriggerId);
		commonJobTrigger.setApplyStatus(applyStatus);
		
		if (applyStatus == CommonJobTrigger.ApplyStatus.SUCCESSFUL) {
			commonJobTrigger.setAppliedAt(OffsetDateTime.now());
		} else {
			commonJobTrigger.setAppliedAt(null);
		}
		
		commonJobTriggerRepository.save(commonJobTrigger);
	}

	@Override
	public void markAsAppliedExcept(List<UUID> commonJobTriggerIdList) {
		commonJobTriggerRepository.markAsAppliedExcept(commonJobTriggerIdList);
	}

	private void validate(CommonJobTrigger commonJobTrigger) {
		if (commonJobTrigger.getTriggerType() == null) {
			throw new InvalidParamException("TriggerType is null");
		}

		switch (commonJobTrigger.getTriggerType()) {
			case CRON:
				if (commonJobTrigger.getTriggerCron() == null) {
					throw new InvalidParamException("TriggerCron is null");
				}
				
				if (org.quartz.CronExpression.isValidExpression(commonJobTrigger.getTriggerCron()) == false) {
					throw new InvalidParamException("TriggerCron is invalid");
				}
				break;
			case SECONDS:
				if (commonJobTrigger.getTriggerSeconds() == null) {
					throw new InvalidParamException("TriggerSeconds is null");
				}
				
				if (commonJobTrigger.getTriggerSeconds() < 1) {
					throw new InvalidParamException("TriggerSeconds is less than 1");
				}
				break;
			default:
				throw new InvalidParamException("Invalid JobType");
		}
	}
}
