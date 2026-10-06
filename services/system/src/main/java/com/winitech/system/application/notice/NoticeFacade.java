package com.winitech.system.application.notice;

import com.winitech.system.domain.notice.Notice;
import com.winitech.system.domain.notice.NoticeCommand;
import com.winitech.system.domain.notice.NoticeInfo;
import com.winitech.system.domain.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeFacade {

    private final NoticeService noticeService;

    public UUID postNotice(NoticeCommand noticeCommand) {
        return noticeService.createNotice(noticeCommand);
    }

    public void reviseNotice(NoticeCommand.UpdateCommand updateCommand) {
        noticeService.updateNotice(updateCommand);
    }

    public void removeNotice(UUID noticeId) {
        noticeService.deleteNotice(noticeId);
    }

    public Page<Notice> getNoticeList(int page, int pageSize, String title, String content, String author) {
        return noticeService.loadNoticeList(page, pageSize, title, content, author);
    }

    public NoticeInfo getNotice(UUID noticeId, UUID requesterId) {
        return noticeService.loadNotice(noticeId, requesterId);
    }

    public NoticeInfo.forUserInfo getNoticeForUser(UUID noticeId, UUID requesterId) {
        return noticeService.loadNoticeForUser(noticeId, requesterId);
    }

    public void checkNoticePw(UUID noticeId, String password) {
        noticeService.checkNoticePw(noticeId, password);
    }
}
