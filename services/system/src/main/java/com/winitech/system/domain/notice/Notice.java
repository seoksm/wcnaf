package com.winitech.system.domain.notice;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.system.domain.user.User;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class Notice extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    private UUID id;

    @NonNull
    private String title;

    @NonNull
    @Column(columnDefinition = "TEXT")
    private String content;

    @NonNull
    @Enumerated(EnumType.STRING)
    private UseStatus useStatus; // 사용여부

    @NonNull
    @Enumerated(EnumType.STRING)
    private NoticeStatus noticeStatus; //공지여부

    @NonNull
    @Enumerated(EnumType.STRING)
    private VisibilityStatus visibilityStatus; // 비밀글여부

    private OffsetDateTime startDate;

    private OffsetDateTime endDate;

    private String pw;

    @NonNull
    private Integer viewCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updater_id")
    private User updater;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @Getter
    @RequiredArgsConstructor
    public enum Status { // 삭제 여부를 관리하기 위한 ENUM
        ENABLE("활성화"),
        DISABLE("비활성화");
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum UseStatus {
        USED("사용"),
        UNUSED("미사용");
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum NoticeStatus {
        NOTICE("공지됨"),
        NOT_NOTICE("미공지");
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum VisibilityStatus {
        SECRET("비밀글"),
        PUBLIC("공개글");
        private final String description;
    }

    @Builder
    public Notice(
        @NonNull String title,
        @NonNull String content,
        @NonNull UseStatus useStatus,
        @NonNull NoticeStatus noticeStatus,
        @NonNull VisibilityStatus visibilityStatus,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        String pw,
        User creator,
        User updater
    ) {
        this.title = title;
        this.content = content;
        this.useStatus = useStatus;
        this.noticeStatus = noticeStatus;
        this.visibilityStatus = visibilityStatus;
        if (noticeStatus == NoticeStatus.NOTICE) {
            validateDates(startDate, endDate);
            this.startDate = startDate;
            this.endDate = endDate;
        }
        if (visibilityStatus == VisibilityStatus.SECRET) {
            this.pw = BCrypt.hashpw(pw, BCrypt.gensalt());
        }
        this.viewCount = 0;
        this.status = Status.ENABLE;
        this.creator = creator;
        this.updater = updater;
    }

    public void increaseViewCount() {
        this.viewCount += 1;
    }

    public void validateDates(OffsetDateTime startDate, OffsetDateTime endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }
    }
}
