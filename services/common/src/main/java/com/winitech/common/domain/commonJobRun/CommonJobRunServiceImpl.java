package com.winitech.common.domain.commonJobRun;

import com.winitech.common.domain.commonJob.CommonJobReader;
import com.winitech.common.domain.commonJobTrigger.CommonJobTriggerReader;
import com.winitech.common.library.commonType.WiniPageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.domain.commonJobRun
 * └ CommonJobRun.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:48
 **/
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommonJobRunServiceImpl extends EgovAbstractServiceImpl implements CommonJobRunService {
	private final CommonJobRunStore commonJobRunStore;
	private final CommonJobRunReader commonJobRunReader;
	private final CommonJobReader commonJobReader;
	private final CommonJobTriggerReader commonJobTriggerReader;

	@Override
	public CommonJobRunInfo registerCommonJobRun(CommonJobRunCommand.RegisterRequestCommand commonJobRunCommand) {
		// 중복체크 필요시 주석해제
		//if (commonJobRunCommand.getCommonJobRunCode() != null && commonJobRunReader.existCommonJobRunByCommonJobRunCodeAndExcludingSelf(commonJobRunCommand.getCommonJobRunCode(), UUID.randomUUID())) {
		//	throw new IllegalStatusException("Already registered CommonJobRun code.");
		//}

		CommonJobRun initCommonJobRun = CommonJobRun.builder()
				.commonJob(commonJobRunCommand.getCommonJobId() == null ? null : commonJobReader.getCommonJobById(commonJobRunCommand.getCommonJobId()))
				.commonJobTrigger(commonJobRunCommand.getCommonJobTriggerId() == null ? null : commonJobTriggerReader.getCommonJobTriggerById(commonJobRunCommand.getCommonJobTriggerId()))
				.startedAt(commonJobRunCommand.getStartedAt())
				.endedAt(commonJobRunCommand.getEndedAt())
				.jobStatus(commonJobRunCommand.getJobStatus())
				.message(commonJobRunCommand.getMessage())

				.systemStatus(CommonJobRun.SystemStatus.ENABLE)
				.build();

		CommonJobRun commonJobRun = commonJobRunStore.store(initCommonJobRun);
		return new CommonJobRunInfo(commonJobRun);
	}

	@Override
	public CommonJobRunInfo modifyCommonJobRun(UUID id, CommonJobRunCommand.ModifyRequestCommand commonJobRunCommand) {
		// 중복체크 필요시 주석해제
		//if (Boolean.TRUE.equals(commonJobRunReader.existCommonJobRunByCommonJobRunCodeAndExcludingSelf(commonJobRunCommand.getCommonJobRunCode(), id))) {
		//	throw new IllegalStatusException("Already registered CommonJobRun code.");
		//}

		CommonJobRun modifyCommonJobRun = commonJobRunReader.getCommonJobRunById(id);

		CommonJobRun commonJobRun = commonJobRunStore.modify(modifyCommonJobRun, commonJobRunCommand);
		return new CommonJobRunInfo(commonJobRun);
	}

	@Override
	public void removeCommonJobRun(UUID id) {
		commonJobRunStore.remove(id);
	}

	@Override
	public CommonJobRunInfo searchCommonJobRunById(UUID id) {
		CommonJobRun commonJobRun = commonJobRunReader.getCommonJobRunById(id);
		return new CommonJobRunInfo(commonJobRun);
	}

	@Override
	public List<CommonJobRunInfo> getAllCommonJobRun() {
		List<CommonJobRun> commonJobRunList = commonJobRunReader.getAllCommonJobRun();
		return commonJobRunList.stream()
				.map(CommonJobRunInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public WiniPageInfo<CommonJobRunInfo> searchCommonJobRunPage(CommonJobRunCommand.SearchRequestCommand searchRequestCommand) {
		Page<CommonJobRunInfo> commonJobRunPage = commonJobRunReader.getCommonJobRunPage(searchRequestCommand);

		return new WiniPageInfo<>(commonJobRunPage);
	}
}
