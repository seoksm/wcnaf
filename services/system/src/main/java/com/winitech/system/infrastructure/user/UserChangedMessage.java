package com.winitech.system.infrastructure.user;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
* com.winitech.system.infrastructure.user
* ㄴ UserChangedMessage.java
* @author : 박준희 과장 (부설연구소)
* @since : 2022-08-29 오후 5:49
* @see : None
 **/
@Data
public class UserChangedMessage {
    OPERATION op;
    Integer ts_ms;
    Object before;
    Object after;
    Object userOrganizationInfo;

    @Getter
    @RequiredArgsConstructor
    public enum OPERATION {
        c("CREATE"), u("UPDATE"), d("DELETE"),
        r("REMOVE_FROM_ORG");
        private final String description;
    }
    @Builder
    public UserChangedMessage(OPERATION op, Integer ts_ms, Object before, Object after, Object userOrganizationInfo) {
        this.op = op;
        this.ts_ms = ts_ms;
        this.before = before;
        this.after = after;
        this.userOrganizationInfo = userOrganizationInfo;
    }
}
