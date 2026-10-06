package com.winitech.smartAsset.domain.tangibleAsset;

/**
 * S-213 엑셀 업서트 preview 결과 위변조 방지 포트.
 * preview()가 만든 각 행에 sign()으로 서명을 붙여 클라이언트에 내려주고, commit()은 클라이언트가
 * 그대로 돌려준 행을 verify()로 재검증한다. 서명 대상에는 실제 저장에 쓰이는 필드와 preview 시점
 * 자산의 버전(updateAt)까지 포함되므로, 클라이언트가 그중 무엇이든 바꾸면 검증에 실패한다.
 */
public interface ExcelRowSigner {

    String sign(TangibleAssetExcelRow row);

    boolean verify(TangibleAssetExcelRow row);
}
