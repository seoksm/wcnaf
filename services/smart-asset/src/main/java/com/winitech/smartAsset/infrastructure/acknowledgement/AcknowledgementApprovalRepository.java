package com.winitech.smartAsset.infrastructure.acknowledgement;

import com.winitech.smartAsset.domain.acknowledgement.AcknowledgementApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AcknowledgementApprovalRepository extends JpaRepository<AcknowledgementApproval, UUID> {

    List<AcknowledgementApproval> findAllByAcknowledgement_IdOrderByApprovedAtAsc(UUID acknowledgementId);
}
