package com.winitech.smartAsset.domain.ticket;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.license.LicenseAssignedUser;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * S-600~603 서비스데스크 티켓. 완료(DONE)로 전환되면 "완료 잠금" - 더 이상 어떤 필드도 바뀌지
 * 않는다(설계문서 §4 "완료 잠금"). 잘못된 건은 새 티켓으로 재처리하고 이미 생성된 자산은 자산
 * 화면에서 수정한다.
 */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class Ticket extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "ticket_id")
    private UUID id;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Type ticketType;

    @NonNull
    private String title;

    private String content;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @NonNull
    private UUID requestedBy;

    private UUID assigneeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tangible_asset_id")
    private TangibleAsset tangibleAsset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "license_assigned_user_id")
    private LicenseAssignedUser licenseAssignedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_asset_id")
    private TangibleAsset createdAsset;

    private LocalDate targetDueDate;

    private OffsetDateTime completedAt;

    @Getter
    @RequiredArgsConstructor
    public enum Type {
        PURCHASE("신규구매"),
        REPAIR("수리"),
        REPLACE("교체"),
        RETURN("회수"),
        EXTEND("연장"),
        DISPOSAL("폐기");
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        WAITING("접수대기"),
        RECEIVED("접수"),
        IN_PROGRESS("처리중"),
        DONE("완료");
        private final String description;
    }

    @Builder
    public Ticket(@NonNull Type ticketType, @NonNull String title, String content, @NonNull UUID requestedBy,
                  TangibleAsset tangibleAsset, LicenseAssignedUser licenseAssignedUser, LocalDate targetDueDate) {
        this.ticketType = ticketType;
        this.title = title;
        this.content = content;
        this.requestedBy = requestedBy;
        this.tangibleAsset = tangibleAsset;
        this.licenseAssignedUser = licenseAssignedUser;
        this.targetDueDate = targetDueDate;
        this.status = Status.WAITING;
    }

    public void modify(String title, String content) {
        assertNotDone();
        this.title = title;
        this.content = content;
    }

    /** Q-50: 수동 배정. 미배정(assigneeId=null)은 목록·칸반에서 경고로 노출한다 */
    public void assign(UUID assigneeId) {
        assertNotDone();
        this.assigneeId = assigneeId;
    }

    public void moveStatus(Status newStatus) {
        assertNotDone();
        if (newStatus == Status.DONE) {
            throw new InvalidParamException("완료 처리는 complete()로만 가능합니다.");
        }
        this.status = newStatus;
    }

    /** createdAsset은 PURCHASE 유형에서만 채워진다(Q-49). 다른 유형은 null 그대로 완료된다 */
    public void complete(TangibleAsset createdAsset) {
        assertNotDone();
        this.status = Status.DONE;
        this.completedAt = OffsetDateTime.now();
        this.createdAsset = createdAsset;
    }

    private void assertNotDone() {
        if (this.status == Status.DONE) {
            throw new InvalidParamException("완료된 티켓은 수정할 수 없습니다.");
        }
    }

    public boolean isOverdue() {
        return this.status != Status.DONE && this.targetDueDate != null && this.targetDueDate.isBefore(LocalDate.now());
    }
}
