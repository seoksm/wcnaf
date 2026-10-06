package com.winitech.smartAsset.domain.tangibleAsset;

import com.winitech.common.exception.InvalidParamException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 배정 형태(assignType)-배정 사용자(currentMemberId) 불변식 검증.
 * TangibleAsset의 생성자와 modify()가 공유하는 단일 메서드(resolveMemberId)의 동작을
 * 두 진입점(등록/수정) 모두에서 직접 검증한다.
 */
class TangibleAssetTest {

    private TangibleAsset newAsset(TangibleAsset.AssignType assignType, UUID currentMemberId) {
        return TangibleAsset.builder()
                .assetCode("AST-2026-0001")
                .assetName("노트북")
                .lifeStatus(TangibleAsset.LifeStatus.USE)
                .assignType(assignType)
                .acquisitionDate(LocalDate.of(2026, 1, 1))
                .acquisitionAmount(new BigDecimal("1000000"))
                .currentMemberId(currentMemberId)
                .build();
    }

    @Test
    void PERSONAL_사용자_있음_등록_성공() {
        UUID memberId = UUID.randomUUID();

        TangibleAsset asset = newAsset(TangibleAsset.AssignType.PERSONAL, memberId);

        assertThat(asset.getAssignType()).isEqualTo(TangibleAsset.AssignType.PERSONAL);
        assertThat(asset.getCurrentMemberId()).isEqualTo(memberId);
    }

    @Test
    void PERSONAL_사용자_없음_등록_실패() {
        assertThatThrownBy(() -> newAsset(TangibleAsset.AssignType.PERSONAL, null))
                .isInstanceOf(InvalidParamException.class);
    }

    @Test
    void ON_LOAN_사용자_없음_등록_실패() {
        assertThatThrownBy(() -> newAsset(TangibleAsset.AssignType.ON_LOAN, null))
                .isInstanceOf(InvalidParamException.class);
    }

    @Test
    void SHARED_사용자_입력해도_null로_정규화된다() {
        TangibleAsset asset = newAsset(TangibleAsset.AssignType.SHARED, UUID.randomUUID());

        assertThat(asset.getCurrentMemberId()).isNull();
    }

    @Test
    void LOANABLE_사용자_입력해도_null로_정규화된다() {
        TangibleAsset asset = newAsset(TangibleAsset.AssignType.LOANABLE, UUID.randomUUID());

        assertThat(asset.getCurrentMemberId()).isNull();
    }

    @Test
    void UNASSIGNED_사용자_입력해도_null로_정규화된다() {
        TangibleAsset asset = newAsset(TangibleAsset.AssignType.UNASSIGNED, UUID.randomUUID());

        assertThat(asset.getCurrentMemberId()).isNull();
    }

    @Test
    void modify로_PERSONAL_전환_시_사용자_없으면_실패한다() {
        TangibleAsset asset = newAsset(TangibleAsset.AssignType.UNASSIGNED, null);

        TangibleAssetCommand.UpdateCommand command = TangibleAssetCommand.UpdateCommand.builder()
                .assetName("노트북")
                .lifeStatus(TangibleAsset.LifeStatus.USE)
                .assignType(TangibleAsset.AssignType.PERSONAL)
                .acquisitionDate(LocalDate.of(2026, 1, 1))
                .acquisitionAmount(new BigDecimal("1000000"))
                .currentMemberId(null)
                .build();

        assertThatThrownBy(() -> asset.modify(command, null, null))
                .isInstanceOf(InvalidParamException.class);
    }

    @Test
    void 불용_전환_시_배정형태와_사용자가_강제로_해제된다() {
        UUID memberId = UUID.randomUUID();
        TangibleAsset asset = newAsset(TangibleAsset.AssignType.PERSONAL, memberId);

        TangibleAssetCommand.UpdateCommand command = TangibleAssetCommand.UpdateCommand.builder()
                .assetName("노트북")
                .lifeStatus(TangibleAsset.LifeStatus.DISUSE)
                // 클라이언트가 실수로 배정 형태를 그대로 PERSONAL로 보내더라도 강제로 UNASSIGNED가 돼야 한다
                .assignType(TangibleAsset.AssignType.PERSONAL)
                .currentMemberId(memberId)
                .acquisitionDate(LocalDate.of(2026, 1, 1))
                .acquisitionAmount(new BigDecimal("1000000"))
                .build();

        boolean assignmentChanged = asset.modify(command, null, null);

        assertThat(assignmentChanged).isTrue();
        assertThat(asset.getAssignType()).isEqualTo(TangibleAsset.AssignType.UNASSIGNED);
        assertThat(asset.getCurrentMemberId()).isNull();
    }

