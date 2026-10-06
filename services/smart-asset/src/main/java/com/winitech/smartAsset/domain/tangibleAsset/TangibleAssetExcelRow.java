package com.winitech.smartAsset.domain.tangibleAsset;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * S-213 엑셀 업서트 - 미리보기 결과와 확정 요청이 공유하는 행 단위 모델.
 * 미리보기 단계에서 이름 기반 참조(종류·위치·사용자)를 ID로 미리 해석해 두고,
 * 확정 단계는 이 값을 그대로 재사용해 다시 파싱하지 않는다 (X2·X5).
 */
@Getter
@Setter
@Builder
public class TangibleAssetExcelRow {

    /** 엑셀 상의 실제 행 번호 (오류 메시지 표시용) */
    private int rowNum;

    /** CREATE | UPDATE | ERROR */
    private String action;

    /** action=ERROR일 때의 사유 */
    private String errorMessage;

    /** action=UPDATE일 때 갱신 대상 (자산코드로 조회해 미리 채워둔다) */
    private UUID tangibleAssetId;

    private String assetCode;
    private String assetName;

    private String categoryName;
    private UUID categoryId;

    private String locationName;
    private UUID locationId;

    /** USE/STORAGE/REPAIR/DISUSE/DISPOSED. 원본 엑셀은 한글 라벨이지만 미리보기에서 코드로 변환해 둔다 */
    private String lifeStatus;
    /** UNASSIGNED/PERSONAL/SHARED/LOANABLE/ON_LOAN */
    private String assignType;

    /** 사용자사번 또는 사용자명 원본 입력값 (표시용) */
    private String memberInput;
    private UUID currentMemberId;

    /** yyyy-MM-dd */
    private String acquisitionDate;
    /** 문자열로 주고받아 화면 표시 시 정밀도 손실을 피한다 */
    private String acquisitionAmount;

    private String modelName;
    private String manufacturer;
    private String serialNo;
    private String memo;

    /** preview 시점 기존 자산의 버전(TangibleAsset.version, UPDATE 행만) - commit 시점에 현재 버전과
     * 다르면 그 사이 다른 곳에서 이미 수정된 것이므로 stale preview로 간주해 반영을 거부한다 (X3).
     * updateAt 문자열 비교 대신 JPA @Version 정수 비교를 쓰는 이유: 문자열 비교는 시각 표현 방식이나
     * 정밀도가 조금만 달라도 오탐/누락이 생길 수 있고, 무엇보다 "확인 시점"과 "실제 반영 시점" 사이의
     * 경쟁 상태(TOCTOU)를 막지 못한다 - 실제 반영은 이 값을 그대로 들고 가 TangibleAssetService의
     * 갱신 호출이 자산을 다시 읽은 직후 이 값과 비교하므로, 그 사이 값이 바뀌어도 감지된다. */
    private Long entityVersion;

    /** preview가 서버 서명 키로 계산해 붙인 서명 - commit이 재계산해 비교하면 클라이언트가
     * 어떤 필드든(카테고리·위치·사용자·상태·금액 등) 바꿨는지 감지할 수 있다 (X4) */
    private String signature;
}
