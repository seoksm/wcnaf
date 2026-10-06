package com.winitech.apiGateway.domain.userSessionBlock;

import com.winitech.common.annotation.WiniJobSchedule;
import com.winitech.common.domain.commonJobTrigger.CommonJobTrigger;
import com.winitech.common.infrastructure.commonJob.BaseWiniJobBean;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * <pre>
 * com.winitech.apiGateway.infrastructure.userSessionBlock
 * └ UserSessionBlockCleanupJob.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-21 17:59
 **/
@Component
@DisallowConcurrentExecution
@ConditionalOnExpression("${spring.quartz.auto-startup:false}")
@WiniJobSchedule(value = "UserSessionBlockCleanupJob", jobGroupName = "System", triggerType = CommonJobTrigger.TriggerType.SECONDS, triggerSeconds = 20L, triggerEnabled = true)
public class UserSessionBlockCleanupJob extends BaseWiniJobBean {
	private final UserSessionBlockService userSessionBlockService;
	
	public UserSessionBlockCleanupJob(UserSessionBlockService userSessionBlockService) {
		this.userSessionBlockService = userSessionBlockService;
	}

	@Override
	protected void executeJob(JobExecutionContext context) {
		userSessionBlockService.removeExpiredAccessToken();
	}
}
