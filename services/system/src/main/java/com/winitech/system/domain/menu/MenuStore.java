package com.winitech.system.domain.menu;

import com.winitech.system.domain.menu.Menu;
import com.winitech.system.domain.menu.MenuCommand;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.menu
 * └ MenuStore.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-24 09:32
 **/
public interface MenuStore {
	Menu store(Menu menu);
	Menu modify(Menu menu, MenuCommand.ModifyRequestCommand menuCommand);
	void remove(UUID menuId);
	void updateParentAndSortSeq(UUID menuId, UUID parentMenuId, int sortSeq);
	void unlinkDeletedMenuWithProgramId(UUID programId);
	void flush();
}
