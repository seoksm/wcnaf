package com.winitech.smartAsset.domain.inventory;

import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InventoryResultTest {

    private InventoryTarget newTarget() {
        Inventory inventory = Inventory.builder()
                .title("테스트 조사")
                .inventoryType(Inventory.InventoryType.MEMBER)
                .createdBy(UUID.randomUUID())
                .build();
        TangibleAsset asset = TangibleAsset.builder()
                .assetCode("AST-2026-0001")
                .assetName("노트북")
                .lifeStatus(TangibleAsset.LifeStatus.USE)
                .assignType(TangibleAsset.AssignType.PERSONAL)
                .currentMemberId(UUID.randomUUID())
                .acquisitionDate(LocalDate.of(2026, 1, 1))
                .acquisitionAmount(new BigDecimal("1000000"))
                .build();
        return InventoryTarget.builder().inventory(inventory).tangibleAsset(asset).build();
    }

    private InventoryResult newResult() {
        return InventoryResult.builder().inventoryTarget(newTarget()).build();
    }

    @Test
    void 생성_직후_상태는_미확인이다() {
        assertThat(newResult().getStatus()).isEqualTo(InventoryResult.Status.UNCONFIRMED);
    }

    @Test
    void 승인옵션_꺼짐_확인하면_바로_확인완료가_된다() {
        InventoryResult result = newResult();

        result.confirm(false);

        assertThat(result.getStatus()).isEqualTo(InventoryResult.Status.CONFIRMED);
    }

    @Test
    void 승인옵션_켜짐_확인하면_승인대기가_된다() {
        InventoryResult result = newResult();

        result.confirm(true);

        assertThat(result.getStatus()).isEqualTo(InventoryResult.Status.PENDING_APPROVAL);
    }

    @Test
    void 이상보고하면_이상상태가_되고_사유가_기록된다() {
        InventoryResult result = newResult();

        result.reportAnomaly(InventoryResult.AnomalyType.WRONG_HOLDER, "실제 보유자가 다릅니다");

        assertThat(result.getStatus()).isEqualTo(InventoryResult.Status.ANOMALY);
        assertThat(result.getAnomalyType()).isEqualTo(InventoryResult.AnomalyType.WRONG_HOLDER);
        assertThat(result.getNote()).isEqualTo("실제 보유자가 다릅니다");
    }

    @Test
    void 이상유형_없이_보고하면_예외() {
        assertThatThrownBy(() -> newResult().reportAnomaly(null, "메모"))
                .isInstanceOf(InvalidParamException.class);
    }

    @Test
    void 승인대기_상태를_승인하면_확인완료가_되고_검수자_정보가_남는다() {
        InventoryResult result = newResult();
        result.confirm(true);
        UUID reviewer = UUID.randomUUID();

        result.approve(reviewer);

        assertThat(result.getStatus()).isEqualTo(InventoryResult.Status.CONFIRMED);
        assertThat(result.getReviewedBy()).isEqualTo(reviewer);
        assertThat(result.getReviewedAt()).isNotNull();
    }

    @Test
    void 승인대기가_아닌_상태는_승인할_수_없다() {
        InventoryResult result = newResult();

        assertThatThrownBy(() -> result.approve(UUID.randomUUID())).isInstanceOf(InvalidParamException.class);
    }

    @Test
    void 반려하면_미확인으로_돌아가고_반려사유가_남는다() {
        InventoryResult result = newResult();
        result.confirm(true);

        result.reject(UUID.randomUUID(), "사진이 흐림");

        assertThat(result.getStatus()).isEqualTo(InventoryResult.Status.UNCONFIRMED);
        assertThat(result.getRejectionReason()).isEqualTo("사진이 흐림");
    }

    @Test
    void 승인대기가_아닌_상태는_반려할_수_없다() {
        InventoryResult result = newResult();

        assertThatThrownBy(() -> result.reject(UUID.randomUUID(), "사유"))
                .isInstanceOf(InvalidParamException.class);
    }

    @Test
    void 미확인_상태는_종결처리하면_사유와_방법이_기록된다() {
        InventoryResult result = newResult();

        result.close(InventoryResult.ClosureAction.CARRY_OVER, InventoryResult.ClosureReasonCode.ON_LEAVE, "휴직 중");

        assertThat(result.getClosureAction()).isEqualTo(InventoryResult.ClosureAction.CARRY_OVER);
        assertThat(result.getClosureReasonCode()).isEqualTo(InventoryResult.ClosureReasonCode.ON_LEAVE);
        assertThat(result.getClosureNote()).isEqualTo("휴직 중");
    }

    @Test
    void 미확인이_아닌_항목은_종결처리할_수_없다() {
        InventoryResult result = newResult();
        result.confirm(false);

        assertThatThrownBy(() -> result.close(InventoryResult.ClosureAction.LOST, InventoryResult.ClosureReasonCode.LOCATION_UNKNOWN, null))
                .isInstanceOf(InvalidParamException.class);
    }

    @Test
    void 종결처리_방법이나_사유가_없으면_예외() {
        InventoryResult result = newResult();

        assertThatThrownBy(() -> result.close(null, InventoryResult.ClosureReasonCode.NOT_PARTICIPATED, null))
                .isInstanceOf(InvalidParamException.class);
        assertThatThrownBy(() -> result.close(InventoryResult.ClosureAction.CARRY_OVER, null, null))
                .isInstanceOf(InvalidParamException.class);
    }

    @Test
    void 사진으로_확인하면_승인옵션과_무관하게_항상_승인대기가_되고_사진정보가_남는다() {
        InventoryResult result = newResult();
        UUID photoFileId = UUID.randomUUID();
        OffsetDateTime capturedAt = OffsetDateTime.now().minusMinutes(1);
        OffsetDateTime uploadedAt = OffsetDateTime.now();

        result.confirmWithoutScan(photoFileId, capturedAt, uploadedAt);

        assertThat(result.getStatus()).isEqualTo(InventoryResult.Status.PENDING_APPROVAL);
        assertThat(result.getPhotoFileId()).isEqualTo(photoFileId);
        assertThat(result.getPhotoCapturedAt()).isEqualTo(capturedAt);
        assertThat(result.getPhotoUploadedAt()).isEqualTo(uploadedAt);
    }

    @Test
    void 사진없이_사진확인을_시도하면_예외() {
        InventoryResult result = newResult();

        assertThatThrownBy(() -> result.confirmWithoutScan(null, OffsetDateTime.now(), OffsetDateTime.now()))
                .isInstanceOf(InvalidParamException.class);
    }
}
