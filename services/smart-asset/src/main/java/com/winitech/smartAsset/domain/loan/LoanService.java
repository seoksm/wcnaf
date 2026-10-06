package com.winitech.smartAsset.domain.loan;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface LoanService {

    /** S-410 대여 현황 */
    Page<LoanInfo> loadLoanList(Loan.Status statusFilter, Integer page, Integer size);

    /** S-411 승인대기 목록 */
    List<LoanInfo> loadPendingApprovalList();

    /** S-420 대여 가능 자산 - LOANABLE·ON_LOAN 전체, 대여중이면 대여자·기한 표시 */
    List<LoanAvailabilityInfo> loadAvailableAssets();

    /** S-421 내 대여 자산 - 로그인한 본인 전체 이력 */
    List<LoanInfo> loadMyLoans();

    /** S-420 임직원 QR 스캔 대여 - 대여자는 항상 로그인한 본인. L4/Q-42는 여기서 검사 */
    UUID borrowBySelf(UUID tangibleAssetId);

    /** S-412 관리자 대행 - 대여자를 관리자가 직접 지정한다. L4/Q-42 검사는 동일 */
    UUID borrowByAdmin(UUID tangibleAssetId, UUID memberId);

    /** S-411 승인 */
    void approveLoan(UUID loanId);

    /** S-411 반려 - 자산은 다시 대여가능 상태로 되돌린다 */
    void rejectLoan(UUID loanId, String reason);

    /** S-421 반납(QR 재스캔) - 본인 소유 대여만 허용 */
    void returnLoanBySelf(UUID loanId, boolean abnormal);

    /** S-421 연장(Q-41 한도까지) - 본인 소유 대여만 허용 */
    void extendLoanBySelf(UUID loanId);
}
