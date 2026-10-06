package com.winitech.common.domain.commonJobGroup;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.commonJobGroup
 * └ CommonJobGroup.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:36
 **/
public interface CommonJobGroupStore {
	CommonJobGroup store(CommonJobGroup commonJobGroup);
	CommonJobGroup modify(CommonJobGroup commonJobGroup, CommonJobGroupCommand.ModifyRequestCommand commonJobGroupCommand);
	void remove(UUID commonJobGroupId);
}
