package com.winitech.smartAsset.domain.acknowledgement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AcknowledgementReader {

    Acknowledgement findById(UUID acknowledgementId);

    /** S-431 확인서 현황 - statusFilter가 null이면 전체 */
    Page<Acknowledgement> findAll(Acknowledgement.Status statusFilter, Pageable pageable);

    /** S-440 - 로그인한 본인의 임직원 승인대기 건 */
    List<Acknowledgement> findAllPendingEmployeeByMemberId(UUID memberId);
}
