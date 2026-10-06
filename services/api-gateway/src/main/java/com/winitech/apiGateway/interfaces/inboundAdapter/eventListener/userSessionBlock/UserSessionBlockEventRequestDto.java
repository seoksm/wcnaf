package com.winitech.apiGateway.interfaces.inboundAdapter.eventListener.userSessionBlock;

import lombok.*;
import java.util.*;
import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserSessionBlockEventRequestDto {
    private OPERATION op;
    private Integer ts_ms;
    private UUID userSessionId;
    private OffsetDateTime accessTokenExpiresAt;
    private UUID userId;

    @Getter
    @RequiredArgsConstructor
    public enum OPERATION {
        s("sync");
        private final String description;
    }

}