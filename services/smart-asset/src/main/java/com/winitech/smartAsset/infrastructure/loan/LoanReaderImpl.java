package com.winitech.smartAsset.infrastructure.loan;

import com.winitech.smartAsset.domain.loan.Loan;
import com.winitech.smartAsset.domain.loan.LoanReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LoanReaderImpl implements LoanReader {

    private final LoanRepository loanRepository;

    @Override
    public Loan findById(UUID loanId) {
        return loanRepository.findById(loanId).orElseThrow();
    }

    @Override
    public Page<Loan> findAll(Loan.Status statusFilter, Pageable pageable) {
        return statusFilter == null
                ? loanRepository.findAll(pageable)
                : loanRepository.findAllByStatus(statusFilter, pageable);
    }

    @Override
    public List<Loan> findAllPendingApproval() {
        return loanRepository.findAllByStatusOrderByBorrowedAtAsc(Loan.Status.PENDING_APPROVAL);
    }

    @Override
    public List<Loan> findAllActive() {
        return loanRepository.findAllByStatus(Loan.Status.ACTIVE);
    }

    @Override
    public List<Loan> findAllByMemberId(UUID memberId) {
        return loanRepository.findAllByMemberIdOrderByBorrowedAtDesc(memberId);
    }

    @Override
    public long countOpenByMemberId(UUID memberId) {
        return loanRepository.countByMemberIdAndStatusIn(
                memberId, Arrays.asList(Loan.Status.PENDING_APPROVAL, Loan.Status.ACTIVE));
    }

    @Override
    public List<Loan> findOverdueActiveByMemberId(UUID memberId, LocalDate today) {
        return loanRepository.findAllByMemberIdAndStatusAndDueDateBefore(memberId, Loan.Status.ACTIVE, today);
    }

    @Override
    public long countActive() {
        return loanRepository.countByStatus(Loan.Status.ACTIVE);
    }

    @Override
    public long countOverdueActive(LocalDate today) {
        return loanRepository.countOverdueActive(today);
    }
}
