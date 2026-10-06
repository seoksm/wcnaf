package com.winitech.smartAsset.infrastructure.loan;

import com.winitech.smartAsset.domain.loan.Loan;
import com.winitech.smartAsset.domain.loan.LoanStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LoanStoreImpl implements LoanStore {

    private final LoanRepository loanRepository;

    @Override
    public Loan store(Loan loan) {
        return loanRepository.save(loan);
    }
}
