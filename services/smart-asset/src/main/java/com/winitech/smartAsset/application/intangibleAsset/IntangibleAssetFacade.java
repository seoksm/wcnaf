package com.winitech.smartAsset.application.intangibleAsset;

import com.winitech.smartAsset.domain.intangibleAsset.IntangibleAssetActionLogInfo;
import com.winitech.smartAsset.domain.intangibleAsset.IntangibleAssetCommand;
import com.winitech.smartAsset.domain.intangibleAsset.IntangibleAssetInfo;
import com.winitech.smartAsset.domain.intangibleAsset.IntangibleAssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntangibleAssetFacade {

    private final IntangibleAssetService intangibleAssetService;

    public UUID postIntangibleAsset(IntangibleAssetCommand command) {
        return intangibleAssetService.createIntangibleAsset(command);
    }

    public void reviseIntangibleAsset(IntangibleAssetCommand.UpdateCommand updateCommand) {
        intangibleAssetService.updateIntangibleAsset(updateCommand);
    }

    public void removeIntangibleAsset(UUID intangibleAssetId) {
        intangibleAssetService.deleteIntangibleAsset(intangibleAssetId);
    }

    public Page<IntangibleAssetInfo> getList(String keyword, Integer withinDays, Integer page, Integer size) {
        return intangibleAssetService.loadList(keyword, withinDays, page, size);
    }

    public IntangibleAssetInfo getIntangibleAsset(UUID intangibleAssetId) {
        return intangibleAssetService.loadIntangibleAsset(intangibleAssetId);
    }

    public List<IntangibleAssetActionLogInfo> getActionLog(UUID intangibleAssetId) {
        return intangibleAssetService.loadActionLog(intangibleAssetId);
    }

    public void renew(UUID intangibleAssetId, LocalDate newExpiryDate, String note) {
        intangibleAssetService.renew(intangibleAssetId, newExpiryDate, note);
    }
}
