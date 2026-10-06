package com.winitech.smartAsset.domain.ticket;

import com.winitech.common.domain.AbstractEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.UUID;

/**
 * Q-51 유형별 SLA 목표일 · Q-49 자동등록 대상 유형 설정. 유형(6종)당 한 행씩 미리 seed되어
 * 있고(V24), 이번 단계에는 전용 관리 화면이 없다 - 값을 바꿔야 하면 직접 SQL로 갱신한다
 * (expense_record와 동일한 "화면 없는 내부 테이블" 판단).
 */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketTypeConfig extends AbstractEntity {

    @Id
    @Column(name = "ticket_type_config_id")
    private UUID id;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Ticket.Type ticketType;

    private UUID defaultAssigneeId;
    private Integer targetDays;

    @NonNull
    private Boolean autoCreateAssetYn;
}
