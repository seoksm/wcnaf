package com.winitech.smartAsset.domain.depreciation;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * 확정된 분기의 자산별 상각 산출 결과 (S-230/231/232, D7).
 * 확정 화면을 재구성하는 데 필요한 값(자산명·코드·분류명·취득일·취득가액·내용연수·제외 사유)을 확정 시점 값
 * 그대로 이 행에 함께 저장해 둔다 - 이후 tangible_asset/asset_category가 바뀌거나 삭제돼도 이미 확정된
 * 분기의 조회 결과는 절대 바뀌지 않아야 하기 때문에(요구사항 1/2/3), 확정 기간 조회는 tangibleAsset 연관관계를
 * 통해 현재 자산을 다시 읽지 않고 이 행의 컬럼값만 사용한다. tangibleAsset은 자산 상세로의 이동(ID) 용도로만 남긴다.
 * 상각 제외 자산도 제외 사유와 함께 그대로 스냅샷 행으로 보존한다(요구사항 2) - openingAccumulated 등 계산값만 null.
 */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepreciationSnapshot extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "depreciation_snapshot_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tangible_asset_id")
    private TangibleAsset tangibleAsset;

    @NonNull
    private Integer fiscalYear;

    @NonNull
    @Enumerated(EnumType.STRING)
    private DepreciationQuarter quarter;

    @NonNull
    private String assetCode;

    @NonNull
    private String assetName;

    /** 자산종류가 없거나(삭제 등) 확정 당시 미지정이었으면 null */
    private String categoryName;

    @NonNull
    private LocalDate acquisitionDate;

    @NonNull
    private BigDecimal acquisitionAmount;

    /** 상각 기준(내용연수)이 없어 제외된 경우 null */
    private Integer usefulLifeMonths;

    /** 상각 대상이었으면 null, 제외 자산이면 제외 사유 */
    @Enumerated(EnumType.STRING)
    private DepreciationCalculator.ExcludedReason excludedReason;

    /** 제외 자산은 null */
    private BigDecimal openingAccumulated;

    /** 제외 자산은 null */
    private BigDecimal periodDepreciation;

    /** 제외 자산은 null */
    private BigDecimal closingAccumulated;

    /** 상각 대상은 계산된 장부가, 제외 자산은 취득가액 그대로 */
    @NonNull
    private BigDecimal bookValue;

    @Builder
    public DepreciationSnapshot(TangibleAsset tangibleAsset, @NonNull Integer fiscalYear, @NonNull DepreciationQuarter quarter,
                                 @NonNull String assetCode, @NonNull String assetName, String categoryName,
                                 @NonNull LocalDate acquisitionDate, @NonNull BigDecimal acquisitionAmount,
                                 Integer usefulLifeMonths, DepreciationCalculator.ExcludedReason excludedReason,
                                 BigDecimal openingAccumulated, BigDecimal periodDepreciation,
                                 BigDecimal closingAccumulated, @NonNull BigDecimal bookValue) {
        this.tangibleAsset = tangibleAsset;
        this.fiscalYear = fiscalYear;
        this.quarter = quarter;
        this.assetCode = assetCode;
        this.assetName = assetName;
        this.categoryName = categoryName;
        this.acquisitionDate = acquisitionDate;
        this.acquisitionAmount = acquisitionAmount;
        this.usefulLifeMonths = usefulLifeMonths;
        this.excludedReason = excludedReason;
        this.openingAccumulated = openingAccumulated;
        this.periodDepreciation = periodDepreciation;
        this.closingAccumulated = closingAccumulated;
        this.bookValue = bookValue;
    }
}
