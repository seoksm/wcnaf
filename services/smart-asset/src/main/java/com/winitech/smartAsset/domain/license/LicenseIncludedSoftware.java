package com.winitech.smartAsset.domain.license;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.smartAsset.domain.software.Software;
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

/** S-533 "포함 SW" 목록 - 라이선스 1건에 여러 소프트웨어가 묶일 수 있다(예: MS 365에 Word+Excel+...) */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LicenseIncludedSoftware extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "license_included_software_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "license_id")
    private License license;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "software_id")
    private Software software;

    @Builder
    public LicenseIncludedSoftware(@NonNull License license, @NonNull Software software) {
        this.license = license;
        this.software = software;
    }
}
