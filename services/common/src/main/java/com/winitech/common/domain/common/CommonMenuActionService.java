package com.winitech.common.domain.common;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonMenuActionService.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 12:42
 **/
public interface CommonMenuActionService {
    CommonMenuActionInfo registerCommonMenuAction(CommonMenuActionCommand command);

    CommonMenuActionInfo modifyCommonMenuAction(UUID id, CommonMenuActionCommand command);

    void removeCommonMenuAction(UUID id);

    CommonMenuActionInfo searchCommonMenuActionById(UUID id);

//    List<CommonMenuActionInfo> searchCommonMenuActionByName(String name);

    List<CommonMenuActionInfo> getAllCommonMenuAction();

    CommonMenuActionInfo saveCommonMenuAction(CommonMenuActionCommand command);

	void syncAll(List<UUID> programIdList, List<CommonMenuActionCommand> commandList);
}
