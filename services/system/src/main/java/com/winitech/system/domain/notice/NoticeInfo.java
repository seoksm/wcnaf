package com.winitech.system.domain.notice;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.Date;

@Getter
public class NoticeInfo {

    private final String id;
    private final String title;
    private final String content;
    private final Notice.UseStatus useStatus;
    private final Notice.NoticeStatus noticeStatus;
    private final Notice.VisibilityStatus visibilityStatus;
    private final Date startDate;
    private final Date endDate;
    private final String pw;
    private final Integer viewCount;
    private final String creator;
    private final OffsetDateTime createAt;
    private final String updater;
    private final OffsetDateTime updateAt;

    public NoticeInfo(Notice notice) {
        this.id = notice.getId().toString();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.useStatus = notice.getUseStatus();
        this.noticeStatus = notice.getNoticeStatus();
        this.visibilityStatus = notice.getVisibilityStatus();
        this.startDate = notice.getStartDate() == null ? null : Date.from(notice.getStartDate().toInstant());
        this.endDate = notice.getEndDate() == null ? null : Date.from(notice.getEndDate().toInstant());
        this.pw = notice.getPw();
        this.viewCount = notice.getViewCount();
        this.creator = notice.getCreator().getFullName();
        this.createAt = notice.getCreateAt();
        this.updater = notice.getUpdater().getFullName();
        this.updateAt = notice.getUpdateAt();
    }

    @Getter
    public static class forUserInfo {
        private final String id;
        private final String title;
        private final String content;
        private final int viewCount;
        private final String creator;
        private final OffsetDateTime createAt;

        public forUserInfo(Notice notice) {
            this.id = notice.getId().toString();
            this.title = notice.getTitle();
            this.content = notice.getContent();
            this.viewCount = notice.getViewCount();
            this.creator = notice.getCreator().getFullName();
            this.createAt = notice.getCreateAt();
        }
    }
}
