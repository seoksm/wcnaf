package com.winitech.smartAsset.application.tangibleAsset;

import com.winitech.common.domain.common.CommonUserReader;
import com.winitech.common.exception.IllegalStatusException;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.tangibleAsset.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * S-213 엑셀 확정의 행(row) 1건을 독립 트랜잭션으로 처리하는 컴포넌트.
 * <p>
 * {@link ExcelCommitRowProcessor}(같은 클래스가 아니라 별도 스프링 빈)가 이 빈의 메서드를 호출하는
 * 형태로만 트랜잭션 경계가 성립한다 - 같은 클래스 안에서 this.processInNewTransaction(...)처럼
 * self-invocation으로 호출하면 스프링 AOP 프록시를 거치지 않아 @Transactional이 조용히 무시된다.
 * <p>
 * processInNewTransaction()은 REQUIRES_NEW로 "행 claim → 재검증 → 자산 등록/수정 → 행 완료 기록"을
 * 하나의 트랜잭션에 묶는다 - TangibleAssetService.createTangibleAsset/updateTangibleAsset은 기본
 * 전파(REQUIRED)라 이 트랜잭션에 그대로 합류하므로, 자산 저장과 행 완료 기록은 항상 함께 커밋되거나
 * 함께 롤백된다(프로세스가 그 사이에 죽어도 두 상태가 어긋나지 않는다). 실패 시 recordFailure()를
 * 별도의 새 트랜잭션(REQUIRES_NEW)으로 호출해야 한다 - 실패한 시도의 트랜잭션은 이미 롤백 대상으로
 * 표시돼 있어 그 안에서 실패 기록을 남기면 그 기록조차 함께 롤백되기 때문이다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExcelCommitRowExecutor {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final ExcelCommitRowStore excelCommitRowStore;
    private final ExcelRowSigner excelRowSigner;
    private final CommonUserReader commonUserReader;
    private final TangibleAssetReader tangibleAssetReader;
    private final TangibleAssetService tangibleAssetService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ExcelCommitRowResult processInNewTransaction(UUID commitId, String rowKey, TangibleAssetExcelRow row, UUID batchId) {
        if (!excelCommitRowStore.claimRow(commitId, rowKey, row.getRowNum())) {
            // 이 사이에 이미 다른 시도가 COMPLETED/FAILED로 끝냈다 - 그 결과를 그대로 재사용한다.
            return excelCommitRowStore.findFinished(commitId, rowKey)
                    .orElseThrow(() -> new IllegalStatusException("행 처리 상태를 확인할 수 없습니다."));
        }

        revalidateRow(row);

        UUID resultAssetId;
        String resultAction;
        if ("CREATE".equals(row.getAction())) {
            resultAssetId = tangibleAssetService.createTangibleAsset(buildCreateCommand(row), batchId);
            resultAction = "CREATED";
        } else {
            tangibleAssetService.updateTangibleAsset(buildUpdateCommand(row), batchId);
            resultAssetId = row.getTangibleAssetId();
            resultAction = "UPDATED";
        }

        ExcelCommitRowResult result = ExcelCommitRowResult.success(resultAction, resultAssetId);
        excelCommitRowStore.completeRow(commitId, rowKey, result);
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(UUID commitId, String rowKey, int rowNum, String errorMessage) {
        excelCommitRowStore.failRow(commitId, rowKey, rowNum, errorMessage);
    }

    /**
     * commit 시 서버가 다시 확인하는 것들:
     * 1) 서명 재검증 - preview 이후 클라이언트가 어떤 필드든 바꿨으면 서명이 더 이상 일치하지 않는다.
     * 2) 배정 사용자 실존 여부 - preview 이후 삭제/비활성화됐을 수 있다.
     * 3) (UPDATE만) 대상 자산이 여전히 존재하는지 - 실제 stale(버전 불일치) 검사는
     *    TangibleAssetServiceImpl.applyUpdate()가 자산을 다시 읽은 직후 수행한다(TOCTOU 최소화).
     * category/location 존재 여부는 create/updateTangibleAsset이 ID로 다시 조회하는 과정에서
     * 항상 재검증된다(라벨은 신뢰하지 않고 ID만 사용).
     */
    private void revalidateRow(TangibleAssetExcelRow row) {
        if (!excelRowSigner.verify(row)) {
            throw new InvalidParamException("요청 데이터가 서버가 발급한 미리보기 결과와 일치하지 않습니다. 다시 미리보기해주세요.");
        }

        if (row.getCurrentMemberId() != null && !commonUserReader.isExistCommonUserById(row.getCurrentMemberId())) {
            throw new InvalidParamException("배정 대상 사용자를 찾을 수 없습니다. 다시 미리보기해주세요.");
        }

        if ("UPDATE".equals(row.getAction())) {
            try {
                tangibleAssetReader.findById(row.getTangibleAssetId());
            } catch (Exception e) {
                throw new InvalidParamException("자산을 찾을 수 없습니다. 이미 삭제되었을 수 있습니다.");
            }
        }
    }

    private TangibleAssetCommand buildCreateCommand(TangibleAssetExcelRow row) {
        return TangibleAssetCommand.builder()
                .assetName(row.getAssetName())
                .categoryId(row.getCategoryId())
                .locationId(row.getLocationId())
                .lifeStatus(TangibleAsset.LifeStatus.valueOf(row.getLifeStatus()))
                .assignType(TangibleAsset.AssignType.valueOf(row.getAssignType()))
                .acquisitionDate(LocalDate.parse(row.getAcquisitionDate(), DATE_FORMAT))
                .acquisitionAmount(new BigDecimal(row.getAcquisitionAmount()))
                .modelName(row.getModelName())
                .manufacturer(row.getManufacturer())
                .serialNo(row.getSerialNo())
                .currentMemberId(row.getCurrentMemberId())
                .memo(row.getMemo())
                .build();
    }

    private TangibleAssetCommand.UpdateCommand buildUpdateCommand(TangibleAssetExcelRow row) {
        return TangibleAssetCommand.UpdateCommand.builder()
                .tangibleAssetId(row.getTangibleAssetId())
                .assetName(row.getAssetName())
                .categoryId(row.getCategoryId())
                .locationId(row.getLocationId())
                .lifeStatus(TangibleAsset.LifeStatus.valueOf(row.getLifeStatus()))
                .assignType(TangibleAsset.AssignType.valueOf(row.getAssignType()))
                .acquisitionDate(LocalDate.parse(row.getAcquisitionDate(), DATE_FORMAT))
                .acquisitionAmount(new BigDecimal(row.getAcquisitionAmount()))
                .modelName(row.getModelName())
                .manufacturer(row.getManufacturer())
                .serialNo(row.getSerialNo())
                .currentMemberId(row.getCurrentMemberId())
                .memo(row.getMemo())
                .expectedVersion(row.getEntityVersion())
                .build();
    }
}
