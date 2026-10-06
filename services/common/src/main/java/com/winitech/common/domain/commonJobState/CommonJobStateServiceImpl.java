package com.winitech.common.domain.commonJobState;

import com.winitech.common.domain.commonJob.CommonJobReader;
import com.winitech.common.domain.commonJobRun.CommonJobRunReader;
import com.winitech.common.exception.EntityNotFoundException;
import com.winitech.common.exception.IllegalStatusException;
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
 * com.winitech.system.domain.commonJobState
 * └ CommonJobState.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:47
 **/
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommonJobStateServiceImpl extends EgovAbstractServiceImpl implements CommonJobStateService {
	private final CommonJobStateStore commonJobStateStore;
	private final CommonJobStateReader commonJobStateReader;
	private final CommonJobReader commonJobReader;
	private final CommonJobRunReader commonJobRunReader;

	@Override
	public CommonJobStateInfo registerCommonJobState(CommonJobStateCommand.RegisterRequestCommand commonJobStateCommand) {
		// 중복체크 필요시 주석해제
		//if (commonJobStateCommand.getCommonJobStateCode() != null && commonJobStateReader.existCommonJobStateByCommonJobStateCodeAndExcludingSelf(commonJobStateCommand.getCommonJobStateCode(), UUID.randomUUID())) {
		//	throw new IllegalStatusException("Already registered CommonJobState code.");
		//}

		CommonJobState initCommonJobState = CommonJobState.builder()
				.commonJob(commonJobStateCommand.getCommonJobId() == null ? null : commonJobReader.getCommonJobById(commonJobStateCommand.getCommonJobId()))
				.lastStartedAt(commonJobStateCommand.getLastStartedAt())
				.lastEndedAt(commonJobStateCommand.getLastEndedAt())
				.lastSuccessAt(commonJobStateCommand.getLastSuccessAt())
				.lastFailedAt(commonJobStateCommand.getLastFailedAt())
				.currentRun(commonJobStateCommand.getCurrentRunId() == null ? null : commonJobRunReader.getCommonJobRunById(commonJobStateCommand.getCurrentRunId()))
				.lastRun(commonJobStateCommand.getLastRunId() == null ? null : commonJobRunReader.getCommonJobRunById(commonJobStateCommand.getLastRunId()))
				.errCnt(commonJobStateCommand.getErrCnt())
				.lastMessage(commonJobStateCommand.getLastMessage())

				.systemStatus(CommonJobState.SystemStatus.ENABLE)
				.build();

		CommonJobState commonJobState = commonJobStateStore.store(initCommonJobState);
		return new CommonJobStateInfo(commonJobState);
	}

	@Override
	public CommonJobStateInfo modifyCommonJobState(UUID id, CommonJobStateCommand.ModifyRequestCommand commonJobStateCommand) {
		// 중복체크 필요시 주석해제
		//if (Boolean.TRUE.equals(commonJobStateReader.existCommonJobStateByCommonJobStateCodeAndExcludingSelf(commonJobStateCommand.getCommonJobStateCode(), id))) {
		//	throw new IllegalStatusException("Already registered CommonJobState code.");
		//}

		CommonJobState modifyCommonJobState = commonJobStateReader.getCommonJobStateById(id);

		CommonJobState commonJobState = commonJobStateStore.modify(modifyCommonJobState, commonJobStateCommand);
		return new CommonJobStateInfo(commonJobState);
	}

	@Override
	public void removeCommonJobState(UUID id) {
		commonJobStateStore.remove(id);
	}

	@Override
	public CommonJobStateInfo searchCommonJobStateById(UUID id) {
		CommonJobState commonJobState = commonJobStateReader.getCommonJobStateById(id);
		return new CommonJobStateInfo(commonJobState);
	}

	@Override
	public CommonJobStateInfo searchCommonJobStateByCommonJobId(UUID commonJobId) {
		CommonJobState commonJobState = commonJobStateReader.getCommonJobStateByCommonJobId(commonJobId);
		return new CommonJobStateInfo(commonJobState);
	}
	
	@Override
	public List<CommonJobStateInfo> getAllCommonJobState() {
		List<CommonJobState> commonJobStateList = commonJobStateReader.getAllCommonJobState();
		return commonJobStateList.stream()
				.map(CommonJobStateInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public WiniPageInfo<CommonJobStateInfo> searchCommonJobStatePage(Integer page, Integer pageSize, String searchType, String searchKeyword) {
		Page<CommonJobStateInfo> commonJobStatePage = commonJobStateReader.getCommonJobStatePage(page, pageSize, searchType, searchKeyword);

		return new WiniPageInfo<>(commonJobStatePage);
	}

	@Override
	public void setCurrentRun(UUID commonJobRunId, UUID currentRunId) {
		commonJobStateStore.setCurrentRun(commonJobRunId, currentRunId);
	}

	@Override
	public void setSuccessfulRun(UUID commonJobRunId, UUID lastRunId, OffsetDateTime lastSuccessAt) {
		commonJobStateStore.setSuccessfulRun(commonJobRunId, lastRunId, lastSuccessAt);
	}

	@Override
	public void setFailedRun(UUID commonJobRunId, UUID lastRunId, OffsetDateTime lastFailedAt, String lastMessage) {
		commonJobStateStore.setFailedRun(commonJobRunId, lastRunId, lastFailedAt, lastMessage);
	}
}
