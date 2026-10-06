package com.winitech.common.domain.commonJobRun;

import com.winitech.common.library.commonType.WiniPageInfo;

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
public interface CommonJobRunService {
	CommonJobRunInfo registerCommonJobRun(CommonJobRunCommand.RegisterRequestCommand commonJobRunCommand);
	CommonJobRunInfo modifyCommonJobRun(UUID id, CommonJobRunCommand.ModifyRequestCommand commonJobRunCommand);
	void removeCommonJobRun(UUID id);
	CommonJobRunInfo searchCommonJobRunById(UUID id);
	List<CommonJobRunInfo> getAllCommonJobRun();
	WiniPageInfo<CommonJobRunInfo> searchCommonJobRunPage(CommonJobRunCommand.SearchRequestCommand searchRequestCommand);
}
