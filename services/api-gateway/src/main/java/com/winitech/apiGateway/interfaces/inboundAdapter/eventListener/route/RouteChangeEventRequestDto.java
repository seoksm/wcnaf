package com.winitech.apiGateway.interfaces.inboundAdapter.eventListener.route;

import lombok.*;
import java.util.*;
import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RouteChangeEventRequestDto {
    private Integer ts_ms;

}