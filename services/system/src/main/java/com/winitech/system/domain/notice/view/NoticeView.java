package com.winitech.system.domain.notice.view;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.system.domain.notice.Notice;
import com.winitech.system.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Table(name = "notice_views")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeView extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public NoticeView(Notice notice, User user) {
        this.notice = notice;
        this.user = user;
    }
}
