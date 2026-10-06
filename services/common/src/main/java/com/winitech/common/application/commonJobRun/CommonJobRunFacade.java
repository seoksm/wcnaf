package com.winitech.common.application.commonJobRun;

import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.common.domain.commonJobRun.CommonJobRunCommand;
import com.winitech.common.domain.commonJobRun.CommonJobRunInfo;
import com.winitech.common.domain.commonJobRun.CommonJobRunService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
@Service
@RequiredArgsConstructor
public class CommonJobRunFacade {
	private final CommonJobRunService commonJobRunService;

	public CommonJobRunInfo registerCommonJobRun(CommonJobRunCommand.RegisterRequestCommand commonJobRunCommand) {
		return commonJobRunService.registerCommonJobRun(commonJobRunCommand);
	}

	public CommonJobRunInfo modifyCommonJobRun(UUID id, CommonJobRunCommand.ModifyRequestCommand commonJobRunCommand) {
		return commonJobRunService.modifyCommonJobRun(id, commonJobRunCommand);
	}

	public void removeCommonJobRun(UUID id) {
		commonJobRunService.removeCommonJobRun(id);
	}

	public CommonJobRunInfo searchCommonJobRunById(UUID id) {
		return commonJobRunService.searchCommonJobRunById(id);
	}

	public List<CommonJobRunInfo> getAllCommonJobRun() {
		return commonJobRunService.getAllCommonJobRun();
	}

	public WiniPageInfo<CommonJobRunInfo> searchCommonJobRunPage(CommonJobRunCommand.SearchRequestCommand searchRequestCommand) {
		return commonJobRunService.searchCommonJobRunPage(searchRequestCommand);
	}
}
