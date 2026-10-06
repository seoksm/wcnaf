package com.winitech.apiGateway.interfaces.inboundAdapter.eventListener.common;

import lombok.*;
import java.util.*;
import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CommonAuthorizationDataEventRequestDto {
    private OPERATION op;
    private Integer ts_ms;
    private String message;

    @Getter
    @RequiredArgsConstructor
    public enum OPERATION {
        s("sync");
        private final String description;
    }

}