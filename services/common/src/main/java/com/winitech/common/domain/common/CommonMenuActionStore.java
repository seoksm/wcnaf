package com.winitech.common.domain.common;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonMenuActionStore.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 12:39
 **/
public interface CommonMenuActionStore {
	CommonMenuAction store(CommonMenuAction commonMenuAction);

	void storeAll(List<CommonMenuAction> toSaveList);

	CommonMenuAction modify(CommonMenuAction commonMenuAction, CommonMenuActionCommand command);

	void remove(UUID commonMenuActionId);

	void removeAll(List<CommonMenuAction> toDeleteList);
}
