package com.winitech.common.domain.commonJobRun;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.commonJobRun
 * └ CommonJobRun.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:48
 **/
public interface CommonJobRunReader {
	CommonJobRun getCommonJobRunById(UUID commonJobRunId);
	// CommonJobRun getCommonJobRunByCommonJobRunCode(String commonJobRunCode);
	List<CommonJobRun> getAllCommonJobRun();
	Page<CommonJobRunInfo> getCommonJobRunPage(CommonJobRunCommand.SearchRequestCommand searchRequestCommand);
	// boolean existCommonJobRunByExcludingSelf(String commonJobRunCode, UUID commonJobRunId);
}
