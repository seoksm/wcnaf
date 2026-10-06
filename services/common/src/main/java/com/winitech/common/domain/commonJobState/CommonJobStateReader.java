package com.winitech.common.domain.commonJobState;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.commonJobState
 * └ CommonJobState.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:47
 **/
public interface CommonJobStateReader {
	CommonJobState getCommonJobStateById(UUID commonJobStateId);
	CommonJobState getCommonJobStateByCommonJobId(UUID commonJobId);
	// CommonJobState getCommonJobStateByCommonJobStateCode(String commonJobStateCode);
	List<CommonJobState> getAllCommonJobState();
	Page<CommonJobStateInfo> getCommonJobStatePage(Integer page, Integer pageSize, String searchType, String searchKeyword);

	boolean existCommonJobStateByCommonJobId(UUID commonJobStateId);
	// boolean existCommonJobStateByExcludingSelf(String commonJobStateCode, UUID commonJobStateId);
}
