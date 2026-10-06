package com.winitech.smartAsset.domain.assetHistory;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssetHistory extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    @Column(name = "asset_history_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tangible_asset_id")
    private TangibleAsset tangibleAsset;

    @NonNull
    @Enumerated(EnumType.STRING)
    private HistoryType historyType;

    /** 변경 필드 목록 JSON [{field,before,after}] - 전체 스냅샷 아님 (§2 저장 형식) */
    private String changedFields;

    /** 배정·상태변경 등 감사 필요 유형만 채우는 전체 상태 스냅샷 JSON (Q-23) */
    private String snapshot;

    /** 엑셀 업서트·일괄변경처럼 여러 자산을 한 번에 처리할 때 같은 실행 건을 공유하는 ID */
    private UUID batchId;

    private UUID createdBy;

    @Getter
    @RequiredArgsConstructor
    public enum HistoryType {
        REGISTER("등록"),
        MODIFY("정보수정"),
        STATUS_CHANGE("상태변경"),
        ASSIGNMENT("배정"),
        INVENTORY("전수조사"),
        LOAN("대여"),
        ACKNOWLEDGEMENT("확인서"),
        DISUSE("불용"),
        DISPOSAL("처분");
        private final String description;
    }

    @Builder
    public AssetHistory(TangibleAsset tangibleAsset, @NonNull HistoryType historyType,
                         String changedFields, String snapshot, UUID batchId, UUID createdBy) {
        this.tangibleAsset = tangibleAsset;
        this.historyType = historyType;
        this.changedFields = changedFields;
        this.snapshot = snapshot;
        this.batchId = batchId;
        this.createdBy = createdBy;
    }
}
