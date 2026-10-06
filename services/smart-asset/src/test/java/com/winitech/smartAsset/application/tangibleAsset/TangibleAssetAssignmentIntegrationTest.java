package com.winitech.smartAsset.application.tangibleAsset;

import com.winitech.smartAsset.domain.assetAssignment.AssetAssignmentInfo;
import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.assetLocation.AssetLocation;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetCommand;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAssetInfo;
import com.winitech.smartAsset.infrastructure.assetCategory.AssetCategoryRepository;
import com.winitech.smartAsset.infrastructure.assetLocation.AssetLocationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Q-16/D8: 자산 배정 변경/회수가 실제 DB(유니크 인덱스 + TangibleAsset.version)를 통해서도
 * 정상적인 순차 흐름에서는 아무 문제 없이 동작해야 한다는 것을 검증한다. 동시성 자체는
 * TangibleAssetOptimisticLockingTest에서 별도로 다루고, 여기서는 facade → service → 실제 DB
 * 전 구간을 통해 배정 관련 업무 규칙(회수·전환·불용 처리·currentMemberId 일치)을 확인한다.
 * <p>
 * TangibleAssetServiceImpl은 LoginUserContext(@RequestScope 빈)를 사용하므로, 이 테스트에서는
 * 매 테스트마다 가짜 요청 컨텍스트를 스레드에 바인딩해야 한다.
 */
@SpringBootTest
class TangibleAssetAssignmentIntegrationTest {

    @Autowired
    private TangibleAssetFacade tangibleAssetFacade;

    @Autowired
    private AssetCategoryRepository assetCategoryRepository;

    @Autowired
    private AssetLocationRepository assetLocationRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private AssetCategory testCategory;
    private AssetLocation testLocation;
    private final List<UUID> createdAssetIds = new java.util.ArrayList<>();

