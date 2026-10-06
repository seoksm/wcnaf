package com.winitech.smartAsset.application.tangibleAsset;

import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.assetHistory.AssetHistoryInfo;
import com.winitech.smartAsset.domain.assetLocation.AssetLocation;
import com.winitech.smartAsset.domain.disposalAsset.DisposalAsset;
import com.winitech.smartAsset.domain.disposalAsset.DisposalAssetRowInfo;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetCommand;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetDisposalCommand;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetInfo;
import com.winitech.smartAsset.infrastructure.assetCategory.AssetCategoryRepository;
import com.winitech.smartAsset.infrastructure.assetLocation.AssetLocationRepository;
import com.winitech.smartAsset.infrastructure.disposalAsset.DisposalAssetRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * S-240~242: 불용/복귀/처분 처리 - facade → service → 실제 DB 전 구간을 검증한다.
 * D4(동결 규칙) 계산 자체는 DepreciationCalculatorTest(순수 단위 테스트)가 이미 다루므로, 여기서는
 * "실제 DB에 저장된 lifeStatusChangedAt을 그대로 읽어와도 동결이 똑같이 적용되는지"와 "불용→처분
 * 전체 흐름이 배정 해제·이력·disposal_asset 저장까지 일관되게 이어지는지"에 집중한다.
 * <p>
 * TangibleAssetServiceImpl은 LoginUserContext(@RequestScope 빈)를 사용하므로, 이 테스트에서는
 * 매 테스트마다 가짜 요청 컨텍스트를 스레드에 바인딩하고 userId를 채워준다(TangibleAssetAssignmentIntegrationTest
 * 참고) - disposal_asset.disposedBy는 @NonNull이라 여기서는 값을 반드시 채워야 한다.
 */
@SpringBootTest
class TangibleAssetDisposalIntegrationTest {

    @Autowired
    private TangibleAssetFacade tangibleAssetFacade;
    @Autowired
    private AssetCategoryRepository assetCategoryRepository;
    @Autowired
    private AssetLocationRepository assetLocationRepository;
    @Autowired
    private DisposalAssetRepository disposalAssetRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private LoginUserContext loginUserContext;

