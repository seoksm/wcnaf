package com.winitech.system.domain.notice;

import com.winitech.common.exception.IllegalStatusException;
import com.winitech.system.domain.notice.view.NoticeViewService;
import com.winitech.system.domain.user.User;
import com.winitech.system.domain.user.UserReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeServiceImpl extends EgovAbstractServiceImpl implements NoticeService {

    private final NoticeReader noticeReader;
    private final NoticeStore noticeStore;
    private final UserReader userReader;
    private final NoticeViewService noticeViewService;

    @Transactional
    @Override
    public UUID createNotice(NoticeCommand noticeCommand) {

        User creator = userReader.getUser(noticeCommand.getCreatorId());
        Notice notice = noticeCommand.toEntity(creator);

        return noticeStore.store(notice);
    }

    @Transactional
    @Override
    public void updateNotice(NoticeCommand.UpdateCommand updateCommand) {

        Notice notice = noticeReader.findById(updateCommand.getNoticeId());

        if (!notice.getUpdater().getId().toString().equals(updateCommand.getUpdaterId().toString())) {
            User updater = userReader.getUser(updateCommand.getUpdaterId());
            notice.setUpdater(updater);
        }

        noticeStore.modify(notice, updateCommand);
    }

    @Transactional
    @Override
    public void deleteNotice(UUID noticeId) {
        noticeStore.delete(noticeId);
    }

    @Override
    public Page<Notice> loadNoticeList(int page, int pageSize, String title, String content, String author) {
        return noticeReader.readNoticeList(page, pageSize, title, content, author);
    }

    @Transactional
    @Override
    public NoticeInfo loadNotice(UUID noticeId, UUID requesterId) {

        Notice notice = noticeReader.findById(noticeId);
        noticeViewService.checkViewer(notice, requesterId);

        return new NoticeInfo(notice);
    }

    @Transactional
    @Override
    public NoticeInfo.forUserInfo loadNoticeForUser(UUID noticeId, UUID requesterId) {

        Notice notice = noticeReader.findById(noticeId);
        noticeViewService.checkViewer(notice, requesterId);

        return new NoticeInfo.forUserInfo(notice);
    }

    @Override
    public void checkNoticePw(UUID noticeId, String password) {

        Notice notice = noticeReader.findById(noticeId);

        if (!BCrypt.checkpw(password, notice.getPw())) {
            throw new IllegalStatusException("Invalid password");
        }
    }
}
