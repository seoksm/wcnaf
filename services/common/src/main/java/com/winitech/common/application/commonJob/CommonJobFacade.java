package com.winitech.common.application.commonJob;

import com.winitech.common.domain.commonJob.CommonJob;
import com.winitech.common.domain.commonJobTrigger.CommonJobTrigger;
import com.winitech.common.domain.commonJobTrigger.CommonJobTriggerInfo;
import com.winitech.common.domain.commonJobTrigger.CommonJobTriggerService;
import com.winitech.common.exception.IllegalStatusException;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.common.library.WiniString;
import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.common.domain.commonJob.CommonJobCommand;
import com.winitech.common.domain.commonJob.CommonJobInfo;
import com.winitech.common.domain.commonJob.CommonJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.domain.commonJob
 * └ CommonJob.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:37
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class CommonJobFacade {
	private final String MANAGED_JOB_NAME_PREFIX = "WJ-";

	private final CommonJobService commonJobService;
	private final CommonJobTriggerService commonJobTriggerService;
	private final SchedulerFactoryBean schedulerFactoryBean;
	
	public CommonJobInfo registerCommonJob(CommonJobCommand.RegisterRequestCommand commonJobCommand) {
		return commonJobService.registerCommonJob(commonJobCommand);
	}

	public CommonJobInfo modifyCommonJob(UUID id, CommonJobCommand.ModifyRequestCommand commonJobCommand) {
		return commonJobService.modifyCommonJob(id, commonJobCommand);
	}

	public void removeCommonJob(UUID id) {
		commonJobService.removeCommonJob(id);
	}

	public CommonJobInfo searchCommonJobById(UUID id) {
		return commonJobService.searchCommonJobById(id);
	}

	public List<CommonJobInfo> getAllCommonJob() {
		return commonJobService.getAllCommonJob();
	}
	
	public List<CommonJobInfo> getActiveCommonJob() {
		return commonJobService.getActiveCommonJob();
	}

	public WiniPageInfo<CommonJobInfo> searchCommonJobPage(UUID commonJobGroupId, Integer page, Integer pageSize, String searchType, String searchKeyword, CommonJob.Status status, CommonJob.JobType jobType) {
		return commonJobService.searchCommonJobPage(commonJobGroupId, page, pageSize, searchType, searchKeyword, status, jobType);
	}

	public void registerAuto(List<CommonJobCommand.RegisterAutoCommand> registerAutoCommand) {
		commonJobService.registerAuto(registerAutoCommand);
	}
	
	public void syncAllCommonJob(boolean ignoreClassNotFound) {
		List<CommonJobInfo> commonJobList = commonJobService.getActiveCommonJob();
		List<CommonJobTriggerInfo> commonJobTriggerList = commonJobTriggerService.getAllCommonJobTrigger();

		Map<UUID, CommonJobInfo> commonJobMap = commonJobList
				.stream()
				.collect(Collectors.toMap(CommonJobInfo::getId, Function.identity()));

		Map<UUID, List<CommonJobTriggerInfo>> commonJobTriggerMap = commonJobTriggerList
				.stream()
				.collect(Collectors.groupingBy(it -> it.getCommonJob().getId()));

		Scheduler scheduler = schedulerFactoryBean.getScheduler();

		Set<JobKey> jobKeySet;
		
		try {
			jobKeySet = scheduler.getJobKeys(GroupMatcher.anyJobGroup());
		} catch (SchedulerException e) {
			throw new IllegalStatusException();
		}

		removeUnusedJobs(jobKeySet, commonJobMap, commonJobTriggerMap);
		
		applyChangedJobs(commonJobMap, commonJobTriggerMap, ignoreClassNotFound);
		
		commonJobService.markAsAppliedExcept(commonJobMap.values()
				.stream()
				.map(CommonJobInfo::getId)
				.collect(Collectors.toList()));
		
		commonJobTriggerService.markAsAppliedExcept(commonJobTriggerList
				.stream()
				.map(CommonJobTriggerInfo::getId)
				.collect(Collectors.toList()));
	}

	private void removeUnusedJobs(Set<JobKey> jobKeySet, Map<UUID, CommonJobInfo> commonJobMap, Map<UUID, List<CommonJobTriggerInfo>> commonJobTriggerMap) {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		
		// commonJob에서 삭제된 작업 quartz에서 삭제
		for (JobKey jobKey : jobKeySet) {
			if (! jobKey.getName().startsWith(MANAGED_JOB_NAME_PREFIX)) {
				continue;
			}
					
			String jobName = jobKey.getName().substring(MANAGED_JOB_NAME_PREFIX.length());
			
			if (! WiniString.isUuid(jobName)) {
				continue;
			}

			UUID commonJobId = UUID.fromString(jobName);
			CommonJobInfo commonJobInfo = commonJobMap.get(commonJobId);
			List<CommonJobTriggerInfo> commonJobTriggerInfo = commonJobTriggerMap.get(commonJobId);
			
			if (commonJobTriggerInfo == null || ! jobKey.getGroup().equals(commonJobInfo.getCommonJobGroupInfo().getName())) {
				// JobKey에 해당하는 CommonJobTrigger가 존재하지 않는 경우 quartz 스케쥴러에서 job 삭제
				
				try {
					scheduler.deleteJob(jobKey);
				} catch (SchedulerException e) {
					throw new RuntimeException(e);
				}
				
				continue;
			}
			
			Map<String, CommonJobTriggerInfo> commonJobTriggerInfoMap = commonJobTriggerInfo
					.stream()
					.collect(Collectors.toMap(it -> it.getId().toString(), Function.identity()));

			List<? extends Trigger> triggersOfJob;
			
			try {
				triggersOfJob = scheduler.getTriggersOfJob(jobKey);
			} catch (SchedulerException e) {
				throw new RuntimeException(e);
			}

			for (Trigger trigger : triggersOfJob) {
				if (commonJobTriggerMap.containsKey(trigger.getKey().getName())) {
					continue;	
				}
				
				// Trigger에 해당하는 CommonJobTrigger가 존재하지 않는 경우 quartz 스케쥴러에서 trigger 삭제
				try {
					scheduler.unscheduleJob(trigger.getKey());
				} catch (SchedulerException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private void applyChangedJobs(Map<UUID, CommonJobInfo> commonJobMap, Map<UUID, List<CommonJobTriggerInfo>> commonJobTriggerMap, boolean ignoreClassNotFound) {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
	
		commonJobMap.values().forEach(commonJobInfo -> {
			if (commonJobInfo.getCommonJobGroupInfo() == null || commonJobInfo.getStatus() == CommonJob.Status.DISABLE) {
				// commonJobGroup이 없는 경우
				return;
			}
			
			JobKey jobKey = JobKey.jobKey(MANAGED_JOB_NAME_PREFIX + commonJobInfo.getId().toString(), commonJobInfo.getCommonJobGroupInfo().getName());

			// trigger가 없는 경우 Job을 안 만들려고 했었는데 수동으로 트리거를 걸 수 있으므로
			// trigger가 없어도 Job을 등록하도록 함
			
			JobDetail jobDetail;
			
			try {
				jobDetail = scheduler.getJobDetail(jobKey);
			} catch (SchedulerException e) {
				throw new RuntimeException(e);
			}

			boolean isJobModified = commonJobInfo.getApplyStatus() != CommonJob.ApplyStatus.SUCCESSFUL;
			
			if (jobDetail == null || isJobModified) {
				// 해당 commonJob이 등록되어 있지 않거나 변경된 경우 quartz 스케쥴러에 job 등록
				
				try {
					String className = commonJobInfo.getClassName();
					Map<String, Object> jobData = new HashMap<>();

					switch (commonJobInfo.getJobType()) {
						case JAVA:
							break;
						case SQL:
							className = "com.winitech.common.infrastructure.commonJob.CommonJobSqlScheduleJob";
							
							jobData.put("sql", commonJobInfo.getSql());
							break;
						default:
							throw new IllegalStatusException("지원하지 않는 JobType입니다 : " + commonJobInfo.getJobType());
					}
					
					Class<? extends Job> klass;
					
					try {
						klass = (Class<? extends Job>) this.getClass().getClassLoader().loadClass(className);
					} catch (ClassNotFoundException e) {
						if (ignoreClassNotFound) {
							// 클래스가 존재하지 않는 경우 무시	
							log.warn("Class not found for job: " + className + ". Job will not be registered.");
							return;
						} else {
							throw new RuntimeException(e);
						}
					}
					
					jobDetail = JobBuilder.newJob(klass)
							.withIdentity(jobKey)
							.withDescription(commonJobInfo.getRemark())
							.usingJobData(new JobDataMap(jobData))
							.storeDurably()
							.build();
					
					scheduler.addJob(jobDetail, true);

					commonJobService.setApplyStatus(commonJobInfo.getId(), CommonJob.ApplyStatus.SUCCESSFUL);
				} catch (SchedulerException e) {
					throw new RuntimeException(e);
				}
			}
			
			List<CommonJobTriggerInfo> commonJobTriggerInfos = commonJobTriggerMap.get(commonJobInfo.getId());

			if (commonJobTriggerInfos == null) {
				// job만 있고 trigger가 없는 경우 생성하지 않음
				return;
			}

			Map<TriggerKey, ? extends Trigger> triggerMap;

			try {
				triggerMap = scheduler.getTriggersOfJob(jobKey)
						.stream()
						.collect(Collectors.toMap(Trigger::getKey, Function.identity()));
			} catch (SchedulerException e) {
				throw new RuntimeException(e);
			}

			for (CommonJobTriggerInfo commonJobTriggerInfo : commonJobTriggerInfos) {
				TriggerKey triggerKey = TriggerKey.triggerKey(commonJobTriggerInfo.getId().toString());

				if (triggerMap.containsKey(triggerKey)) {
					// 이미 동일한 트리거가 있는 경우
					
					if (commonJobTriggerInfo.getApplyStatus() == CommonJobTrigger.ApplyStatus.SUCCESSFUL) {
						// trigger가 최신 상태이면 무시
						continue;	
					}

					try {
						// 트리거 재등록을 위해 기존 트리거 삭제
						scheduler.unscheduleJob(triggerKey);
					} catch (SchedulerException e) {
						throw new RuntimeException(e);
					}
				}
				
				// 트리거 등록
				Trigger trigger;
				
				switch (commonJobTriggerInfo.getTriggerType()) {
					case CRON:
						trigger = TriggerBuilder.newTrigger()
								.withIdentity(triggerKey)
								.withDescription(commonJobTriggerInfo.getName())
								.forJob(jobKey)
								.withSchedule(CronScheduleBuilder.cronSchedule(commonJobTriggerInfo.getTriggerCron()))
								.build();
						break;
					case SECONDS:
						trigger = TriggerBuilder.newTrigger()
								.withIdentity(triggerKey)
								.withDescription(commonJobTriggerInfo.getName())
								.forJob(jobKey)
								.startNow()
								.withSchedule(SimpleScheduleBuilder.simpleSchedule()
										.withIntervalInSeconds(commonJobTriggerInfo.getTriggerSeconds().intValue())
										.repeatForever())
								.build();
						break;
					default:
						throw new IllegalStatusException("지원하지 않는 TriggerType입니다 : " + commonJobTriggerInfo.getTriggerType());
				}
				
				try {
					scheduler.scheduleJob(trigger);
					
					commonJobTriggerService.setApplyStatus(commonJobTriggerInfo.getId(), CommonJobTrigger.ApplyStatus.SUCCESSFUL);
				} catch (SchedulerException e) {
					commonJobTriggerService.setApplyStatus(commonJobTriggerInfo.getId(), CommonJobTrigger.ApplyStatus.FAILED);

					throw new RuntimeException(e);
				}
			}
		});
	}

	public OffsetDateTime getLastPendingUpdateAt() {
		return commonJobService.getLastPendingUpdateAt();
	}

	public void triggerCommonJob(UUID commonJobId) {
		triggerCommonJob(commonJobId, null);
	}
	
	public void triggerCommonJob(UUID commonJobId, Map<String, Object> params) {
		CommonJobInfo commonJobInfo = commonJobService.searchCommonJobById(commonJobId);
		
		JobKey jobKey = new JobKey("WJ-" + commonJobInfo.getId().toString(), commonJobInfo.getCommonJobGroupInfo().getName());

		Scheduler scheduler = schedulerFactoryBean.getScheduler();

		try {
			if (params == null) {
				scheduler.triggerJob(jobKey);
			} else {
				scheduler.triggerJob(jobKey, new JobDataMap(params));
			}
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public void triggerCommonJob(String jobGroupName, String jobName, Map<String, Object> params) {
		CommonJobInfo commonJobInfo = commonJobService.searchCommonJobByName(jobGroupName, jobName);

		triggerCommonJob(commonJobInfo.getId(), params);
	}

	public void cancelCommonJob(UUID commonJobId) {
		CommonJobInfo commonJobInfo = commonJobService.searchCommonJobById(commonJobId);

		JobKey jobKey = new JobKey("WJ-" + commonJobInfo.getId().toString(), commonJobInfo.getCommonJobGroupInfo().getName());

		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		try {
			scheduler.interrupt(jobKey);
		} catch (SchedulerException e) {
			throw new InvalidParamException("Failed to trigger job");
		}
	}
}
