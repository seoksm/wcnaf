package com.winitech.common.config;

import com.winitech.common.application.commonJob.CommonJobFacade;
import com.winitech.common.application.commonJobRun.CommonJobRunFacade;
import com.winitech.common.application.commonJobState.CommonJobStateFacade;
import com.winitech.common.domain.commonJob.CommonJobCommand;
import com.winitech.common.domain.commonJob.CommonJobInfo;
import com.winitech.common.domain.commonJobRun.CommonJobRun;
import com.winitech.common.domain.commonJobRun.CommonJobRunCommand;
import com.winitech.common.domain.commonJobRun.CommonJobRunInfo;
import com.winitech.common.domain.commonJobTrigger.CommonJobTrigger;
import com.winitech.common.library.WiniCom;
import com.winitech.common.library.WiniString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.winitech.common.annotation.WiniJobSchedule;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.time.OffsetDateTime;
import java.util.Map;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.common.config
 * └ SchedulerConfig.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-13 13:21
 **/
@Slf4j
@Configuration
@ConditionalOnExpression("${spring.quartz.auto-startup:false}")
@RequiredArgsConstructor
public class SchedulerConfig implements InitializingBean, ApplicationContextAware {
	private final String MANAGED_JOB_NAME_PREFIX = "WJ-";

	private final CommonJobFacade commonJobFacade;
	private final CommonJobRunFacade commonJobRunFacade;
	private final CommonJobStateFacade commonJobStateFacade;
	
	private final SchedulerFactoryBean schedulerFactoryBean;
	
	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// @WiniJobSchedule 어노테이션이 붙은 Job 목록 조회
		List<CommonJobCommand.RegisterAutoCommand> registerAutoCommand = getRegisterAutoCommandListByWiniJobScheduler();
		
		// @WiniJobSchedule 어노테이션이 붙은 Job 중 미등록된 Job 자동 등록
		commonJobFacade.registerAuto(registerAutoCommand);

