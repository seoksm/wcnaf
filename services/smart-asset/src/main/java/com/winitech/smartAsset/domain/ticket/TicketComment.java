package com.winitech.smartAsset.domain.ticket;

import com.winitech.common.domain.AbstractEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.UUID;

/** S-602/611 티켓 코멘트 - append-only. 요청자 본인 작성 여부(isRequester)로 화면에서 배경색 구분 */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketComment extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "ticket_comment_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @NonNull
    private String content;

    @NonNull
    private UUID writtenBy;

    @NonNull
    private Boolean isRequester;

    @Builder
    public TicketComment(@NonNull Ticket ticket, @NonNull String content, @NonNull UUID writtenBy, @NonNull Boolean isRequester) {
        this.ticket = ticket;
        this.content = content;
        this.writtenBy = writtenBy;
        this.isRequester = isRequester;
    }
}
