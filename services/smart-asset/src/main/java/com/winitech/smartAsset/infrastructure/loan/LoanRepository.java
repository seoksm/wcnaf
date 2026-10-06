package com.winitech.smartAsset.infrastructure.loan;

import com.winitech.smartAsset.domain.loan.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {

    @EntityGraph(attributePaths = {"tangibleAsset", "tangibleAsset.category"})
    Page<Loan> findAllByStatus(Loan.Status status, Pageable pageable);

    @EntityGraph(attributePaths = {"tangibleAsset", "tangibleAsset.category"})
    Page<Loan> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"tangibleAsset", "tangibleAsset.category"})
    List<Loan> findAllByStatusOrderByBorrowedAtAsc(Loan.Status status);

    @EntityGraph(attributePaths = {"tangibleAsset", "tangibleAsset.category"})
    List<Loan> findAllByStatus(Loan.Status status);

    @EntityGraph(attributePaths = {"tangibleAsset", "tangibleAsset.category"})
    List<Loan> findAllByMemberIdOrderByBorrowedAtDesc(UUID memberId);

    long countByMemberIdAndStatusIn(UUID memberId, List<Loan.Status> statuses);

    @EntityGraph(attributePaths = {"tangibleAsset", "tangibleAsset.category"})
    List<Loan> findAllByMemberIdAndStatusAndDueDateBefore(UUID memberId, Loan.Status status, LocalDate date);

    /** S-700 대여 현황 - 대여중 건수(전체) */
    long countByStatus(Loan.Status status);

    /** S-700 대여 현황 - L2(연체는 파생값) 전체 연체 건수 */
    @Query("select count(l) from Loan l where l.status = 'ACTIVE' and l.dueDate < :today")
    long countOverdueActive(LocalDate today);
}
