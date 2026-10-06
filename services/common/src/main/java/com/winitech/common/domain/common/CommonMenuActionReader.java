package com.winitech.common.domain.common;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonMenuActionReader.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 12:39
 **/
public interface CommonMenuActionReader {
	CommonMenuAction getCommonMenuActionById(UUID id);

	CommonMenuAction getCommonMenuActionByIdIfExists(UUID id);

//	List<CommonMenuAction> getCommonMenuActionByName(String name);

	List<CommonMenuAction> getAllCommonMenuAction();

	boolean isExistCommonMenuActionById(UUID id);

	List<CommonMenuAction> getListByProgramIdList(List<UUID> programIdList);
}