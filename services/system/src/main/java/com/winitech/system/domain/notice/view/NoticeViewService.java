package com.winitech.system.domain.notice.view;

import com.winitech.system.domain.notice.Notice;

import java.util.UUID;

public interface NoticeViewService {

    void checkViewer(Notice notice, UUID viewerId);
}
