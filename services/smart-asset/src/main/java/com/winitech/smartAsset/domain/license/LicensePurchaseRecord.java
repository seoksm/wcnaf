package com.winitech.smartAsset.domain.license;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.smartAsset.domain.vendor.Vendor;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** S-533 구매내역 1건 - 이 합계가 라이선스의 "보유 수량"이 된다(License.java 파생값 계산 참고) */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class LicensePurchaseRecord extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "license_purchase_record_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "license_id")
    private License license;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @NonNull
    private LocalDate purchaseDate;

    @NonNull
    private Integer quantity;

    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private String memo;

    @Builder
    public LicensePurchaseRecord(@NonNull License license, Vendor vendor, @NonNull LocalDate purchaseDate,
                                  @NonNull Integer quantity, BigDecimal unitPrice, BigDecimal totalAmount, String memo) {
        this.license = license;
        this.vendor = vendor;
        this.purchaseDate = purchaseDate;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = totalAmount;
        this.memo = memo;
    }

    public void modify(Vendor vendor, LocalDate purchaseDate, Integer quantity, BigDecimal unitPrice,
                        BigDecimal totalAmount, String memo) {
        this.vendor = vendor;
        this.purchaseDate = purchaseDate;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = totalAmount;
        this.memo = memo;
    }
}
