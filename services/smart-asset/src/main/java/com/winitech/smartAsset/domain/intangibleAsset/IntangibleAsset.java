package com.winitech.smartAsset.domain.intangibleAsset;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.common.exception.InvalidParamException;
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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.UUID;

/**
 * S-500~502 무형자산 - 도메인·인증서·상표권(Q-46, SW 라이선스는 별도 모듈). "만료 사고 예방"이
 * 이 화면의 존재 이유라 정상/임박/만료 상태는 별도 컬럼이 아니라 expiryDate에서 매번 계산하는
 * 파생값이다(L2 연체 파생값과 동일한 이유 - 배치로 갱신하면 그 배치가 실패한 날은 상태가 어긋난다).
 */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class IntangibleAsset extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "intangible_asset_id")
    private UUID id;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Type intangibleType;

    @NonNull
    private String name;

    private String issuer;

    private LocalDate registeredDate;

    @NonNull
    private LocalDate expiryDate;

    private UUID ownerMemberId;

    /** 만료 알림 시점 CSV(예: "30,7,3,1") - 실제 발송은 알림 인프라 부재로 미구현, 목록 표기·임박 판정용 */
    @NonNull
    private String alertDays;

    private String memo;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @Getter
    @RequiredArgsConstructor
    public enum Type {
        DOMAIN("도메인"),
        CERTIFICATE("인증서"),
        TRADEMARK("상표권"),
        OTHER("기타");
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        ENABLE("사용"),
        DISABLE("사용안함");
        private final String description;
    }

    @Builder
    public IntangibleAsset(@NonNull Type intangibleType, @NonNull String name, String issuer,
                            LocalDate registeredDate, @NonNull LocalDate expiryDate, UUID ownerMemberId,
                            String alertDays, String memo) {
        this.intangibleType = intangibleType;
        this.name = name;
        this.issuer = issuer;
        this.registeredDate = registeredDate;
        this.expiryDate = expiryDate;
        this.ownerMemberId = ownerMemberId;
        this.alertDays = alertDays != null ? alertDays : "30,7,3,1";
        this.memo = memo;
        this.status = Status.ENABLE;
    }

    public void modify(IntangibleAssetCommand.UpdateCommand command) {
        this.intangibleType = command.getIntangibleType();
        this.name = command.getName();
        this.issuer = command.getIssuer();
        this.registeredDate = command.getRegisteredDate();
        this.expiryDate = command.getExpiryDate();
        this.ownerMemberId = command.getOwnerMemberId();
        this.alertDays = command.getAlertDays() != null ? command.getAlertDays() : this.alertDays;
        this.memo = command.getMemo();
    }

    /** S-502 갱신 - 만료일을 미래로 미룬다. 과거로 되돌리는 것(오타 정정 등)은 modify()로 처리한다 */
    public LocalDate renew(LocalDate newExpiryDate) {
        if (!newExpiryDate.isAfter(this.expiryDate)) {
            throw new InvalidParamException("갱신 후 만료일은 기존 만료일보다 이후여야 합니다.");
        }
        LocalDate previous = this.expiryDate;
        this.expiryDate = newExpiryDate;
        return previous;
    }

    public void delete() {
        this.status = Status.DISABLE;
    }

    public long daysUntilExpiry() {
        return ChronoUnit.DAYS.between(LocalDate.now(), this.expiryDate);
    }

    public boolean isExpired() {
        return this.expiryDate.isBefore(LocalDate.now());
    }

    /** 설정된 알림 시점 중 하나라도 남은 일수 이내로 들어오면 "임박" */
    public boolean isNearExpiry() {
        if (isExpired()) return false;
        long remaining = daysUntilExpiry();
        return Arrays.stream(this.alertDays.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .mapToLong(Long::parseLong)
                .anyMatch(threshold -> remaining <= threshold);
    }
}
