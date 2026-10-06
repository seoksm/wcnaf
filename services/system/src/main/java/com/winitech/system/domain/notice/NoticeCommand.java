package com.winitech.system.domain.notice;

import com.winitech.system.domain.user.User;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class NoticeCommand {

    private String title;
    private String content;
    private Notice.UseStatus useStatus;
    private Notice.NoticeStatus noticeStatus;
    private Notice.VisibilityStatus visibilityStatus;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private String pw;
    private UUID creatorId;

    public Notice toEntity(User creator) {
        return Notice.builder()
                .title(title)
                .content(content)
                .useStatus(useStatus)
                .noticeStatus(noticeStatus)
                .visibilityStatus(visibilityStatus)
                .startDate(startDate)
                .endDate(endDate)
                .pw(pw)
                .creator(creator)
                .updater(creator)
                .build();
    }

    @Getter
    @Builder
    public static class UpdateCommand {
        private UUID noticeId;
        private String title;
        private String content;
        private Notice.UseStatus useStatus;
        private Notice.NoticeStatus noticeStatus;
        private Notice.VisibilityStatus visibilityStatus;
        private OffsetDateTime startDate;
        private OffsetDateTime endDate;
        private String pw;
        private UUID updaterId;
    }
}
