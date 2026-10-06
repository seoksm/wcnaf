package com.winitech.smartAsset.infrastructure.acknowledgement;

import com.winitech.smartAsset.domain.acknowledgement.Acknowledgement;
import com.winitech.smartAsset.domain.acknowledgement.AcknowledgementStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AcknowledgementStoreImpl implements AcknowledgementStore {

    private final AcknowledgementRepository acknowledgementRepository;

    @Override
    public Acknowledgement store(Acknowledgement acknowledgement) {
        return acknowledgementRepository.save(acknowledgement);
    }
}
