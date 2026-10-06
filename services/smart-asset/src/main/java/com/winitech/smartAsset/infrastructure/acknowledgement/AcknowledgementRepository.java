package com.winitech.smartAsset.infrastructure.acknowledgement;

import com.winitech.smartAsset.domain.acknowledgement.Acknowledgement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AcknowledgementRepository extends JpaRepository<Acknowledgement, UUID> {

    @EntityGraph(attributePaths = {"tangibleAsset", "tangibleAsset.category"})
    Page<Acknowledgement> findAllByStatus(Acknowledgement.Status status, Pageable pageable);

    @EntityGraph(attributePaths = {"tangibleAsset", "tangibleAsset.category"})
    Page<Acknowledgement> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"tangibleAsset", "tangibleAsset.category"})
    List<Acknowledgement> findAllByMemberIdAndStatusOrderByRequestedAtAsc(UUID memberId, Acknowledgement.Status status);
}
