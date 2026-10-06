package com.winitech.smartAsset.interfaces.inboundAdapter.web.tangibleAsset;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.common.response.CommonResponse;
import com.winitech.smartAsset.application.tangibleAsset.TangibleAssetExcelFacade;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetExcelCommitRequest;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetExcelCommitResult;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetExcelRow;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * S-213 유형자산 엑셀 업서트 - 양식 다운로드 / 미리보기 / 확정.
 * openapi-generator 파이프라인을 쓰지 않는 파일 업/다운로드 전용 컨트롤러
 * (CommonFileController와 동일하게 apiSpec.yaml 밖에서 직접 작성).
 */
@Api(tags = "TangibleAsset")
@RestController
@ForceDefaultTenant
@RequiredArgsConstructor
@RequestMapping("/api/v1/smart-asset/tangible-asset/excel")
public class TangibleAssetExcelApiController {

    private final TangibleAssetExcelFacade tangibleAssetExcelFacade;

    @ApiOperation(value = "유형자산 등록 양식 다운로드")
    @GetMapping("/template")
    public void downloadTemplate(HttpServletRequest req, HttpServletResponse res) {
        tangibleAssetExcelFacade.downloadTemplate(req, res);
    }

    @ApiOperation(value = "유형자산 엑셀 업서트 미리보기 (저장하지 않음)")
    @PostMapping("/preview")
    public CommonResponse<List<TangibleAssetExcelRow>> preview(@RequestParam("file") MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".xlsx")) {
            throw new InvalidParamException(".xlsx 형식의 엑셀 파일만 업로드할 수 있습니다.");
        }

        List<TangibleAssetExcelRow> rows = tangibleAssetExcelFacade.preview(file.getInputStream());
        return CommonResponse.success(rows);
    }

    @ApiOperation(value = "유형자산 엑셀 업서트 확정 - commitId는 같은 확정 시도에서 항상 동일해야 하며 서버가 중복 처리를 막는 데 쓰인다")
    @PostMapping("/commit")
    public CommonResponse<TangibleAssetExcelCommitResult> commit(@RequestBody TangibleAssetExcelCommitRequest request) {
        if (request.getCommitId() == null) {
            throw new InvalidParamException("commitId는 필수입니다.");
        }

        TangibleAssetExcelCommitResult result = tangibleAssetExcelFacade.commit(request.getCommitId(), request.getRows());
        return CommonResponse.success(result);
    }
}
