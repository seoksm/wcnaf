package com.winitech.smartAsset.infrastructure.intangibleAsset;

import com.winitech.smartAsset.domain.intangibleAsset.IntangibleAsset;
import com.winitech.smartAsset.domain.intangibleAsset.IntangibleAssetCommand;
import com.winitech.smartAsset.domain.intangibleAsset.IntangibleAssetStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class IntangibleAssetStoreImpl implements IntangibleAssetStore {

    private final IntangibleAssetRepository intangibleAssetRepository;

    @Override
    public UUID store(IntangibleAsset intangibleAsset) {
        return intangibleAssetRepository.save(intangibleAsset).getId();
    }

    @Override
    public void modify(IntangibleAsset intangibleAsset, IntangibleAssetCommand.UpdateCommand updateCommand) {
        intangibleAsset.modify(updateCommand);
    }

    @Override
    public void delete(IntangibleAsset intangibleAsset) {
        intangibleAsset.delete();
    }
}
