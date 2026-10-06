package com.winitech.system.domain.notice.view;

import java.util.UUID;

public interface NoticeViewReader {

    boolean checkIfUserVisitedBefore(UUID noticeId, UUID viewerId);
}
