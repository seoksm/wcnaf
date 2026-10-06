package com.winitech.smartAsset.domain.acknowledgement;

import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface AcknowledgementService {

    /** S-431 확인서 현황 */
    Page<AcknowledgementInfo> loadList(Acknowledgement.Status statusFilter, Integer page, Integer size);

    /** S-432 관리자 상세 */
    AcknowledgementDetailInfo loadDetail(UUID acknowledgementId);

    /** S-440 - 로그인한 본인의 임직원 승인대기 목록 */
    List<AcknowledgementInfo> loadMyPending();

    /** S-440 - 로그인한 본인 소유 건의 상세 */
    AcknowledgementDetailInfo loadMyDetail(UUID acknowledgementId);

    /**
     * S-430 확인서 요청 - memberName/managerName은 이 서비스가 사용자 이름을 알 수 없어(system
     * 서비스 소관, cross-DB) 프런트가 이미 로드해둔 사용자 목록에서 넘겨주는 값이다. 문서 스냅샷에
     * 이름이 필요한데 없으면 ID를 그대로 노출하는 대신, 요청 화면이 항상 사용자 목록을 먼저 불러온
     * 뒤 선택하게 해 이 값이 비어있지 않도록 강제한다.
     */
    UUID requestAcknowledgement(UUID tangibleAssetId, UUID memberId, String memberName, Acknowledgement.Type type, String managerName);

    /** S-440 K4 - 임직원 본인 2단계 확인 승인 */
    void approveByEmployeeSelf(UUID acknowledgementId, String clientIp);

    /** S-432 - 담당자 승인. RETURN + 승인 옵션 ON일 때만 도달 가능(도메인 자체가 상태로 강제) */
    void approveByManager(UUID acknowledgementId, String clientIp, AcknowledgementApproval.ReturnCondition returnCondition,
                           TangibleAsset.LifeStatus nextLifeStatus, TangibleAsset.AssignType nextAssignType);

    /** S-431 - 완료 전 요청 취소 */
    void cancelAcknowledgement(UUID acknowledgementId, String reason);
}
