package com.winitech.smartAsset.infrastructure.tangibleAsset;

import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TangibleAssetReaderImpl implements TangibleAssetReader {

    private final TangibleAssetRepository tangibleAssetRepository;

    @Override
    public TangibleAsset findById(UUID tangibleAssetId) {
        return tangibleAssetRepository.findById(tangibleAssetId).orElseThrow();
    }

    @Override
    public Page<TangibleAsset> findAllByContainsKeyword(String keyword, Pageable pageable) {
        return tangibleAssetRepository.findAllByContainsKeyword(keyword, pageable);
    }

    @Override
    public Optional<TangibleAsset> findByAssetCode(String assetCode) {
        return tangibleAssetRepository.findByAssetCodeAndStatus(assetCode, TangibleAsset.Status.ENABLE);
    }

    @Override
    public long countByCategoryId(UUID categoryId) {
        return tangibleAssetRepository.countByCategoryId(categoryId);
    }

    @Override
    public long countByLocationId(UUID locationId) {
        return tangibleAssetRepository.countByLocationId(locationId);
    }

    @Override
    public Page<TangibleAsset> findByLifeStatusIn(Collection<TangibleAsset.LifeStatus> lifeStatuses, Pageable pageable) {
        return tangibleAssetRepository.findByLifeStatusIn(lifeStatuses, pageable);
    }

    @Override
    public List<TangibleAsset> findAllByAssignTypeInAndLifeStatus(Collection<TangibleAsset.AssignType> assignTypes,
                                                                    TangibleAsset.LifeStatus lifeStatus) {
        return tangibleAssetRepository.findAllByAssignTypeInAndLifeStatusAndStatus(
                assignTypes, lifeStatus, TangibleAsset.Status.ENABLE);
    }

    @Override
    public List<TangibleAsset> findAllByAssignTypeIn(Collection<TangibleAsset.AssignType> assignTypes) {
        return tangibleAssetRepository.findAllByAssignTypeInAndStatus(assignTypes, TangibleAsset.Status.ENABLE);
    }

    @Override
    public List<Object[]> countActiveGroupByCategory() {
        return tangibleAssetRepository.countActiveGroupByCategory();
    }

    @Override
    public List<Object[]> countActiveGroupByLocation() {
        return tangibleAssetRepository.countActiveGroupByLocation();
    }

    @Override
    public List<Object[]> countGroupByLifeStatus() {
        return tangibleAssetRepository.countGroupByLifeStatus();
    }
}
