package com.winitech.system.domain.notice;

import org.springframework.data.domain.Page;

import java.util.UUID;

public interface NoticeService {

    UUID createNotice(NoticeCommand noticeCommand);

    void updateNotice(NoticeCommand.UpdateCommand updateCommand);

    void deleteNotice(UUID noticeId);

    Page<Notice> loadNoticeList(int page, int pageSize, String title, String content, String author);

    NoticeInfo loadNotice(UUID noticeId, UUID requesterId);

    NoticeInfo.forUserInfo loadNoticeForUser(UUID noticeId, UUID requesterId);

    void checkNoticePw(UUID noticeId, String password);
}
