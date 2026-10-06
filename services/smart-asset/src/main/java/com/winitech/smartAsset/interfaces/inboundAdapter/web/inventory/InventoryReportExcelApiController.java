package com.winitech.smartAsset.interfaces.inboundAdapter.web.inventory;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.smartAsset.application.inventory.InventoryReportExcelFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * S-305 리포트 엑셀 다운로드 - openapi-generator 파이프라인을 쓰지 않는 파일 다운로드 전용 컨트롤러
 * (TangibleAssetExcelApiController와 동일하게 apiSpec.yaml 밖에서 직접 작성).
 */
@Api(tags = "Inventory")
@RestController
@ForceDefaultTenant
@RequiredArgsConstructor
@RequestMapping("/api/v1/smart-asset/inventory")
public class InventoryReportExcelApiController {

    private final InventoryReportExcelFacade inventoryReportExcelFacade;

    @ApiOperation(value = "전수조사 리포트 엑셀 다운로드")
    @GetMapping("/{inventoryId}/report/excel")
    public void downloadReport(@PathVariable UUID inventoryId, HttpServletRequest req, HttpServletResponse res) {
        inventoryReportExcelFacade.downloadReport(inventoryId, req, res);
    }
}
