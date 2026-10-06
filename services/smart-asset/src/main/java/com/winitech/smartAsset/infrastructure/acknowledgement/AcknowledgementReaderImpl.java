package com.winitech.smartAsset.infrastructure.acknowledgement;

import com.winitech.smartAsset.domain.acknowledgement.Acknowledgement;
import com.winitech.smartAsset.domain.acknowledgement.AcknowledgementReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AcknowledgementReaderImpl implements AcknowledgementReader {

    private final AcknowledgementRepository acknowledgementRepository;

    @Override
    public Acknowledgement findById(UUID acknowledgementId) {
        return acknowledgementRepository.findById(acknowledgementId).orElseThrow();
    }

    @Override
    public Page<Acknowledgement> findAll(Acknowledgement.Status statusFilter, Pageable pageable) {
        return statusFilter == null
                ? acknowledgementRepository.findAll(pageable)
                : acknowledgementRepository.findAllByStatus(statusFilter, pageable);
    }

    @Override
    public List<Acknowledgement> findAllPendingEmployeeByMemberId(UUID memberId) {
        return acknowledgementRepository.findAllByMemberIdAndStatusOrderByRequestedAtAsc(
                memberId, Acknowledgement.Status.PENDING_EMPLOYEE);
    }
}
