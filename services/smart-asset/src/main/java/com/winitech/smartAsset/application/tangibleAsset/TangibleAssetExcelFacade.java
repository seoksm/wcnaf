package com.winitech.smartAsset.application.tangibleAsset;

import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.domain.common.CommonUser;
import com.winitech.common.domain.common.CommonUserReader;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.common.library.WiniExcel;
import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.assetCategory.AssetCategoryReader;
import com.winitech.smartAsset.domain.assetLocation.AssetLocation;
import com.winitech.smartAsset.domain.assetLocation.AssetLocationReader;
import com.winitech.smartAsset.domain.tangibleAsset.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * S-213 엑셀 업서트 - 미리보기(파싱·검증만, 저장 없음) / 확정(실제 등록·수정) / 양식 다운로드.
 * X1~X5 규칙(설계문서 08 §3) 적용: 자산코드 유무로 신규/갱신 판단, 종류·위치·사용자는 이름으로 매칭,
 * 갱신 시 빈 칸은 "변경 없음"으로 처리, 확정 전에 행 단위 미리보기를 먼저 제공한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TangibleAssetExcelFacade {

    private static final List<String> HEADERS = List.of(
            "자산코드", "자산명", "자산종류", "자산위치", "취득일", "취득가액",
            "모델명", "제조사", "시리얼번호", "생애상태", "배정형태", "사용자사번", "사용자명", "메모"
    );

    /** 신규 등록 시 필수인 컬럼 (X1: 자산코드가 있으면 갱신이라 빈 칸은 기존 값 유지되므로 제외) */
    private static final Set<String> REQUIRED_HEADERS = Set.of("자산명", "자산종류", "자산위치", "취득일", "취득가액");

    /** 양식에 포함된 입력 예시 행을 식별하는 표시 - 이 표시가 붙은 행은 미리보기 파싱에서 제외한다 */
    private static final String EXAMPLE_ROW_MARKER = "(예시)";

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    /** 한 번에 업로드 가능한 최대 데이터 행 수 (헤더/예시 행 제외) */
    private static final int MAX_UPLOAD_ROWS = 2000;

    private final AssetCategoryReader assetCategoryReader;
    private final AssetLocationReader assetLocationReader;
    private final CommonUserReader commonUserReader;
    private final TangibleAssetReader tangibleAssetReader;
    private final ExcelRowSigner excelRowSigner;
    private final ExcelCommitIdempotencyStore idempotencyStore;
    private final ExcelCommitRowProcessor excelCommitRowProcessor;
    private final LoginUserContext loginUserContext;

    public void downloadTemplate(HttpServletRequest req, HttpServletResponse res) {
        // 신규 등록 시 필수 입력 항목은 헤더에 '*'를 붙여 표시한다 (실제 파싱 시에는 '*'를 무시하고 매칭)
        List<String> displayHeaders = HEADERS.stream()
                .map(header -> REQUIRED_HEADERS.contains(header) ? header + "*" : header)
                .toList();

        WiniExcel.WiniExcelOptions options = WiniExcel.WiniExcelOptions.builder()
                .title("유형자산 등록 양식  (신규 등록 시 '*' 표시 항목은 필수 입력 / 자산코드를 입력하면 수정이며 빈 칸은 기존 값 유지)")
                .headerList(displayHeaders)
                .build();

        WiniExcel.downloadExcel(options, "유형자산_등록양식", displayHeaders, buildExampleRows(displayHeaders), req, res);
    }

    /**
     * 예시 행의 자산종류·자산위치는 현재 마스터 데이터에서 실제로 존재하는 값을 반영한다(T1) -
     * 하드코딩된 이름("노트북"/"본사")은 그 이름의 종류·위치가 삭제되거나 다르게 편집된
     * 환경에서는 업로드 시 "등록되지 않은 종류/위치"로 검증 실패하는 예시가 돼버린다.
     * 마스터 데이터가 아직 하나도 없는 완전 초기 상태에서만 안내용 기본값으로 대체한다.
     */
    String exampleCategoryName() {
        List<AssetCategory> categories = assetCategoryReader.findAllByContainsKeyword(null);
        return categories.isEmpty() ? "노트북" : categories.get(0).getCategoryName();
    }

    String exampleLocationName() {
        List<AssetLocation> locations = assetLocationReader.findAllByContainsKeyword(null);
        return locations.isEmpty() ? "본사" : locations.get(0).getLocationName();
    }

    /** 입력 형식 안내용 예시 데이터 2행 - '자산명' 앞에 EXAMPLE_ROW_MARKER가 붙어 업로드 시 자동으로 무시된다 */
    private List<Map<String, Object>> buildExampleRows(List<String> displayHeaders) {
        Map<String, Object> row1 = new LinkedHashMap<>();
        row1.put(headerKey(displayHeaders, "자산코드"), "");
        row1.put(headerKey(displayHeaders, "자산명"), EXAMPLE_ROW_MARKER + " 개발자용 노트북");
        row1.put(headerKey(displayHeaders, "자산종류"), exampleCategoryName());
        row1.put(headerKey(displayHeaders, "자산위치"), exampleLocationName());
        row1.put(headerKey(displayHeaders, "취득일"), "2026-01-15");
        row1.put(headerKey(displayHeaders, "취득가액"), 1500000);
        row1.put(headerKey(displayHeaders, "모델명"), "ThinkPad T14");
        row1.put(headerKey(displayHeaders, "제조사"), "Lenovo");
        row1.put(headerKey(displayHeaders, "시리얼번호"), "SN-0001");
        row1.put(headerKey(displayHeaders, "생애상태"), "사용");
        row1.put(headerKey(displayHeaders, "배정형태"), "개인배정");
        row1.put(headerKey(displayHeaders, "사용자사번"), "EMP001");
        row1.put(headerKey(displayHeaders, "사용자명"), "홍길동");
        row1.put(headerKey(displayHeaders, "메모"), "신규 등록 예시 - 자산코드를 비우면 신규로 등록됩니다.");

        Map<String, Object> row2 = new LinkedHashMap<>();
        row2.put(headerKey(displayHeaders, "자산코드"), "AST-2026-0001");
        row2.put(headerKey(displayHeaders, "자산명"), EXAMPLE_ROW_MARKER + " 수정할 항목만 채우면 됨");
        row2.put(headerKey(displayHeaders, "자산종류"), "");
        row2.put(headerKey(displayHeaders, "자산위치"), "");
        row2.put(headerKey(displayHeaders, "취득일"), "");
        row2.put(headerKey(displayHeaders, "취득가액"), 1800000);
        row2.put(headerKey(displayHeaders, "모델명"), "");
        row2.put(headerKey(displayHeaders, "제조사"), "");
        row2.put(headerKey(displayHeaders, "시리얼번호"), "");
        row2.put(headerKey(displayHeaders, "생애상태"), "");
        row2.put(headerKey(displayHeaders, "배정형태"), "");
        row2.put(headerKey(displayHeaders, "사용자사번"), "");
        row2.put(headerKey(displayHeaders, "사용자명"), "");
        row2.put(headerKey(displayHeaders, "메모"), "수정 예시 - 자산코드를 입력하면 수정이며, 빈 칸은 기존 값이 그대로 유지됩니다(취득가액만 변경).");

        return List.of(row1, row2);
    }

    private String headerKey(List<String> displayHeaders, String baseHeader) {
        return displayHeaders.stream()
                .filter(h -> normalizeHeader(h).equals(baseHeader))
                .findFirst()
                .orElse(baseHeader);
    }

    @Transactional(readOnly = true)
    public List<TangibleAssetExcelRow> preview(InputStream excelStream) {
        List<AssetCategory> categories = assetCategoryReader.findAllByContainsKeyword(null);
        List<AssetLocation> locations = assetLocationReader.findAllByContainsKeyword(null);
        List<CommonUser> users = commonUserReader.getAllCommonUser();

        List<TangibleAssetExcelRow> result = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(excelStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Map<String, Integer> columnIndex = resolveHeaderColumns(sheet);

            for (Row row : sheet) {
                if (row.getRowNum() <= headerRowNum(sheet)) {
                    continue;
                }
                if (isBlankRow(row)) {
                    continue;
                }
                if (isExampleRow(row, columnIndex)) {
                    continue;
                }
                if (result.size() >= MAX_UPLOAD_ROWS) {
                    throw new InvalidParamException("한 번에 업로드할 수 있는 행은 최대 " + MAX_UPLOAD_ROWS + "건입니다.");
                }

                result.add(validateRow(row, columnIndex, categories, locations, users));
            }
        } catch (InvalidParamException e) {
            throw e;
        } catch (Exception e) {
            log.error("엑셀 파싱 실패", e);
            throw new InvalidParamException("엑셀 파일을 읽을 수 없습니다. 양식을 확인해주세요.");
        }

        return result;
    }

    /**
     * 확정. 이 메서드 자체는 트랜잭션을 시작하지 않는다 - 행별 실제 처리(자산 저장 + 행 완료 기록)는
     * ExcelCommitRowProcessor/ExcelCommitRowExecutor가 행마다 독립된 REQUIRES_NEW 트랜잭션으로
     * 수행하므로, 한 행의 실패가 다른 행의 커밋을 막지 않는다("행별로 독립 처리, 실패한 행만
     * failedRows로 보고"라는 응답 구조 그대로).
     * <p>
     * 요청 레벨 멱등성: commitId + 요청 내용 지문(requestHash)으로 같은 요청의 중복 실행을 막는다.
     * 같은 commitId라도 내용이 다르면 거부하고(REQUEST_MISMATCH), 이미 끝난 요청이면 그 결과를
     * 그대로 재사용한다(ALREADY_COMPLETED). 행별 멱등성은 ExcelCommitRowProcessor가 commitId+행별
     * rowKey로 별도 보장하므로, 재시도 시 이미 반영된 행은 다시 등록·수정하지 않는다.
     */
    public TangibleAssetExcelCommitResult commit(UUID commitId, List<TangibleAssetExcelRow> rows) {
        validateCommitRows(rows);

        String requestHash = computeRequestHash(rows);
        UUID requestedBy = loginUserContext.getUserId();

        ExcelCommitClaim claim = idempotencyStore.claim(commitId, requestedBy, requestHash);
        switch (claim.getOutcome()) {
            case ALREADY_COMPLETED:
                return claim.getCompletedResult();
            case IN_PROGRESS_ELSEWHERE:
                throw new InvalidParamException("동일한 요청이 이미 처리 중입니다. 잠시 후 다시 확인해주세요.");
            case REQUEST_MISMATCH:
                throw new InvalidParamException("이 확정 번호는 이미 다른 내용의 요청에 사용되었습니다. 새로 미리보기 후 다시 시도해주세요.");
            case CLAIMED:
            default:
                break;
        }

        int created = 0;
        int updated = 0;
        int skipped = 0;
        List<TangibleAssetExcelRow> failed = new ArrayList<>();

        // 같은 업로드 건에서 나온 변경들을 히스토리에서 한 번에 찾을 수 있도록 배치 ID를 공유한다 (§2 배치 추적)
        UUID batchId = UUID.randomUUID();

        try {
            for (TangibleAssetExcelRow row : rows) {
                if (!"CREATE".equals(row.getAction()) && !"UPDATE".equals(row.getAction())) {
                    skipped++;
                    continue;
                }

                ExcelCommitRowResult rowResult = excelCommitRowProcessor.processRow(commitId, row, batchId);
                if (rowResult.isSuccess()) {
                    if ("CREATED".equals(rowResult.getResultAction())) {
                        created++;
                    } else {
                        updated++;
                    }
                } else {
                    row.setAction("ERROR");
                    row.setErrorMessage(rowResult.getErrorMessage());
                    failed.add(row);
                }
            }
        } catch (Exception e) {
            // 행별 실패는 위 루프 안에서 이미 개별적으로 처리·기록됐다 - 여기 도달하는 예외는 그 루프
            // 자체가 깨진, 요청 레벨의 예상 못한 실패다. 무조건 COMPLETED로 덮어쓰지 않고 FAILED로
            // 남겨, 다음 재시도가 안전하게 재claim(lease 만료/FAILED 경로)할 수 있게 한다.
            log.error("엑셀 확정 요청 처리 중 예상하지 못한 오류 (commitId={})", commitId, e);
            idempotencyStore.fail(commitId, e.getMessage());
            throw e;
        }

        TangibleAssetExcelCommitResult result = TangibleAssetExcelCommitResult.builder()
                .createdCount(created)
                .updatedCount(updated)
                .skippedCount(skipped)
                .failedRows(failed)
                .build();
        idempotencyStore.complete(commitId, result);
        return result;
    }

    private void validateCommitRows(List<TangibleAssetExcelRow> rows) {
        if (rows == null || rows.isEmpty()) {
            throw new InvalidParamException("확정할 행이 없습니다.");
        }
        if (rows.stream().anyMatch(Objects::isNull)) {
            throw new InvalidParamException("잘못된 요청입니다. 목록에 빈 행이 포함되어 있습니다.");
        }
        Set<Integer> rowNums = new HashSet<>();
        for (TangibleAssetExcelRow row : rows) {
            if (!rowNums.add(row.getRowNum())) {
                throw new InvalidParamException("요청에 중복된 행 번호(" + row.getRowNum() + ")가 있습니다. 다시 미리보기해주세요.");
            }
        }
        if (rows.size() > MAX_UPLOAD_ROWS) {
            throw new InvalidParamException("한 번에 확정할 수 있는 행은 최대 " + MAX_UPLOAD_ROWS + "건입니다.");
        }
    }

    /** 요청 내용(행 목록)의 지문 - 같은 commitId라도 이 값이 다르면 다른 요청으로 간주한다. 각 행의
     * signature는 이미 preview 시점 전체 내용을 반영해 서버 키로 계산돼 있으므로, rowNum과 signature만
     * 모아 해시해도 요청 전체 내용을 충분히 대표한다(위변조 방지 자체는 signature 검증이 담당하고,
     * 이 해시는 순전히 "같은 commitId 재사용 시 내용 일치 여부"를 가리기 위한 용도다). */
    /** package-private: 같은 패키지의 통합 테스트가 "이전 시도가 쓴 것과 같은 requestHash"를
     * 재현해 크래시 후 재시도 시나리오를 준비할 수 있도록 접근을 허용한다(공개 API는 아니다). */
    String computeRequestHash(List<TangibleAssetExcelRow> rows) {
        String canonical = rows.stream()
                .sorted(Comparator.comparingInt(TangibleAssetExcelRow::getRowNum))
                .map(r -> r.getRowNum() + ":" + (r.getSignature() == null ? "" : r.getSignature()))
                .collect(Collectors.joining("|"));
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(canonical.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("요청 해시 계산 중 오류가 발생했습니다.", e);
        }
    }

    /** 다운로드 양식은 필수 항목 헤더에 '*'를 붙이므로, 매칭 시에는 이를 무시한다 */
    private String normalizeHeader(String header) {
        return header == null ? "" : header.trim().replaceAll("\\*+$", "").trim();
    }

    private int headerRowNum(Sheet sheet) {
        for (Row row : sheet) {
            if (findColumnInRow(row, "자산명") != null) {
                return row.getRowNum();
            }
        }
        return 0;
    }

    private Map<String, Integer> resolveHeaderColumns(Sheet sheet) {
        for (Row row : sheet) {
            Integer assetNameCol = findColumnInRow(row, "자산명");
            if (assetNameCol == null) {
                continue;
            }

            Map<String, Integer> columnIndex = new HashMap<>();
            for (Cell cell : row) {
                String header = normalizeHeader(getCellString(cell));
                if (!header.isEmpty()) {
                    columnIndex.put(header, cell.getColumnIndex());
                }
            }
            return columnIndex;
        }
        throw new com.winitech.common.exception.InvalidParamException("엑셀 양식의 헤더 행을 찾을 수 없습니다. ('자산명' 컬럼 필요)");
    }

    private Integer findColumnInRow(Row row, String headerText) {
        for (Cell cell : row) {
            if (headerText.equals(normalizeHeader(getCellString(cell)))) {
                return cell.getColumnIndex();
            }
        }
        return null;
    }

    /** 수식 셀은 캐시된 값을 그대로 신뢰하지 않고 행 자체를 거부한다 (업로드 시 수식 셀 제한) */
    private boolean containsFormulaCell(Row row) {
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() == CellType.FORMULA) {
                return true;
            }
        }
        return false;
    }

    private boolean isBlankRow(Row row) {
        for (Cell cell : row) {
            if (!getCellString(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /** 양식에 미리 채워둔 입력 예시 행("자산명"에 EXAMPLE_ROW_MARKER 포함)은 실제 데이터로 취급하지 않는다 */
    private boolean isExampleRow(Row row, Map<String, Integer> columnIndex) {
        String assetName = readCell(row, columnIndex, "자산명");
        return assetName != null && assetName.trim().startsWith(EXAMPLE_ROW_MARKER);
    }

    private TangibleAssetExcelRow validateRow(
            Row row,
            Map<String, Integer> columnIndex,
            List<AssetCategory> categories,
            List<AssetLocation> locations,
            List<CommonUser> users
    ) {
        int rowNum = row.getRowNum() + 1; // 엑셀 표시 행 번호(1-base)

        if (containsFormulaCell(row)) {
            return errorRow(rowNum, readCell(row, columnIndex, "자산코드"), "수식이 포함된 셀은 지원하지 않습니다. 값만 입력해주세요.");
        }

        String assetCode = readCell(row, columnIndex, "자산코드");

        TangibleAsset existing = null;
        boolean isUpdate = assetCode != null && !assetCode.isBlank();
        if (isUpdate) {
            existing = tangibleAssetReader.findByAssetCode(assetCode.trim()).orElse(null);
            if (existing == null) {
                return errorRow(rowNum, assetCode, "존재하지 않는 자산코드입니다.");
            }
        }

        String assetName = resolveField(readCell(row, columnIndex, "자산명"), existing == null ? null : existing.getAssetName());
        if (isBlank(assetName)) {
            return errorRow(rowNum, assetCode, "자산명은 필수입니다.");
        }

        String categoryName = resolveField(readCell(row, columnIndex, "자산종류"),
                existing == null ? null : existing.getCategory().getCategoryName());
        if (isBlank(categoryName)) {
            return errorRow(rowNum, assetCode, "자산종류는 필수입니다.");
        }
        AssetCategory category = categories.stream()
                .filter(c -> c.getCategoryName().equals(categoryName.trim()))
                .findFirst()
                .orElse(null);
        if (category == null) {
            return errorRow(rowNum, assetCode, "등록되지 않은 종류입니다: " + categoryName);
        }

        String locationName = resolveField(readCell(row, columnIndex, "자산위치"),
                existing == null ? null : existing.getLocation().getLocationName());
        if (isBlank(locationName)) {
            return errorRow(rowNum, assetCode, "자산위치는 필수입니다.");
        }
        AssetLocation location = locations.stream()
                .filter(l -> l.getLocationName().equals(locationName.trim()))
                .findFirst()
                .orElse(null);
        if (location == null) {
            return errorRow(rowNum, assetCode, "등록되지 않은 위치입니다: " + locationName);
        }

        String acquisitionDateRaw = resolveField(readCell(row, columnIndex, "취득일"),
                existing == null ? null : existing.getAcquisitionDate().toString());
        LocalDate acquisitionDate;
        try {
            acquisitionDate = LocalDate.parse(acquisitionDateRaw.trim(), DATE_FORMAT);
        } catch (Exception e) {
            return errorRow(rowNum, assetCode, "취득일 형식이 올바르지 않습니다. (예: 2026-09-01)");
        }

        String acquisitionAmountRaw = resolveField(readCell(row, columnIndex, "취득가액"),
                existing == null ? null : existing.getAcquisitionAmount().toString());
        BigDecimal acquisitionAmount;
        try {
            acquisitionAmount = new BigDecimal(acquisitionAmountRaw.replaceAll(",", "").trim());
        } catch (Exception e) {
            return errorRow(rowNum, assetCode, "취득가액은 숫자여야 합니다.");
        }

        String lifeStatusLabel = resolveField(readCell(row, columnIndex, "생애상태"),
                existing == null ? TangibleAsset.LifeStatus.USE.getDescription() : existing.getLifeStatus().getDescription());
        TangibleAsset.LifeStatus lifeStatus = findLifeStatusByLabel(lifeStatusLabel);
        if (lifeStatus == null) {
            return errorRow(rowNum, assetCode, "알 수 없는 생애상태입니다: " + lifeStatusLabel);
        }

        String assignTypeLabel = resolveField(readCell(row, columnIndex, "배정형태"),
                existing == null ? TangibleAsset.AssignType.UNASSIGNED.getDescription() : existing.getAssignType().getDescription());
        TangibleAsset.AssignType assignType = findAssignTypeByLabel(assignTypeLabel);
        if (assignType == null) {
            return errorRow(rowNum, assetCode, "알 수 없는 배정형태입니다: " + assignTypeLabel);
        }

        if (!TangibleAsset.isValidCombination(lifeStatus, assignType)) {
            return errorRow(rowNum, assetCode,
                    "생애상태 '" + lifeStatus.getDescription() + "'에서는 배정형태 '" + assignType.getDescription() + "'를 선택할 수 없습니다.");
        }

        String memberInput = readCell(row, columnIndex, "사용자사번");
        String memberNameInput = readCell(row, columnIndex, "사용자명");
        UUID currentMemberId = existing == null ? null : existing.getCurrentMemberId();

        if (TangibleAsset.requiresMember(assignType)) {
            CommonUser matched = matchUser(memberInput, memberNameInput, users);
            if (matched == null) {
                if (currentMemberId == null) {
                    return errorRow(rowNum, assetCode, "배정 형태가 '" + assignType.getDescription() + "'이면 배정 사용자(사번 또는 이름)가 필요합니다.");
                }
                // 사용자 입력이 없고 기존 배정을 유지하는 경우
            } else {
                currentMemberId = matched.getId();
            }
        } else {
            currentMemberId = null;
        }

        String modelName = resolveField(readCell(row, columnIndex, "모델명"), existing == null ? null : existing.getModelName());
        String manufacturer = resolveField(readCell(row, columnIndex, "제조사"), existing == null ? null : existing.getManufacturer());
        String serialNo = resolveField(readCell(row, columnIndex, "시리얼번호"), existing == null ? null : existing.getSerialNo());
        String memo = resolveField(readCell(row, columnIndex, "메모"), existing == null ? null : existing.getMemo());

        TangibleAssetExcelRow row0 = TangibleAssetExcelRow.builder()
                .rowNum(rowNum)
                .action(isUpdate ? "UPDATE" : "CREATE")
                .tangibleAssetId(existing == null ? null : existing.getId())
                .assetCode(assetCode)
                .assetName(assetName.trim())
                .categoryName(category.getCategoryName())
                .categoryId(category.getId())
                .locationName(location.getLocationName())
                .locationId(location.getId())
                .lifeStatus(lifeStatus.name())
                .assignType(assignType.name())
                .memberInput(!isBlank(memberInput) ? memberInput : memberNameInput)
                .currentMemberId(currentMemberId)
                .acquisitionDate(acquisitionDate.toString())
                .acquisitionAmount(acquisitionAmount.toPlainString())
                .modelName(nullToEmpty(modelName))
                .manufacturer(nullToEmpty(manufacturer))
                .serialNo(nullToEmpty(serialNo))
                .memo(nullToEmpty(memo))
                .entityVersion(existing == null ? null : existing.getVersion())
                .build();

        // preview 시점 값 그대로 서명한다 - commit에서 클라이언트가 돌려준 행을 재서명해 비교하면
        // 위 필드 중 하나라도 바뀌었는지(위변조) 또는 서명 자체가 없는지(조작된 새 행 끼워넣기)를
        // 감지할 수 있다.
        row0.setSignature(excelRowSigner.sign(row0));
        return row0;
    }

    private CommonUser matchUser(String employeeNo, String fullName, List<CommonUser> users) {
        if (!isBlank(employeeNo)) {
            Optional<CommonUser> byEmployeeNo = users.stream()
                    .filter(u -> employeeNo.trim().equals(u.getEmployeeNo()))
                    .findFirst();
            if (byEmployeeNo.isPresent()) {
                return byEmployeeNo.get();
            }
        }
        if (!isBlank(fullName)) {
            return users.stream()
                    .filter(u -> fullName.trim().equals(u.getFullName()))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private TangibleAsset.LifeStatus findLifeStatusByLabel(String label) {
        if (label == null) return null;
        String trimmed = label.trim();
        for (TangibleAsset.LifeStatus value : TangibleAsset.LifeStatus.values()) {
            if (value.name().equals(trimmed) || value.getDescription().equals(trimmed)) {
                return value;
            }
        }
        return null;
    }

    private TangibleAsset.AssignType findAssignTypeByLabel(String label) {
        if (label == null) return null;
        String trimmed = label.trim();
        for (TangibleAsset.AssignType value : TangibleAsset.AssignType.values()) {
            if (value.name().equals(trimmed) || value.getDescription().equals(trimmed)) {
                return value;
            }
        }
        return null;
    }

    private String resolveField(String excelValue, String existingValue) {
        return isBlank(excelValue) ? existingValue : excelValue;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String readCell(Row row, Map<String, Integer> columnIndex, String header) {
        Integer idx = columnIndex.get(header);
        if (idx == null) {
            return "";
        }
        Cell cell = row.getCell(idx);
        return getCellString(cell);
    }

    private String getCellString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                double numeric = cell.getNumericCellValue();
                if (numeric == Math.floor(numeric) && !Double.isInfinite(numeric)) {
                    return String.valueOf((long) numeric);
                }
                return String.valueOf(numeric);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }

    private TangibleAssetExcelRow errorRow(int rowNum, String assetCode, String message) {
        return TangibleAssetExcelRow.builder()
                .rowNum(rowNum)
                .action("ERROR")
                .assetCode(assetCode)
                .errorMessage(message)
                .build();
    }
}
