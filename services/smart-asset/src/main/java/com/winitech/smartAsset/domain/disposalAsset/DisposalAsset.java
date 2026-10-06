package com.winitech.smartAsset.domain.disposalAsset;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

/** S-242 처분 처리 결과 - 자산당 최대 1건(Q-28: 처분완료는 되돌릴 수 없으므로 재처분이 없다) */
@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DisposalAsset extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "disposal_asset_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tangible_asset_id")
    private TangibleAsset tangibleAsset;

    /** 처분 시점(=불용 동결 시점)의 장부가 - 이후 다시 계산하지 않고 고정한다 */
    @NonNull
    private BigDecimal bookValueAtDisposal;

    @NonNull
    @Enumerated(EnumType.STRING)
    private DisposalReason disposalReasonCode;

    /** 매각·처분 금액 - 폐기 등 금액이 없으면 null. Q-26: 처분손익(disposalGainLoss) 계산의 기준 */
    private BigDecimal disposalAmount;

    private String counterparty;

    private String memo;

    @NonNull
    private UUID disposedBy;

    @Getter
    @RequiredArgsConstructor
    public enum DisposalReason {
        SALE("매각"),
        SCRAP("폐기"),
        DONATION("기부"),
        LOSS("분실"),
        THEFT("도난"),
        OTHER("기타");
        private final String description;
    }

    @Builder
    public DisposalAsset(TangibleAsset tangibleAsset, @NonNull BigDecimal bookValueAtDisposal,
                          @NonNull DisposalReason disposalReasonCode, BigDecimal disposalAmount,
                          String counterparty, String memo, @NonNull UUID disposedBy) {
        this.tangibleAsset = tangibleAsset;
        this.bookValueAtDisposal = bookValueAtDisposal;
        this.disposalReasonCode = disposalReasonCode;
        this.disposalAmount = disposalAmount;
        this.counterparty = counterparty;
        this.memo = memo;
        this.disposedBy = disposedBy;
    }

    /** Q-26: 처분손익 = 처분금액 - 처분 시점 장부가. 처분금액이 없으면(예: 단순 폐기) null */
    public BigDecimal disposalGainLoss() {
        return disposalAmount == null ? null : disposalAmount.subtract(bookValueAtDisposal);
    }
}
