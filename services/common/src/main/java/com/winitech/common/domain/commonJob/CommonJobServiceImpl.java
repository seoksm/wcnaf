package com.winitech.common.domain.commonJob;

import com.winitech.common.domain.common.CommonAuthorizationGroupPermissionStore;
import com.winitech.common.domain.commonJobGroup.CommonJobGroup;
import com.winitech.common.domain.commonJobGroup.CommonJobGroupReader;
import com.winitech.common.domain.commonJobGroup.CommonJobGroupStore;
import com.winitech.common.domain.commonJobTrigger.CommonJobTrigger;
import com.winitech.common.domain.commonJobTrigger.CommonJobTriggerStore;
import com.winitech.common.exception.IllegalStatusException;
import com.winitech.common.library.commonType.WiniPageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.quartz.CronExpression;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
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
@Transactional
public class CommonJobServiceImpl extends EgovAbstractServiceImpl implements CommonJobService {
	private final CommonJobStore commonJobStore;
	private final CommonJobReader commonJobReader;
	private final CommonJobGroupReader commonJobGroupReader;
	private final CommonJobGroupStore commonJobGroupStore;
	private final CommonJobTriggerStore commonJobTriggerStore;

	@Override
	public CommonJobInfo registerCommonJob(CommonJobCommand.RegisterRequestCommand commonJobCommand) {
		CommonJobGroup commonJobGroup = commonJobCommand.getCommonJobGroupId() == null ? null : commonJobGroupReader.getCommonJobGroupById(commonJobCommand.getCommonJobGroupId());

		if (commonJobGroup != null && commonJobReader.existCommonJobByExcludingSelf(commonJobGroup.getName(), commonJobCommand.getName(), UUID.randomUUID())) {
			throw new IllegalStatusException("Already registered CommonJob name.");
		}

		CommonJob initCommonJob = CommonJob.builder()
				.name(commonJobCommand.getName())
				.className(commonJobCommand.getClassName())
				.sql(commonJobCommand.getSql())
				.remark(commonJobCommand.getRemark())
				.commonJobGroup(commonJobGroup)
				.jobType(commonJobCommand.getJobType())
				.status(commonJobCommand.getStatus())
				.systemStatus(CommonJob.SystemStatus.ENABLE)
				.applyStatus(CommonJob.ApplyStatus.PENDING)
				.build();

		CommonJob commonJob = commonJobStore.store(initCommonJob);
		return new CommonJobInfo(commonJob);
	}

	/**
	 * 어플리케이션 기동시 @WiniJobSchedule 어노테이션이 붙은 Job을 자동으로 등록하는 메소드
	 * @param registerAutoCommandList 자동등록할 Job 정보
	 */
	@Override
	public void registerAuto(List<CommonJobCommand.RegisterAutoCommand> registerAutoCommandList) {
		List<String> classNameList = registerAutoCommandList.stream()
				.map(CommonJobCommand.RegisterAutoCommand::getClassName)
				.distinct()
				.collect(Collectors.toList());
		
		Set<String> existingClassNameSet = commonJobReader.getExistingClassNameListByClassNameAndJobGroupName(classNameList)
				.stream()
				.collect(Collectors.toSet());
		
		// 등록할 Job만 추려냄
		List<CommonJobCommand.RegisterAutoCommand> newRegisterAutoCommandList = registerAutoCommandList.stream()
				.filter(registerAutoCommand -> !existingClassNameSet.contains(registerAutoCommand.getClassName()))
				.collect(Collectors.toList());
		
		if (newRegisterAutoCommandList.size() == 0) {
			// 등록할 Job이 없으면 종료
			return;
		}

		List<String> jobGroupNameList = newRegisterAutoCommandList
				.stream()
				.map(CommonJobCommand.RegisterAutoCommand::getJobGroupName)
				.distinct()
				.collect(Collectors.toList());

		// 작업이 한꺼번에 등록되는 일은 없으므로 퍼포먼스에 문제가 없으니, 한건씩 등록함

		Map<String, List<CommonJobGroup>> nameToCommonJobGroupMap = commonJobGroupReader.getCommonJobGroupByName(jobGroupNameList)
				.stream()
				.collect(Collectors.groupingBy(CommonJobGroup::getName));
		
		for (String jobGroupName : jobGroupNameList) {
			List<CommonJobGroup> commonJobGroupList = nameToCommonJobGroupMap.get(jobGroupName);
			if (commonJobGroupList != null && commonJobGroupList.size() > 0) {
				// 등록할 JobGroup이 있으면 다음으로
				continue;
			}
			
			log.info("자동 등록할 JobGroup : {}", jobGroupName);
			
			CommonJobGroup initCommonJobGroup = CommonJobGroup.builder()
					.name(jobGroupName)
					.remark("자동 등록됨")
					.status(CommonJobGroup.Status.ENABLE)
					.systemStatus(CommonJobGroup.SystemStatus.ENABLE)
					.build();
			
			commonJobGroupStore.store(initCommonJobGroup);
			
			nameToCommonJobGroupMap.put(jobGroupName, Arrays.asList(initCommonJobGroup));
		}
		
		for (CommonJobCommand.RegisterAutoCommand command : newRegisterAutoCommandList) {
			log.info("자동 등록할 Job : {} / {}", command.getJobName(), command.getClassName());

			if (commonJobReader.existCommonJobByExcludingSelf(command.getJobGroupName(), command.getJobName(), UUID.randomUUID())) {
				throw new IllegalStatusException("Already registered CommonJob name.");
			}

			CommonJob initCommonJob = CommonJob.builder()
					.name(command.getJobName())
					.commonJobGroup(nameToCommonJobGroupMap.get(command.getJobGroupName()).get(0))
					.className(command.getClassName())
					.remark(command.getRemark())
					.jobType(CommonJob.JobType.JAVA)
					.status(CommonJob.Status.ENABLE)
					.systemStatus(CommonJob.SystemStatus.ENABLE)
					.applyStatus(CommonJob.ApplyStatus.PENDING)
					.build();

			commonJobStore.store(initCommonJob);
			
			if (command.getTriggerType() == CommonJobTrigger.TriggerType.CRON) {
				if (command.getTriggerCron() == null || CronExpression.isValidExpression(command.getTriggerCron()) == false) {
					throw new IllegalStatusException("Invalid trigger cron expression.");					
				}
			}
			
			CommonJobTrigger initCommonJobTrigger = CommonJobTrigger.builder()
					.name(command.getJobName() + " trigger")
					.triggerType(command.getTriggerType())
					.triggerSeconds(command.getTriggerSeconds())
					.triggerCron(command.getTriggerCron())
					.status(command.getTriggerEnabled() ? CommonJobTrigger.Status.ENABLE : CommonJobTrigger.Status.DISABLE)
					.systemStatus(CommonJobTrigger.SystemStatus.ENABLE)
					.applyStatus(CommonJobTrigger.ApplyStatus.PENDING)
					.commonJob(initCommonJob)
					.build();

			commonJobTriggerStore.store(initCommonJobTrigger);
		}
	}

