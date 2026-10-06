package com.winitech.smartAsset.domain.license;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.common.exception.InvalidParamException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * S-533/550 라이선스 1건이 임직원 1명에게 배정된 상태 - AssetAssignment와 동일하게 releasedAt이
 * null이면 현재 유효한 배정이다(삭제하지 않고 소프트 릴리스). licensePurchaseRecord가 null이면
 * "미연결 배정"(어느 구매건에서 나왔는지 특정되지 않은 배정)이므로 화면에 명시적으로 표시해야 한다.
 */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class LicenseAssignedUser extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "license_assigned_user_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "license_id")
    private License license;

    @NonNull
    private UUID memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "license_purchase_record_id")
    private LicensePurchaseRecord licensePurchaseRecord;

    @NonNull
    private OffsetDateTime assignedAt;

    @NonNull
    private UUID assignedBy;

    private OffsetDateTime releasedAt;

    @NonNull
    private Boolean releaseRequestedYn;

    private OffsetDateTime releaseRequestedAt;

    @Builder
    public LicenseAssignedUser(@NonNull License license, @NonNull UUID memberId, LicensePurchaseRecord licensePurchaseRecord,
                                @NonNull UUID assignedBy) {
        this.license = license;
        this.memberId = memberId;
        this.licensePurchaseRecord = licensePurchaseRecord;
        this.assignedAt = OffsetDateTime.now();
        this.assignedBy = assignedBy;
        this.releaseRequestedYn = false;
    }

    /** S-550 임직원 셀프서비스 - 회수 요청 표시만 한다(Phase 5 이전이라 실제 티켓은 생성하지 않음) */
    public void requestRelease() {
        if (this.releasedAt != null) {
            throw new InvalidParamException("이미 회수된 배정입니다.");
        }
        this.releaseRequestedYn = true;
        this.releaseRequestedAt = OffsetDateTime.now();
    }

    public void release() {
        if (this.releasedAt != null) {
            throw new InvalidParamException("이미 회수된 배정입니다.");
        }
        this.releasedAt = OffsetDateTime.now();
    }

    public boolean isActive() {
        return this.releasedAt == null;
    }
}
