package com.winitech.common.domain.commonJobGroup;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.commonJobGroup
 * └ CommonJobGroup.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:36
 **/
public interface CommonJobGroupReader {
	CommonJobGroup getCommonJobGroupById(UUID commonJobGroupId);
	// CommonJobGroup getCommonJobGroupByCommonJobGroupCode(String commonJobGroupCode);
	List<CommonJobGroup> getAllCommonJobGroup();
	Page<CommonJobGroupInfo> getCommonJobGroupPage(Integer page, Integer pageSize, String searchType, String searchKeyword, CommonJobGroup.Status status);

	List<CommonJobGroup> getCommonJobGroupByName(List<String> jobGroupNameList);

	boolean existCommonJobGroupByNameAndExcludingSelf(String name, UUID commonJobGroupId);
}
