package com.winitech.system.domain.code;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CodeInfo {

    private final UUID codeId;
    private final String code;
    private final String codeName;
    private final String codeDescription;
    private final Integer codeDepthNo;
    private final Integer codeOrderNo;
    private final Code.UseStatus codeUseStatus;

    public CodeInfo(Code code) {
        this.codeId = code.getId();
        this.code = code.getCode();
        this.codeName = code.getName();
        this.codeDescription = code.getDescription();
        this.codeDepthNo = code.getDepthNo();
        this.codeOrderNo = code.getOrderNo();
        this.codeUseStatus = code.getUseStatus();
    }
}
