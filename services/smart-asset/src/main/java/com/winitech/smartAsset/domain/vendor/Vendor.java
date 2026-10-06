package com.winitech.smartAsset.domain.vendor;

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

/** S-540 공급사 - 렌탈·라이선스 공용 */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class Vendor extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "vendor_id")
    private UUID id;

    @NonNull
    private String name;

    private String contactName;
    private String contactPhone;
    private String contactEmail;
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
    public Vendor(@NonNull String name, String contactName, String contactPhone, String contactEmail, String memo) {
        this.name = name;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.memo = memo;
        this.status = Status.ENABLE;
    }

    public void modify(VendorCommand.UpdateCommand command) {
        this.name = command.getName();
        this.contactName = command.getContactName();
        this.contactPhone = command.getContactPhone();
        this.contactEmail = command.getContactEmail();
        this.memo = command.getMemo();
    }

    public void delete() {
        this.status = Status.DISABLE;
    }
}
