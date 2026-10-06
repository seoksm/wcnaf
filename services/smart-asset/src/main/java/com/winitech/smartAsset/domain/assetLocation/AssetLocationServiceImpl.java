package com.winitech.smartAsset.domain.assetLocation;

import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AssetLocationServiceImpl extends EgovAbstractServiceImpl implements AssetLocationService {

    private final AssetLocationReader assetLocationReader;
    private final AssetLocationStore assetLocationStore;
    private final TangibleAssetReader tangibleAssetReader;

    @Transactional
    @Override
    public UUID createLocation(AssetLocationCommand command) {
        return assetLocationStore.store(command.toEntity());
    }

    @Transactional
    @Override
    public void updateLocation(AssetLocationCommand.UpdateCommand updateCommand) {

        AssetLocation assetLocation = assetLocationReader.findById(updateCommand.getLocationId());
        assetLocationStore.modify(assetLocation, updateCommand);
    }

    @Transactional
    @Override
    public void deleteLocation(UUID locationId) {

        AssetLocation assetLocation = assetLocationReader.findById(locationId);

        long usageCount = tangibleAssetReader.countByLocationId(locationId);
        if (usageCount > 0) {
            throw new InvalidParamException("자산 " + usageCount + "건이 이 위치를 사용 중입니다.");
        }

        assetLocationStore.delete(assetLocation);
    }

    @Override
    public List<AssetLocationInfo> loadLocationList(String keyword) {

        return assetLocationReader.findAllByContainsKeyword(keyword).stream()
                .map(AssetLocationInfo::new)
                .collect(Collectors.toList());
    }

    @Override
    public AssetLocationInfo loadLocation(UUID locationId) {

        return new AssetLocationInfo(assetLocationReader.findById(locationId));
    }
}