    @Test
    void disuse_처리하면_불용상태가_되고_배정이_해제된다() {
        UUID memberId = UUID.randomUUID();
        TangibleAsset asset = newAsset(TangibleAsset.AssignType.PERSONAL, memberId);

        boolean assignmentChanged = asset.disuse();

        assertThat(assignmentChanged).isTrue();
        assertThat(asset.getLifeStatus()).isEqualTo(TangibleAsset.LifeStatus.DISUSE);
        assertThat(asset.getAssignType()).isEqualTo(TangibleAsset.AssignType.UNASSIGNED);
        assertThat(asset.getCurrentMemberId()).isNull();
    }

    @Test
    void 배정이_없는_자산을_불용처리하면_assignmentChanged는_false다() {
        TangibleAsset asset = newAsset(TangibleAsset.AssignType.UNASSIGNED, null);

        boolean assignmentChanged = asset.disuse();

        assertThat(assignmentChanged).isFalse();
    }

    @Test
    void 이미_불용상태인_자산은_다시_불용처리할_수_없다() {
        TangibleAsset asset = newAsset(TangibleAsset.AssignType.UNASSIGNED, null);
        asset.disuse();

        assertThatThrownBy(asset::disuse).isInstanceOf(InvalidParamException.class);
    }

    @Test
    void restoreFromDisuse_하면_사용상태로_복귀한다() {
        TangibleAsset asset = newAsset(TangibleAsset.AssignType.UNASSIGNED, null);
        asset.disuse();

        asset.restoreFromDisuse();

        assertThat(asset.getLifeStatus()).isEqualTo(TangibleAsset.LifeStatus.USE);
    }

    @Test
    void 불용상태가_아니면_복귀할_수_없다() {
        TangibleAsset asset = newAsset(TangibleAsset.AssignType.UNASSIGNED, null);

        assertThatThrownBy(asset::restoreFromDisuse).isInstanceOf(InvalidParamException.class);
    }

    @Test
    void dispose_하면_처분완료상태가_된다() {
        TangibleAsset asset = newAsset(TangibleAsset.AssignType.UNASSIGNED, null);
        asset.disuse();

        asset.dispose();

        assertThat(asset.getLifeStatus()).isEqualTo(TangibleAsset.LifeStatus.DISPOSED);
    }

    @Test
    void 불용상태가_아니면_처분처리할_수_없다() {
        TangibleAsset asset = newAsset(TangibleAsset.AssignType.UNASSIGNED, null);

        assertThatThrownBy(asset::dispose).isInstanceOf(InvalidParamException.class);
    }

    @Test
    void 처분완료_자산은_modify로_수정할_수_없다() {
        TangibleAsset asset = newAsset(TangibleAsset.AssignType.UNASSIGNED, null);
        asset.disuse();
        asset.dispose();

        TangibleAssetCommand.UpdateCommand command = TangibleAssetCommand.UpdateCommand.builder()
                .assetName("노트북(수정)")
                .lifeStatus(TangibleAsset.LifeStatus.DISPOSED)
                .assignType(TangibleAsset.AssignType.UNASSIGNED)
                .acquisitionDate(LocalDate.of(2026, 1, 1))
                .acquisitionAmount(new BigDecimal("1000000"))
                .build();

        assertThatThrownBy(() -> asset.modify(command, null, null))
                .isInstanceOf(InvalidParamException.class);
    }

    @Test
    void modify로는_처분완료_상태로_전환할_수_없다() {
        TangibleAsset asset = newAsset(TangibleAsset.AssignType.UNASSIGNED, null);
        asset.disuse();

        TangibleAssetCommand.UpdateCommand command = TangibleAssetCommand.UpdateCommand.builder()
                .assetName("노트북")
                .lifeStatus(TangibleAsset.LifeStatus.DISPOSED)
                .assignType(TangibleAsset.AssignType.UNASSIGNED)
                .acquisitionDate(LocalDate.of(2026, 1, 1))
                .acquisitionAmount(new BigDecimal("1000000"))
                .build();

        assertThatThrownBy(() -> asset.modify(command, null, null))
                .isInstanceOf(InvalidParamException.class);
    }
}
