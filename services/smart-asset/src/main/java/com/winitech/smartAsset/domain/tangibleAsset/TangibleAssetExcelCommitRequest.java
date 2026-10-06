package com.winitech.smartAsset.domain.tangibleAsset;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * S-213 엑셀 업서트 확정 요청. commitId는 클라이언트가 "확정" 버튼을 누른 시점에 한 번만 생성해
 * 재시도해도 같은 값을 보내야 한다 - 서버가 이 값으로 중복 처리를 막는다(멱등성).
 */
@Getter
@Setter
public class TangibleAssetExcelCommitRequest {
    private UUID commitId;
    private List<TangibleAssetExcelRow> rows;
}
