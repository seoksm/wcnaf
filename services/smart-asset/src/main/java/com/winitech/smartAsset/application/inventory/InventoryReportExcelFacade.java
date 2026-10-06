package com.winitech.smartAsset.application.inventory;

import com.winitech.common.library.WiniExcel;
import com.winitech.smartAsset.domain.inventory.InventoryReportInfo;
import com.winitech.smartAsset.domain.inventory.InventoryResult;
import com.winitech.smartAsset.domain.inventory.InventoryResultRowInfo;
import com.winitech.smartAsset.domain.inventory.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * S-305 리포트 엑셀 다운로드(참고용) - §4 "진행 중 리포트는 엑셀로만" 규칙에 따라 진행중/종료 모두
 * 내려받을 수 있다. PDF(종료 확정 후 감사 제출용)는 이 프로젝트에 PDF 생성 인프라가 없어 미구현이다
 * (운영 문서 참고). 핵심 섹션인 "미확인 처리 내역"을 자산 단위로 그대로 노출한다.
 */
@Service
@RequiredArgsConstructor
public class InventoryReportExcelFacade {

    private static final List<String> HEADERS = List.of("자산코드", "자산명", "종류", "종결처리방법", "사유", "메모", "장부가");

    private final InventoryService inventoryService;

    public void downloadReport(UUID inventoryId, HttpServletRequest req, HttpServletResponse res) {
        InventoryReportInfo report = inventoryService.loadReport(inventoryId);
        List<InventoryResultRowInfo> closedRows = inventoryService.loadResultRows(inventoryId, InventoryResult.Status.UNCONFIRMED)
                .stream()
                .filter(row -> row.getClosureAction() != null)
                .collect(Collectors.toList());

        String title = String.format(
                "%s 전수조사 리포트 (전체 %d건 · 확인완료 %d · 승인대기 %d · 이상 %d · 미확인처리 %d건)",
                report.getTitle(), report.getTotalCount(), report.getConfirmedCount(),
                report.getPendingApprovalCount(), report.getAnomalyCount(), closedRows.size());

        WiniExcel.WiniExcelOptions options = WiniExcel.WiniExcelOptions.builder()
                .title(title)
                .headerList(HEADERS)
                .build();

        WiniExcel.downloadExcel(options, "전수조사_리포트_" + report.getTitle(), HEADERS, toDataList(closedRows), req, res);
    }

    private List<Map<String, Object>> toDataList(List<InventoryResultRowInfo> rows) {
        return rows.stream().map(row -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("자산코드", row.getAssetCode());
            map.put("자산명", row.getAssetName());
            map.put("종류", row.getCategoryName());
            map.put("종결처리방법", row.getClosureAction() == null ? "" : row.getClosureAction().getDescription());
            map.put("사유", row.getClosureReasonCode() == null ? "" : row.getClosureReasonCode().getDescription());
            map.put("메모", row.getClosureNote() == null ? "" : row.getClosureNote());
            map.put("장부가", row.getBookValue());
            return map;
        }).collect(Collectors.toList());
    }
}
