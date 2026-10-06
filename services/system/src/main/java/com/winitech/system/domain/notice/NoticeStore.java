package com.winitech.system.domain.notice;

import java.util.UUID;

public interface NoticeStore {

    UUID store(Notice notice);

    void modify(Notice notice, NoticeCommand.UpdateCommand updateCommand);

    void delete(UUID noticeId);
}
