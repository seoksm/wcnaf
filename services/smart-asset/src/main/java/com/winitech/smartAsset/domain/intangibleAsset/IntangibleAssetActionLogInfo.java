package com.winitech.smartAsset.domain.intangibleAsset;

import lombok.Getter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
public class IntangibleAssetActionLogInfo {

    private final UUID intangibleAssetActionLogId;
    private final IntangibleAssetActionLog.ActionType actionType;
    private final LocalDate previousExpiryDate;
    private final LocalDate newExpiryDate;
    private final String note;
    private final UUID actedBy;
    private final OffsetDateTime actedAt;

    public IntangibleAssetActionLogInfo(IntangibleAssetActionLog log) {
        this.intangibleAssetActionLogId = log.getId();
        this.actionType = log.getActionType();
        this.previousExpiryDate = log.getPreviousExpiryDate();
        this.newExpiryDate = log.getNewExpiryDate();
        this.note = log.getNote();
        this.actedBy = log.getActedBy();
        this.actedAt = log.getActedAt();
    }
}
