package com.winitech.common.infrastructure.commonJob;

import com.winitech.common.application.commonJob.CommonJobFacade;
import com.winitech.common.application.commonJobTrigger.CommonJobTriggerFacade;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 자동으로 CommonJob과 Quartz Job을 동기화하는 Job
 * <pre>
 * com.winitech.common.infrastructure.commonJob
 * └ CommonJobSyncAllJob.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-19 09:56
 **/
@Component
@DisallowConcurrentExecution
@ConditionalOnExpression("${spring.quartz.auto-startup:false}")
public class CommonJobSyncAllJob extends QuartzJobBean implements ApplicationContextAware {
	private ApplicationContext applicationContext;
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		// Quartz Job과 CommonJob 동기화
		syncAllCommonJob();
		
		// trigger_state가 ERROR인 trigger를 모두 PENDING으로 변경
		resetErroredTrigger();
	}

	private void syncAllCommonJob() {
		CommonJobFacade commonJobFacade = applicationContext.getBean(CommonJobFacade.class);
		CommonJobTriggerFacade commonJobTriggerFacade = applicationContext.getBean(CommonJobTriggerFacade.class);

		OffsetDateTime lastJobPendingUpdateAt = commonJobFacade.getLastPendingUpdateAt();

		boolean isSyncNeeded = false;

		if (lastJobPendingUpdateAt != null && OffsetDateTime.now().isAfter(lastJobPendingUpdateAt.plusMinutes(1))) {
			isSyncNeeded = true;
		} else {
			OffsetDateTime lastTriggerPendingUpdateAt = commonJobTriggerFacade.getLastPendingUpdateAt();

			if (lastTriggerPendingUpdateAt != null && OffsetDateTime.now().isAfter(lastTriggerPendingUpdateAt.plusMinutes(1))) {
				isSyncNeeded = true;
			}
		}

		if (isSyncNeeded) {
			// 자동으로 동기화하는 작업에서는 Job에 해당하는 클래스가 존재하지 않더라도 예외를 발생시키지 않음
			commonJobFacade.syncAllCommonJob(true);
		}
	}

	private void resetErroredTrigger() {
		SchedulerFactoryBean schedulerFactoryBean = applicationContext.getBean(SchedulerFactoryBean.class);
		
		if (schedulerFactoryBean == null) {
			return;
		}

		Scheduler scheduler = schedulerFactoryBean.getScheduler();

		List<TriggerKey> errorTriggers = new ArrayList<>();
		Set<TriggerKey> triggerKeys = null;
		try {
			triggerKeys = scheduler.getTriggerKeys(GroupMatcher.anyTriggerGroup());

			for (TriggerKey triggerKey : triggerKeys) {
				Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
				if (triggerState == Trigger.TriggerState.ERROR) {
					errorTriggers.add(triggerKey);
				}
			}
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Bean
	public JobDetail syncAllJobDetail() {
		return JobBuilder.newJob(CommonJobSyncAllJob.class)
				.withIdentity("CommonJobSyncAllJob")
				.withDescription("주기적으로 CommonJob와 Quartz Job을 동기화하는 Job")
				.storeDurably()
				.build();
	}

	@Bean
	public Trigger syncAllJobTrigger(JobDetail job) {
		return TriggerBuilder.newTrigger()
				.forJob(job)
				.withIdentity("CommonJobSyncAllJobTrigger")
				.withDescription("주기적으로 CommonJob와 Quartz Job을 동기화")
				.withSchedule(SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInSeconds(60)
						.repeatForever())
				.startNow()
				.build();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