	@Override
	public CommonJobInfo modifyCommonJob(UUID id, CommonJobCommand.ModifyRequestCommand commonJobCommand) {
		CommonJobGroup commonJobGroup = commonJobCommand.getCommonJobGroupId() == null ? null : commonJobGroupReader.getCommonJobGroupById(commonJobCommand.getCommonJobGroupId());

		if (commonJobGroup != null && commonJobReader.existCommonJobByExcludingSelf(commonJobGroup.getName(), commonJobCommand.getName(), id)) {
			throw new IllegalStatusException("Already registered CommonJob name.");
		}

		CommonJob modifyCommonJob = commonJobReader.getCommonJobById(id);

		CommonJob commonJob = commonJobStore.modify(modifyCommonJob, commonJobCommand);
		return new CommonJobInfo(commonJob);
	}

	@Override
	public void removeCommonJob(UUID id) {
		commonJobStore.remove(id);
	}

	@Override
	public CommonJobInfo searchCommonJobById(UUID id) {
		CommonJob commonJob = commonJobReader.getCommonJobById(id);
		return new CommonJobInfo(commonJob);
	}

	@Override
	public CommonJobInfo searchCommonJobByName(String jobGroupName, String jobName) {
		CommonJob commonJob = commonJobReader.getCommonJobByName(jobGroupName, jobName);
		return new CommonJobInfo(commonJob);
	}

	@Override
	public List<CommonJobInfo> getAllCommonJob() {
		List<CommonJob> commonJobList = commonJobReader.getAllCommonJob();
		return commonJobList.stream()
				.map(CommonJobInfo::new)
				.collect(Collectors.toList());
	}
	
	public List<CommonJobInfo> getActiveCommonJob() {
		List<CommonJob> commonJobList = commonJobReader.getActiveCommonJob();
		return commonJobList.stream()
				.map(CommonJobInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public WiniPageInfo<CommonJobInfo> searchCommonJobPage(UUID commonJobGroupId, Integer page, Integer pageSize, String searchType, String searchKeyword, CommonJob.Status status, CommonJob.JobType jobType) {
		Page<CommonJobInfo> commonJobPage = commonJobReader.getCommonJobPage(commonJobGroupId, page, pageSize, searchType, searchKeyword, status, jobType);

		return new WiniPageInfo<>(commonJobPage);
	}
	
	@Override
	public void setApplyStatus(UUID commonJobId, CommonJob.ApplyStatus applyStatus) {
		commonJobStore.setApplyStatus(commonJobId, applyStatus);
	}

	@Override
	public OffsetDateTime getLastPendingUpdateAt() {
		return commonJobReader.getLastPendingUpdateAt();
	}

	@Override
	public void markAsAppliedExcept(List<UUID> commonJobIdList) {
		commonJobStore.markAsAppliedExcept(commonJobIdList);
	}
}
