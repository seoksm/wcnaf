package com.winitech.smartAsset.domain.software;

import lombok.Getter;

import java.util.UUID;

@Getter
public class SoftwareInfo {

    private final UUID softwareId;
    private final String name;
    private final String publisher;
    private final String category;
    private final String memo;
    private final Software.Status status;

    public SoftwareInfo(Software software) {
        this.softwareId = software.getId();
        this.name = software.getName();
        this.publisher = software.getPublisher();
        this.category = software.getCategory();
        this.memo = software.getMemo();
        this.status = software.getStatus();
    }
}
