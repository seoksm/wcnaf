package com.winitech.smartAsset.domain.software;

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

/** S-520 소프트웨어 마스터 - 라이선스 "포함 SW" 자동완성 소스(R2, 벤더 DB 연동 제외) */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class Software extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "software_id")
    private UUID id;

    @NonNull
    private String name;

    private String publisher;
    private String category;
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
    public Software(@NonNull String name, String publisher, String category, String memo) {
        this.name = name;
        this.publisher = publisher;
        this.category = category;
        this.memo = memo;
        this.status = Status.ENABLE;
    }

    public void modify(SoftwareCommand.UpdateCommand command) {
        this.name = command.getName();
        this.publisher = command.getPublisher();
        this.category = command.getCategory();
        this.memo = command.getMemo();
    }

    public void delete() {
        this.status = Status.DISABLE;
    }
}
