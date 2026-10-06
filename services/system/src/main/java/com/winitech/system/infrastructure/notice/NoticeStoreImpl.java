package com.winitech.system.infrastructure.notice;

import com.winitech.system.domain.notice.Notice;
import com.winitech.system.domain.notice.NoticeCommand;
import com.winitech.system.domain.notice.NoticeStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NoticeStoreImpl implements NoticeStore {

    private final NoticeRepository noticeRepository;

    @Override
    public UUID store(Notice notice) {
        return noticeRepository.save(notice).getId();
    }

    @Override
    public void modify(Notice notice, NoticeCommand.UpdateCommand updateCommand) {
        notice.setTitle(updateCommand.getTitle());
        notice.setContent(updateCommand.getContent());
        notice.setUseStatus(updateCommand.getUseStatus());
        notice.setNoticeStatus(updateCommand.getNoticeStatus());

        if (updateCommand.getVisibilityStatus() == Notice.VisibilityStatus.SECRET) {
            if ((notice.getPw() == null) || !(notice.getPw().equals(updateCommand.getPw()))) {
                notice.setPw(BCrypt.hashpw(updateCommand.getPw(), BCrypt.gensalt()));
            }
        } else if (updateCommand.getVisibilityStatus() == Notice.VisibilityStatus.PUBLIC) {
            notice.setPw(null);
        }
        notice.setVisibilityStatus(updateCommand.getVisibilityStatus());

        if (updateCommand.getNoticeStatus() == Notice.NoticeStatus.NOTICE) {
            notice.validateDates(updateCommand.getStartDate(), notice.getEndDate());
            notice.setStartDate(updateCommand.getStartDate());
            notice.setEndDate(updateCommand.getEndDate());
        }
    }

    @Override
    public void delete(UUID noticeId) {
        noticeRepository.softDelete(noticeId);
    }
}
