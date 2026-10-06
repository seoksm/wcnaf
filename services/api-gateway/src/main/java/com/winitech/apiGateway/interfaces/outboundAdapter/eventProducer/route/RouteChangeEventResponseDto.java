package com.winitech.apiGateway.interfaces.outboundAdapter.eventProducer.route;

import lombok.*;
import java.util.*;
import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@Builder
public class RouteChangeEventResponseDto {
    private Integer ts_ms;

}