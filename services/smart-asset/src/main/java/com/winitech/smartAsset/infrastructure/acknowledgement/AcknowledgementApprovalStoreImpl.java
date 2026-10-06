package com.winitech.smartAsset.infrastructure.acknowledgement;

import com.winitech.smartAsset.domain.acknowledgement.AcknowledgementApproval;
import com.winitech.smartAsset.domain.acknowledgement.AcknowledgementApprovalStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AcknowledgementApprovalStoreImpl implements AcknowledgementApprovalStore {

    private final AcknowledgementApprovalRepository acknowledgementApprovalRepository;

    @Override
    public AcknowledgementApproval store(AcknowledgementApproval acknowledgementApproval) {
        return acknowledgementApprovalRepository.save(acknowledgementApproval);
    }
}
