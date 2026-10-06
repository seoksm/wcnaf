package com.winitech.common.domain.commonJobTrigger;

import com.winitech.common.domain.commonJob.CommonJobReader;
import com.winitech.common.library.commonType.WiniPageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.domain.commonJobTrigger
 * └ CommonJobTrigger.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:45
 **/
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommonJobTriggerServiceImpl extends EgovAbstractServiceImpl implements CommonJobTriggerService {
	private final CommonJobTriggerStore commonJobTriggerStore;
	private final CommonJobTriggerReader commonJobTriggerReader;
	private final CommonJobReader commonJobReader;

	@Override
	public CommonJobTriggerInfo registerCommonJobTrigger(CommonJobTriggerCommand.RegisterRequestCommand commonJobTriggerCommand) {
		// 중복체크 필요시 주석해제
		//if (commonJobTriggerCommand.getCommonJobTriggerCode() != null && commonJobTriggerReader.existCommonJobTriggerByCommonJobTriggerCodeAndExcludingSelf(commonJobTriggerCommand.getCommonJobTriggerCode(), UUID.randomUUID())) {
		//	throw new IllegalStatusException("Already registered CommonJobTrigger code.");
		//}

		CommonJobTrigger initCommonJobTrigger = CommonJobTrigger.builder()
				.commonJob(commonJobReader.getCommonJobById(commonJobTriggerCommand.getCommonJobId()))
				.name(commonJobTriggerCommand.getName())
				.triggerCron(commonJobTriggerCommand.getTriggerCron())
				.triggerSeconds(commonJobTriggerCommand.getTriggerSeconds())
				.triggerType(commonJobTriggerCommand.getTriggerType())
				.status(commonJobTriggerCommand.getStatus())
				.applyStatus(CommonJobTrigger.ApplyStatus.PENDING)
				
				.systemStatus(CommonJobTrigger.SystemStatus.ENABLE)
				.build();

		CommonJobTrigger commonJobTrigger = commonJobTriggerStore.store(initCommonJobTrigger);
		return new CommonJobTriggerInfo(commonJobTrigger);
	}

	@Override
	public CommonJobTriggerInfo modifyCommonJobTrigger(UUID id, CommonJobTriggerCommand.ModifyRequestCommand commonJobTriggerCommand) {
		// 중복체크 필요시 주석해제
		//if (Boolean.TRUE.equals(commonJobTriggerReader.existCommonJobTriggerByCommonJobTriggerCodeAndExcludingSelf(commonJobTriggerCommand.getCommonJobTriggerCode(), id))) {
		//	throw new IllegalStatusException("Already registered CommonJobTrigger code.");
		//}

		CommonJobTrigger modifyCommonJobTrigger = commonJobTriggerReader.getCommonJobTriggerById(id);

		CommonJobTrigger commonJobTrigger = commonJobTriggerStore.modify(modifyCommonJobTrigger, commonJobTriggerCommand);
		return new CommonJobTriggerInfo(commonJobTrigger);
	}

	@Override
	public void removeCommonJobTrigger(UUID commonJobId, UUID commonJobTriggerId) {
		commonJobTriggerStore.remove(commonJobId, commonJobTriggerId);
	}

	@Override
	public CommonJobTriggerInfo searchCommonJobTriggerById(UUID id) {
		CommonJobTrigger commonJobTrigger = commonJobTriggerReader.getCommonJobTriggerById(id);
		return new CommonJobTriggerInfo(commonJobTrigger);
	}

	@Override
	public List<CommonJobTriggerInfo> getAllCommonJobTriggerByCommonJobId(UUID commonJobId) {
		List<CommonJobTrigger> commonJobTriggerList = commonJobTriggerReader.getAllCommonJobTriggerByCommonJobId(commonJobId);
		return commonJobTriggerList.stream()
				.map(CommonJobTriggerInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public WiniPageInfo<CommonJobTriggerInfo> searchCommonJobTriggerPage(UUID commonJobId, Integer page, Integer pageSize, CommonJobTrigger.Status status, String searchType, String searchKeyword) {
		Page<CommonJobTriggerInfo> commonJobTriggerPage = commonJobTriggerReader.getCommonJobTriggerPage(commonJobId, page, pageSize, status, searchType, searchKeyword);

		return new WiniPageInfo<>(commonJobTriggerPage);
	}

	@Override
	public List<CommonJobTriggerInfo> getAllCommonJobTrigger() {
		return commonJobTriggerReader.getAllCommonJobTrigger().stream()
				.map(CommonJobTriggerInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public void setApplyStatus(UUID commonJobTriggerId, CommonJobTrigger.ApplyStatus applyStatus) {
		commonJobTriggerStore.setApplyStatus(commonJobTriggerId, applyStatus);
	}

	@Override
	public OffsetDateTime getLastPendingUpdateAt() {
		return commonJobTriggerReader.getLastPendingUpdateAt();
	}

	@Override
	public void markAsAppliedExcept(List<UUID> commonJobTriggerIdList) {
		commonJobTriggerStore.markAsAppliedExcept(commonJobTriggerIdList);
	}
}