    @BeforeEach
    void setUp() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        testCategory = assetCategoryRepository.save(AssetCategory.builder()
                .categoryCode("ASGN-IT-CAT").categoryName("배정통합테스트카테고리").usefulLifeMonths(36).build());
        testLocation = assetLocationRepository.save(AssetLocation.builder().locationName("배정통합테스트위치").build());
        createdAssetIds.clear();
    }

    @AfterEach
    void cleanUp() {
        // assetCode는 서버가 실제 채번 규칙(AssetCodeGenerator)으로 자동 발급하므로 접두사로
        // 골라낼 수 없다 - 생성 시점에 기록해 둔 ID로 직접 정리한다.
        for (UUID assetId : createdAssetIds) {
            jdbcTemplate.update("DELETE FROM asset_history WHERE tangible_asset_id = ?", assetId);
            jdbcTemplate.update("DELETE FROM asset_assignment WHERE tangible_asset_id = ?", assetId);
            jdbcTemplate.update("DELETE FROM tangible_asset WHERE tangible_asset_id = ?", assetId);
        }
        jdbcTemplate.update("DELETE FROM asset_category WHERE asset_category_id = ?", testCategory.getId());
        jdbcTemplate.update("DELETE FROM asset_location WHERE asset_location_id = ?", testLocation.getId());
        RequestContextHolder.resetRequestAttributes();
    }

    private UUID createAsset(TangibleAsset.AssignType assignType, UUID memberId) {
        TangibleAssetCommand command = TangibleAssetCommand.builder()
                .assetName("배정통합테스트자산")
                .categoryId(testCategory.getId())
                .locationId(testLocation.getId())
                .lifeStatus(TangibleAsset.LifeStatus.USE)
                .assignType(assignType)
                .acquisitionDate(LocalDate.of(2024, 1, 1))
                .acquisitionAmount(BigDecimal.valueOf(1_000_000))
                .currentMemberId(memberId)
                .build();
        UUID assetId = tangibleAssetFacade.postTangibleAsset(command);
        createdAssetIds.add(assetId);
        return assetId;
    }

    private TangibleAssetCommand.UpdateCommand updateCommandFor(UUID assetId, TangibleAsset.AssignType assignType,
                                                                 UUID memberId, TangibleAsset.LifeStatus lifeStatus) {
        return TangibleAssetCommand.UpdateCommand.builder()
                .tangibleAssetId(assetId)
                .assetName("배정통합테스트자산")
                .categoryId(testCategory.getId())
                .locationId(testLocation.getId())
                .lifeStatus(lifeStatus)
                .assignType(assignType)
                .acquisitionDate(LocalDate.of(2024, 1, 1))
                .acquisitionAmount(BigDecimal.valueOf(1_000_000))
                .currentMemberId(memberId)
                .build();
    }

    private List<AssetAssignmentInfo> activeOnly(List<AssetAssignmentInfo> history) {
        return history.stream().filter(a -> a.getReleasedAt() == null).collect(java.util.stream.Collectors.toList());
    }

    @Test
    void 한_자산에_활성_배정_1건_생성에_성공한다() {
        UUID memberId = UUID.randomUUID();
        UUID assetId = createAsset(TangibleAsset.AssignType.PERSONAL, memberId);

        List<AssetAssignmentInfo> history = tangibleAssetFacade.getAssignmentHistory(assetId);
        assertThat(activeOnly(history)).hasSize(1);
        assertThat(activeOnly(history).get(0).getMemberId()).isEqualTo(memberId);

        TangibleAssetInfo info = tangibleAssetFacade.getTangibleAsset(assetId);
        assertThat(info.getCurrentMemberId()).isEqualTo(memberId);
    }

    @Test
    void 사용자_A에서_B로_변경시_A_이력_종료_후_B_이력이_생성된다() {
        UUID memberA = UUID.randomUUID();
        UUID memberB = UUID.randomUUID();
        UUID assetId = createAsset(TangibleAsset.AssignType.PERSONAL, memberA);

        tangibleAssetFacade.reviseTangibleAsset(
                updateCommandFor(assetId, TangibleAsset.AssignType.PERSONAL, memberB, TangibleAsset.LifeStatus.USE));

        List<AssetAssignmentInfo> history = tangibleAssetFacade.getAssignmentHistory(assetId);
        assertThat(activeOnly(history)).hasSize(1);
        assertThat(activeOnly(history).get(0).getMemberId()).isEqualTo(memberB);

        List<AssetAssignmentInfo> released = history.stream()
                .filter(a -> a.getReleasedAt() != null).collect(java.util.stream.Collectors.toList());
        assertThat(released).hasSize(1);
        assertThat(released.get(0).getMemberId()).isEqualTo(memberA);

        TangibleAssetInfo info = tangibleAssetFacade.getTangibleAsset(assetId);
        assertThat(info.getCurrentMemberId()).isEqualTo(memberB);
    }

    @Test
    void 명시적_회수_후_활성_배정이_0건이_된다() {
        UUID memberId = UUID.randomUUID();
        UUID assetId = createAsset(TangibleAsset.AssignType.PERSONAL, memberId);

        tangibleAssetFacade.releaseAssignment(assetId);

        List<AssetAssignmentInfo> history = tangibleAssetFacade.getAssignmentHistory(assetId);
        assertThat(activeOnly(history)).isEmpty();

        TangibleAssetInfo info = tangibleAssetFacade.getTangibleAsset(assetId);
        assertThat(info.getCurrentMemberId()).isNull();
        assertThat(info.getAssignType()).isEqualTo(TangibleAsset.AssignType.UNASSIGNED);
    }

    @Test
    void 불용_전환시_활성_배정이_종료된다() {
        UUID memberId = UUID.randomUUID();
        UUID assetId = createAsset(TangibleAsset.AssignType.PERSONAL, memberId);

        tangibleAssetFacade.reviseTangibleAsset(
                updateCommandFor(assetId, TangibleAsset.AssignType.PERSONAL, memberId, TangibleAsset.LifeStatus.DISUSE));

        List<AssetAssignmentInfo> history = tangibleAssetFacade.getAssignmentHistory(assetId);
        assertThat(activeOnly(history)).isEmpty();

        TangibleAssetInfo info = tangibleAssetFacade.getTangibleAsset(assetId);
        assertThat(info.getLifeStatus()).isEqualTo(TangibleAsset.LifeStatus.DISUSE);
        assertThat(info.getAssignType()).isEqualTo(TangibleAsset.AssignType.UNASSIGNED);
        assertThat(info.getCurrentMemberId()).isNull();
    }

    @Test
    void currentMemberId와_활성_배정_memberId가_항상_일치한다() {
        UUID memberId = UUID.randomUUID();
        UUID assetId = createAsset(TangibleAsset.AssignType.PERSONAL, memberId);

        TangibleAssetInfo info = tangibleAssetFacade.getTangibleAsset(assetId);
        List<AssetAssignmentInfo> active = activeOnly(tangibleAssetFacade.getAssignmentHistory(assetId));

        assertThat(active).hasSize(1);
        assertThat(info.getCurrentMemberId()).isEqualTo(active.get(0).getMemberId());
    }

    @Test
    void 여러_자산의_배정_변경은_서로_차단하지_않는다() {
        UUID member1 = UUID.randomUUID();
        UUID member2 = UUID.randomUUID();
        UUID asset1 = createAsset(TangibleAsset.AssignType.PERSONAL, member1);
        UUID asset2 = createAsset(TangibleAsset.AssignType.PERSONAL, member2);

        UUID newMember1 = UUID.randomUUID();
        UUID newMember2 = UUID.randomUUID();
        tangibleAssetFacade.reviseTangibleAsset(
                updateCommandFor(asset1, TangibleAsset.AssignType.PERSONAL, newMember1, TangibleAsset.LifeStatus.USE));
        tangibleAssetFacade.reviseTangibleAsset(
                updateCommandFor(asset2, TangibleAsset.AssignType.PERSONAL, newMember2, TangibleAsset.LifeStatus.USE));

        assertThat(tangibleAssetFacade.getTangibleAsset(asset1).getCurrentMemberId()).isEqualTo(newMember1);
        assertThat(tangibleAssetFacade.getTangibleAsset(asset2).getCurrentMemberId()).isEqualTo(newMember2);
    }
}
