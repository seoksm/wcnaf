package com.winitech.common.infrastructure.commonJobRun;

import com.winitech.common.domain.commonJobRun.CommonJobRunCommand;
import com.winitech.common.exception.EntityNotFoundException;

import com.winitech.common.library.WiniCom;
import com.winitech.common.domain.commonJobRun.CommonJobRun;
import com.winitech.common.domain.commonJobRun.CommonJobRunInfo;
import com.winitech.common.domain.commonJobRun.CommonJobRunReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.Comparator;
import java.util.List;
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
public class CommonJobRunReaderImpl implements CommonJobRunReader {
	private final CommonJobRunRepository commonJobRunRepository;
	private final CommonJobRunQueryRepository commonJobRunQueryRepository;

	@Override
	public CommonJobRun getCommonJobRunById(UUID commonJobRunId) {
		return commonJobRunRepository.findByIdAndSystemStatus(commonJobRunId, CommonJobRun.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
	}

	//@Override
	//public CommonJobRun getCommonJobRunByCommonJobRunCode(String commonJobRunCode) {
	//	return commonJobRunRepository.findByCommonJobRunCodeAndSystemStatus(commonJobRunCode, CommonJobRun.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
	//}

	@Override
	public List<CommonJobRun> getAllCommonJobRun() {
		return commonJobRunRepository.findAllBySystemStatusOrderByIdDesc(CommonJobRun.SystemStatus.ENABLE);
	}

	@Override
	public Page<CommonJobRunInfo> getCommonJobRunPage(CommonJobRunCommand.SearchRequestCommand searchRequestCommand) {
		return commonJobRunQueryRepository.findAllPage(searchRequestCommand);
	}

	//@Override
	//public boolean existCommonJobRunByExcludingSelf(String commonJobRunCode, UUID commonJobRunId)  {
	//	return commonJobRunRepository.existsByCommonJobRunCodeAndIdNotAndSystemStatus(commonJobRunCode, commonJobRunId, CommonJobRun.SystemStatus.ENABLE);
	//}
}