    private AssetCategory testCategory;
    private AssetLocation testLocation;
    private final List<UUID> createdAssetIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        loginUserContext.setUserId(UUID.randomUUID());
        testCategory = assetCategoryRepository.save(AssetCategory.builder()
                .categoryCode("DISPOSAL-IT-CAT").categoryName("처분통합테스트카테고리").usefulLifeMonths(60).build());
        testLocation = assetLocationRepository.save(AssetLocation.builder().locationName("처분통합테스트위치").build());
        createdAssetIds.clear();
    }

    @AfterEach
    void cleanUp() {
        for (UUID assetId : createdAssetIds) {
            jdbcTemplate.update("DELETE FROM disposal_asset WHERE tangible_asset_id = ?", assetId);
            jdbcTemplate.update("DELETE FROM asset_history WHERE tangible_asset_id = ?", assetId);
            jdbcTemplate.update("DELETE FROM asset_assignment WHERE tangible_asset_id = ?", assetId);
            jdbcTemplate.update("DELETE FROM tangible_asset WHERE tangible_asset_id = ?", assetId);
        }
        jdbcTemplate.update("DELETE FROM asset_category WHERE asset_category_id = ?", testCategory.getId());
        jdbcTemplate.update("DELETE FROM asset_location WHERE asset_location_id = ?", testLocation.getId());
        RequestContextHolder.resetRequestAttributes();
    }

    private UUID createAsset(TangibleAsset.AssignType assignType, UUID memberId, BigDecimal acquisitionAmount, LocalDate acquisitionDate) {
        TangibleAssetCommand command = TangibleAssetCommand.builder()
                .assetName("처분통합테스트자산")
                .categoryId(testCategory.getId())
                .locationId(testLocation.getId())
                .lifeStatus(TangibleAsset.LifeStatus.USE)
                .assignType(assignType)
                .acquisitionDate(acquisitionDate)
                .acquisitionAmount(acquisitionAmount)
                .currentMemberId(memberId)
                .build();
        UUID assetId = tangibleAssetFacade.postTangibleAsset(command);
        createdAssetIds.add(assetId);
        return assetId;
    }

    /** 실제로는 방금 불용 처리했더라도, DB의 동결 시점을 직접 과거로 되돌려 "예전부터 불용이었던" 상황을 재현한다 */
    private void pushLifeStatusChangedAtBack(UUID assetId, int monthsAgo) {
        OffsetDateTime frozenAt = OffsetDateTime.now().minusMonths(monthsAgo);
        jdbcTemplate.update("UPDATE tangible_asset SET life_status_changed_at = ? WHERE tangible_asset_id = ?",
                Timestamp.valueOf(frozenAt.toLocalDateTime()), assetId);
    }

    @Test
    void 불용_처분_흐름은_배정해제_이력_disposal자산_생성까지_일관되게_이어진다() {
        UUID memberId = UUID.randomUUID();
        UUID assetId = createAsset(TangibleAsset.AssignType.PERSONAL, memberId, new BigDecimal("1000000"), LocalDate.now().minusMonths(3));

        tangibleAssetFacade.disuseTangibleAsset(assetId, "부서 통폐합으로 인한 유휴 장비");

        TangibleAssetInfo afterDisuse = tangibleAssetFacade.getTangibleAsset(assetId);
        assertThat(afterDisuse.getLifeStatus()).isEqualTo(TangibleAsset.LifeStatus.DISUSE);
        assertThat(afterDisuse.getAssignType()).isEqualTo(TangibleAsset.AssignType.UNASSIGNED);
        assertThat(afterDisuse.getCurrentMemberId()).isNull();
        assertThat(tangibleAssetFacade.getAssignmentHistory(assetId)).allMatch(a -> a.getReleasedAt() != null);

        TangibleAssetDisposalCommand disposeCommand = TangibleAssetDisposalCommand.builder()
                .disposalReasonCode(DisposalAsset.DisposalReason.SALE)
                .disposalAmount(new BigDecimal("200000"))
                .counterparty("중고나라")
                .build();
        tangibleAssetFacade.disposeTangibleAsset(assetId, disposeCommand);

        TangibleAssetInfo afterDispose = tangibleAssetFacade.getTangibleAsset(assetId);
        assertThat(afterDispose.getLifeStatus()).isEqualTo(TangibleAsset.LifeStatus.DISPOSED);

        DisposalAsset saved = disposalAssetRepository.findByTangibleAssetId(assetId).orElseThrow();
        assertThat(saved.getDisposalReasonCode()).isEqualTo(DisposalAsset.DisposalReason.SALE);
        assertThat(saved.getCounterparty()).isEqualTo("중고나라");
        assertThat(saved.disposalGainLoss()).isEqualByComparingTo(
                new BigDecimal("200000").subtract(saved.getBookValueAtDisposal()));

        List<String> historyTypes = tangibleAssetFacade.getHistory(assetId).stream()
                .map(AssetHistoryInfo::getHistoryType).toList();
        assertThat(historyTypes).contains("DISUSE", "DISPOSAL");
    }

    @Test
    void 불용에서_사용으로_복귀하면_다시_정상_자산으로_돌아온다() {
        UUID assetId = createAsset(TangibleAsset.AssignType.UNASSIGNED, null, new BigDecimal("1000000"), LocalDate.now().minusMonths(3));
        tangibleAssetFacade.disuseTangibleAsset(assetId, null);

        tangibleAssetFacade.restoreTangibleAsset(assetId);

        TangibleAssetInfo info = tangibleAssetFacade.getTangibleAsset(assetId);
        assertThat(info.getLifeStatus()).isEqualTo(TangibleAsset.LifeStatus.USE);
    }

    @Test
    void 처분완료_자산은_일반_수정_경로로는_다시_바꿀_수_없다() {
        UUID assetId = createAsset(TangibleAsset.AssignType.UNASSIGNED, null, new BigDecimal("1000000"), LocalDate.now().minusMonths(3));
        tangibleAssetFacade.disuseTangibleAsset(assetId, null);
        tangibleAssetFacade.disposeTangibleAsset(assetId, TangibleAssetDisposalCommand.builder()
                .disposalReasonCode(DisposalAsset.DisposalReason.SCRAP).build());

        TangibleAssetCommand.UpdateCommand updateCommand = TangibleAssetCommand.UpdateCommand.builder()
                .tangibleAssetId(assetId)
                .assetName("수정시도")
                .categoryId(testCategory.getId())
                .locationId(testLocation.getId())
                .lifeStatus(TangibleAsset.LifeStatus.DISPOSED)
                .assignType(TangibleAsset.AssignType.UNASSIGNED)
                .acquisitionDate(LocalDate.now().minusMonths(3))
                .acquisitionAmount(new BigDecimal("1000000"))
                .build();

        assertThatThrownBy(() -> tangibleAssetFacade.reviseTangibleAsset(updateCommand))
                .isInstanceOf(InvalidParamException.class);
    }

    @Test
    void 동결_시점_이전에_불용된_자산은_그_시점까지만_상각된_장부가로_처분된다() {
        // 취득 40개월 전, 종류의 내용연수는 60개월, 취득가 6,001,000원(비망가액 기본값 1,000원을 뺀
        // 6,000,000원 ÷ 60개월 = 매월 100,000원 - 나눗셈이 딱 떨어지도록 골랐다). 10개월 전에 이미
        // 불용됐던 것으로 동결 시점을 되돌리면, 취득월을 포함해 31개월치(취득~동결 시점)만 상각되어야
        // 하므로 누적상각액은 정확히 3,100,000원, 장부가는 6,001,000 - 3,100,000 = 2,901,000원이다.
        UUID assetId = createAsset(TangibleAsset.AssignType.UNASSIGNED, null,
                new BigDecimal("6001000"), LocalDate.now().minusMonths(40));
        tangibleAssetFacade.disuseTangibleAsset(assetId, null);
        pushLifeStatusChangedAtBack(assetId, 10);

        tangibleAssetFacade.disposeTangibleAsset(assetId,
                TangibleAssetDisposalCommand.builder().disposalReasonCode(DisposalAsset.DisposalReason.SCRAP).build());

        DisposalAsset saved = disposalAssetRepository.findByTangibleAssetId(assetId).orElseThrow();
        assertThat(saved.getBookValueAtDisposal()).isEqualByComparingTo(new BigDecimal("2901000"));
    }

    @Test
    void loadDisposalList은_생애상태로_필터링해_불용_처분완료_자산만_돌려준다() {
        UUID useAssetId = createAsset(TangibleAsset.AssignType.UNASSIGNED, null, new BigDecimal("1000000"), LocalDate.now().minusMonths(3));
        UUID disuseAssetId = createAsset(TangibleAsset.AssignType.UNASSIGNED, null, new BigDecimal("1000000"), LocalDate.now().minusMonths(3));
        UUID disposedAssetId = createAsset(TangibleAsset.AssignType.UNASSIGNED, null, new BigDecimal("1000000"), LocalDate.now().minusMonths(3));
        tangibleAssetFacade.disuseTangibleAsset(disuseAssetId, null);
        tangibleAssetFacade.disuseTangibleAsset(disposedAssetId, null);
        tangibleAssetFacade.disposeTangibleAsset(disposedAssetId,
                TangibleAssetDisposalCommand.builder().disposalReasonCode(DisposalAsset.DisposalReason.SCRAP).build());

        Page<DisposalAssetRowInfo> all = tangibleAssetFacade.getDisposalList(null, 0, 50, null);
        List<UUID> allIds = all.getContent().stream().map(DisposalAssetRowInfo::getTangibleAssetId).toList();
        assertThat(allIds).contains(disuseAssetId, disposedAssetId).doesNotContain(useAssetId);

        Page<DisposalAssetRowInfo> disposedOnly = tangibleAssetFacade.getDisposalList("DISPOSED", 0, 50, null);
        List<UUID> disposedIds = disposedOnly.getContent().stream().map(DisposalAssetRowInfo::getTangibleAssetId).toList();
        assertThat(disposedIds).contains(disposedAssetId).doesNotContain(disuseAssetId, useAssetId);
    }
}
