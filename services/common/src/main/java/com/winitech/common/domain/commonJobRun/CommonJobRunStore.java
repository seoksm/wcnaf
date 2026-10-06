package com.winitech.common.domain.commonJobRun;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.commonJobRun
 * └ CommonJobRun.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:48
 **/
public interface CommonJobRunStore {
	CommonJobRun store(CommonJobRun commonJobRun);
	CommonJobRun modify(CommonJobRun commonJobRun, CommonJobRunCommand.ModifyRequestCommand commonJobRunCommand);
	void remove(UUID commonJobRunId);
}
