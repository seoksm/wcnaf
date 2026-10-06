package com.winitech.smartAsset.domain.acknowledgement;

import java.util.List;
import java.util.UUID;

public interface AcknowledgementApprovalReader {

    List<AcknowledgementApproval> findAllByAcknowledgementId(UUID acknowledgementId);
}
