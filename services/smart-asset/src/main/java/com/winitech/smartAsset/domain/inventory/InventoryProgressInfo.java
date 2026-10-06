package com.winitech.smartAsset.domain.inventory;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

/** S-303 진행 현황 - 4개 지표(확인완료/승인대기/이상/미확인) + 참여자별 분해 */
@Getter
public class InventoryProgressInfo {

    private final long confirmedCount;
    private final long pendingApprovalCount;
    private final long anomalyCount;
    private final long unconfirmedCount;
    private final long totalCount;
    /** 임직원형(MEMBER)에서만 값이 있다 - 관리자형은 특정 개인에게 대상이 걸려 있지 않다(Q-32) */
    private final List<ParticipantSummary> participants;

    public InventoryProgressInfo(long confirmedCount, long pendingApprovalCount, long anomalyCount,
                                  long unconfirmedCount, long totalCount, List<ParticipantSummary> participants) {
        this.confirmedCount = confirmedCount;
        this.pendingApprovalCount = pendingApprovalCount;
        this.anomalyCount = anomalyCount;
        this.unconfirmedCount = unconfirmedCount;
        this.totalCount = totalCount;
        this.participants = participants;
    }

    /** "미확인 69건 = 미참여 11명 + 부분완료 8명"처럼 사람 단위로 분해해 보여주기 위한 참여자 1명 요약 */
    @Getter
    public static class ParticipantSummary {
        private final UUID memberId;
        private final long totalCount;
        private final long unconfirmedCount;
        /** 이 참여자가 배정받은 대상 중 하나라도 확인을 시도했는지(미참여 여부 판정 기준) */
        private final boolean participated;

        public ParticipantSummary(UUID memberId, long totalCount, long unconfirmedCount, boolean participated) {
            this.memberId = memberId;
            this.totalCount = totalCount;
            this.unconfirmedCount = unconfirmedCount;
            this.participated = participated;
        }
    }
}
