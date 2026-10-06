package com.winitech.smartAsset.application.acknowledgement;

import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.acknowledgement.Acknowledgement;
import com.winitech.smartAsset.domain.acknowledgement.AcknowledgementApproval;
import com.winitech.smartAsset.domain.acknowledgement.AcknowledgementDetailInfo;
import com.winitech.smartAsset.domain.acknowledgement.AcknowledgementInfo;
import com.winitech.smartAsset.domain.acknowledgement.AcknowledgementService;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AcknowledgementFacade {

    private final AcknowledgementService acknowledgementService;

    public Page<AcknowledgementInfo> getList(Acknowledgement.Status statusFilter, Integer page, Integer size) {
        return acknowledgementService.loadList(statusFilter, page, size);
    }

    public AcknowledgementDetailInfo getDetail(UUID acknowledgementId) {
        return acknowledgementService.loadDetail(acknowledgementId);
    }

    public List<AcknowledgementInfo> getMyPending() {
        return acknowledgementService.loadMyPending();
    }

    public AcknowledgementDetailInfo getMyDetail(UUID acknowledgementId) {
        return acknowledgementService.loadMyDetail(acknowledgementId);
    }

    public UUID requestAcknowledgement(UUID tangibleAssetId, UUID memberId, String memberName,
                                        Acknowledgement.Type type, String managerName) {
        return acknowledgementService.requestAcknowledgement(tangibleAssetId, memberId, memberName, type, managerName);
    }

    public void approveByEmployeeSelf(UUID acknowledgementId, String clientIp) {
        withConflictTranslation(() -> {
            acknowledgementService.approveByEmployeeSelf(acknowledgementId, clientIp);
            return null;
        });
    }

    public void approveByManager(UUID acknowledgementId, String clientIp, AcknowledgementApproval.ReturnCondition returnCondition,
                                  TangibleAsset.LifeStatus nextLifeStatus, TangibleAsset.AssignType nextAssignType) {
        withConflictTranslation(() -> {
            acknowledgementService.approveByManager(acknowledgementId, clientIp, returnCondition, nextLifeStatus, nextAssignType);
            return null;
        });
    }

    public void cancelAcknowledgement(UUID acknowledgementId, String reason) {
        acknowledgementService.cancelAcknowledgement(acknowledgementId, reason);
    }

    /**
     * 승인 시점에 TangibleAsset(개인배정 확정 또는 반납 후 상태 확정)을 함께 바꾸는 두 경로
     * (approveByEmployeeSelf의 RECEIPT 완료 시점, approveByManager)는 TangibleAsset.version
     * 낙관적 잠금과 경쟁한다. 그 충돌은 Spring이 트랜잭션 커밋 시점에 던지므로(도메인 서비스
     * 메서드 본문이 아니라 그 메서드를 호출하는 지점, 즉 여기서 잡힌다) - LoanFacade/
     * TangibleAssetFacade.withConflictTranslation과 동일한 이유로 이 계층에서 변환한다.
     */
    private <T> T withConflictTranslation(java.util.function.Supplier<T> action) {
        try {
            return action.get();
        } catch (ObjectOptimisticLockingFailureException | DataIntegrityViolationException e) {
            throw new InvalidParamException("다른 곳에서 이미 이 자산 정보를 변경했습니다. 새로고침 후 다시 시도해주세요.");
        }
    }
}
