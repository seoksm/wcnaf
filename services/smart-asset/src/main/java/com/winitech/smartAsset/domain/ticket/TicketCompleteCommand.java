package com.winitech.smartAsset.domain.ticket;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * S-602 완료 처리 - PURCHASE 유형만 자산 등록 필드가 채워진다(Q-49). S-212와 동일한 필수 5개
 * (자산명·종류·위치·취득일·취득가액)를 그대로 받는다. requesterName/managerName은 수령확인서
 * 스냅샷에 필요한데 이 서비스가 멤버 이름을 모르므로(system 서비스 소관) 프런트가 넘겨준다
 * (Acknowledgement 요청 화면과 동일한 이유).
 */
@Getter
@Builder
@ToString
public class TicketCompleteCommand {

    private String assetName;
    private UUID categoryId;
    private UUID locationId;
    private LocalDate acquisitionDate;
    private BigDecimal acquisitionAmount;
    private String modelName;
    private String manufacturer;
    private String serialNo;
    private String memo;
    private String requesterName;
    private String managerName;
}
