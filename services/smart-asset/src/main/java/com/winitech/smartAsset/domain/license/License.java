package com.winitech.smartAsset.domain.license;

import com.winitech.common.domain.AbstractEntity;
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
import java.util.UUID;

/**
 * S-530~533 라이선스 상품 1건(예: "Adobe Creative Cloud"). 보유·배정·잔여는 이 엔티티가 아니라
 * 하위 테이블(구매내역 합계·배정건수)에서 매번 계산하는 파생값이다 - L2와 동일한 이유.
 */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class License extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "license_id")
    private UUID id;

    @NonNull
    private String name;

    private String memo;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        ENABLE("사용"),
        DISABLE("사용안함");
        private final String description;
    }

    @Builder
    public License(@NonNull String name, String memo) {
        this.name = name;
        this.memo = memo;
        this.status = Status.ENABLE;
    }

    public void modify(LicenseCommand.UpdateCommand command) {
        this.name = command.getName();
        this.memo = command.getMemo();
    }

    public void delete() {
        this.status = Status.DISABLE;
    }
}
