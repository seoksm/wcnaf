package com.winitech.smartAsset.domain.loan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LoanReader {

    Loan findById(UUID loanId);

    /** S-410 대여 현황 - statusFilter가 null이면 전체 */
    Page<Loan> findAll(Loan.Status statusFilter, Pageable pageable);

    /** S-411 승인대기 목록 */
    List<Loan> findAllPendingApproval();

    /** S-420 대여 가능 자산 - 현재 대여중인 자산의 대여자·기한 표시용 */
    List<Loan> findAllActive();

    /** S-421 내 대여 자산 - 본인의 전체 대여 이력(진행 중 + 반납완료) */
    List<Loan> findAllByMemberId(UUID memberId);

    /** Q-42 동시 대여 한도 검사 - 진행 중(승인대기+대여중)인 건수 */
    long countOpenByMemberId(UUID memberId);

    /** L4 연체 중 신규 대여 차단 - 대여중인데 기한이 지난 건 */
    List<Loan> findOverdueActiveByMemberId(UUID memberId, LocalDate today);

    /** S-700 대여 현황 KPI - 전체 대여중 건수 */
    long countActive();

    /** S-700 대여 현황 KPI - 전체 연체 건수 */
    long countOverdueActive(LocalDate today);
}