		// CommonJob, CommonJobTrigger 정보와 실제 Quartz 스케줄러에 등록된 Job 정보 동기화
		// 시작시에는 Job에 해당하는 클래스가 존재하지 않더라도 예외를 발생시키지 않음
		commonJobFacade.syncAllCommonJob(true);
		
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		scheduler.getListenerManager().addJobListener(new SchedulerJobListener(), GroupMatcher.anyJobGroup());
	}

	private List<CommonJobCommand.RegisterAutoCommand> getRegisterAutoCommandListByWiniJobScheduler() {
		Map<String, Object> beans = applicationContext.getBeansWithAnnotation(WiniJobSchedule.class);

		return beans.values()
				.stream()
				.map(bean -> {
					WiniJobSchedule annotation = bean.getClass().getAnnotation(WiniJobSchedule.class);
					return CommonJobCommand.RegisterAutoCommand.builder()
							.jobGroupName(annotation.jobGroupName())
							.jobName(annotation.value())
							.className(bean.getClass().getName())
							.remark(annotation.remark())
							.triggerEnabled(annotation.triggerEnabled())
							.triggerType(annotation.triggerType())
							.triggerSeconds(annotation.triggerSeconds())
							.triggerCron(annotation.triggerCron())
							.build();
				})
				.collect(Collectors.toList());
	}

	private class SchedulerJobListener implements JobListener {
		@Override
		public String getName() {
			return "WiniCommonJobListener";
		}

		@Override
		public void jobToBeExecuted(JobExecutionContext context) {
			try {
                log.info("Job to be executed: " + context.getJobDetail().getKey());

                if (!context.getJobDetail().getKey().getName().startsWith(MANAGED_JOB_NAME_PREFIX)) {
                    // WJ-로 시작하지 않는 Job은 WiniCommonJobListener에서 관리하지 않음
                    return;
                }

                CommonJobRunCommand.RegisterRequestCommand.RegisterRequestCommandBuilder builder = CommonJobRunCommand.RegisterRequestCommand.builder();

                if (context.getTrigger() != null) {
                    String jobName = context.getTrigger().getKey().getName();

                    if (WiniString.isUuid(jobName)) {
                        builder = builder.commonJobTriggerId(UUID.fromString(jobName));
                    }
                }

                CommonJobRunCommand.RegisterRequestCommand commonJobRunCommand = builder
                        .commonJobId(UUID.fromString(context.getJobDetail().getKey().getName().substring(MANAGED_JOB_NAME_PREFIX.length())))
                        .startedAt(OffsetDateTime.now())
                        .jobStatus(CommonJobRun.JobStatus.PROCESSING)
                        .build();

                CommonJobRunInfo commonJobRunInfo = commonJobRunFacade.registerCommonJobRun(commonJobRunCommand);

                // commonJobState에 현재 실행 ID 저장
                commonJobStateFacade.setCurrentRun(commonJobRunCommand.getCommonJobId(), commonJobRunInfo.getId());

                context.getMergedJobDataMap().put("commonJobRunId", commonJobRunInfo.getId());
            }catch (NullPointerException e){
                log.warn("Job 실행 정보가 충분하지 않아 작업 실행 기록을 생성할 수 없습니다.", e);
			} catch (Exception _ignored) {
				// 무시
				log.warn("작업 시작 중 오류가 발생하였습니다.", _ignored);
			}
		}

		@Override
		public void jobExecutionVetoed(JobExecutionContext context) {
			log.info("Job execution vetoed: " + context.getJobDetail().getKey());
		}

		@Override
		public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
			try {
                log.info("Job was executed: " + context.getJobDetail().getKey());
                if (jobException != null) {
                    log.info("Job execution exception: " + jobException.getMessage());
                }

                UUID commonJobRunId = (UUID) context.getMergedJobDataMap().get("commonJobRunId");

                if (commonJobRunId != null) {
                    String message = "";

                    if (context.getResult() != null && context.getResult() instanceof String) {
                        message = (String) context.getResult();
                    }

                    CommonJobRun.JobStatus jobStatus = CommonJobRun.JobStatus.SUCCESSFUL;

                    if (jobException != null) {
                        if (WiniCom.getRootCause(jobException) instanceof InterruptedException) {
                            // 작업 실행이 취소된 경우
                            message = "작업이 취소되었습니다.";
                            jobStatus = CommonJobRun.JobStatus.CANCEL;
                        } else {
                            // 작업 실행 중 오류가 발생한 경우
                            message = jobException.getMessage();
                            jobStatus = CommonJobRun.JobStatus.FAILED;
                        }
                    }

                    CommonJobRunCommand.ModifyRequestCommand commonJobRunCommand = CommonJobRunCommand.ModifyRequestCommand.builder()
                            .endedAt(OffsetDateTime.now())
                            .jobStatus(jobStatus)
                            .message(WiniString.cut(message, 255))
                            .build();

                    CommonJobRunInfo commonJobRunInfo = commonJobRunFacade.modifyCommonJobRun(commonJobRunId, commonJobRunCommand);

                    // commonJobState에 현재 실행 ID 저장

                    if (jobException == null) {
                        // 성공시
                        commonJobStateFacade.setSuccessfulRun(commonJobRunInfo.getCommonJob().getId(), commonJobRunId, commonJobRunInfo.getEndedAt());
                    } else {
                        // 실패시
                        String errMsg = jobException.getMessage();

                        if (WiniCom.getRootCause(jobException) instanceof InterruptedException) {
                            errMsg = "작업이 취소되었습니다.";
                        }

                        commonJobStateFacade.setFailedRun(commonJobRunInfo.getCommonJob().getId(), commonJobRunId, commonJobRunInfo.getEndedAt(), errMsg);
                    }
                }
            } catch (NullPointerException e){
                log.warn("Job 실행 정보가 충분하지 않아 작업 실행 기록을 수정할 수 없습니다.", e);
			} catch (Exception _ignored) {
				// 무시	
				log.warn("작업 종료 처리 중 오류가 발생하였습니다.", _ignored);
			}
		}
	}
}
