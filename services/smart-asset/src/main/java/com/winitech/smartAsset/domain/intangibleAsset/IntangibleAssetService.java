package com.winitech.smartAsset.domain.intangibleAsset;

import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IntangibleAssetService {

    UUID createIntangibleAsset(IntangibleAssetCommand command);

    void updateIntangibleAsset(IntangibleAssetCommand.UpdateCommand updateCommand);

    void deleteIntangibleAsset(UUID intangibleAssetId);

    /** S-500 목록 - withinDays가 있으면 "N일 내 만료(+이미 만료된 것 전부)"로 필터 */
    Page<IntangibleAssetInfo> loadList(String keyword, Integer withinDays, Integer page, Integer size);

    IntangibleAssetInfo loadIntangibleAsset(UUID intangibleAssetId);

    /** S-502 갱신 이력 */
    List<IntangibleAssetActionLogInfo> loadActionLog(UUID intangibleAssetId);

    /** S-502 갱신 - 만료일을 미룬다 */
    void renew(UUID intangibleAssetId, LocalDate newExpiryDate, String note);
}
