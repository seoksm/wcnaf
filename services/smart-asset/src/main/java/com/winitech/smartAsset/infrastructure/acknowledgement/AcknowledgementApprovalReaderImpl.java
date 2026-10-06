package com.winitech.smartAsset.infrastructure.acknowledgement;

import com.winitech.smartAsset.domain.acknowledgement.AcknowledgementApproval;
import com.winitech.smartAsset.domain.acknowledgement.AcknowledgementApprovalReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AcknowledgementApprovalReaderImpl implements AcknowledgementApprovalReader {

    private final AcknowledgementApprovalRepository acknowledgementApprovalRepository;

    @Override
    public List<AcknowledgementApproval> findAllByAcknowledgementId(UUID acknowledgementId) {
        return acknowledgementApprovalRepository.findAllByAcknowledgement_IdOrderByApprovedAtAsc(acknowledgementId);
    }
}
