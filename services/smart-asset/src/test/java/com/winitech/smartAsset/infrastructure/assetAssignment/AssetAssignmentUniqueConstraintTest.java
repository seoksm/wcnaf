package com.winitech.smartAsset.infrastructure.assetAssignment;

import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.assetLocation.AssetLocation;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import com.winitech.smartAsset.infrastructure.assetCategory.AssetCategoryRepository;
import com.winitech.smartAsset.infrastructure.assetLocation.AssetLocationRepository;
import com.winitech.smartAsset.infrastructure.tangibleAsset.TangibleAssetRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * V11__asset_assignment_unique_active.sql 이 세우는 "자산당 활성 배정(released_at IS NULL)은
 * 최대 1건" 제약을 DB 레벨에서 직접 검증한다. 실제 Postgres의 partial unique index 동작에
 * 의존하므로(H2로는 재현되지 않는다) 실제 DB에 붙는 통합 테스트로 작성한다.
 */
@SpringBootTest
class AssetAssignmentUniqueConstraintTest {

    @Autowired
    private TangibleAssetRepository tangibleAssetRepository;

    @Autowired
    private AssetCategoryRepository assetCategoryRepository;

    @Autowired
    private AssetLocationRepository assetLocationRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private AssetCategory testCategory;
    private AssetLocation testLocation;

    @BeforeEach
    void setUp() {
        testCategory = assetCategoryRepository.save(AssetCategory.builder()
                .categoryCode("ASGN-TEST-CAT").categoryName("배정테스트카테고리").usefulLifeMonths(36).build());
        testLocation = assetLocationRepository.save(AssetLocation.builder().locationName("배정테스트위치").build());
    }

    @AfterEach
    void cleanUp() {
        jdbcTemplate.update("DELETE FROM asset_assignment WHERE tangible_asset_id IN " +
                "(SELECT tangible_asset_id FROM tangible_asset WHERE asset_code LIKE 'AST-921%')");
        jdbcTemplate.update("DELETE FROM tangible_asset WHERE asset_code LIKE 'AST-921%'");
        jdbcTemplate.update("DELETE FROM asset_category WHERE asset_category_id = ?", testCategory.getId());
        jdbcTemplate.update("DELETE FROM asset_location WHERE asset_location_id = ?", testLocation.getId());
    }

    private UUID createAsset(String assetCode) {
        TangibleAsset asset = tangibleAssetRepository.save(TangibleAsset.builder()
                .assetCode(assetCode)
                .assetName("배정테스트자산")
                .category(testCategory)
                .location(testLocation)
                .acquisitionDate(LocalDate.of(2024, 1, 1))
                .acquisitionAmount(BigDecimal.valueOf(1_000_000))
                .build());
        return asset.getId();
    }

    private void insertActiveAssignment(UUID assetId, UUID memberId) {
        jdbcTemplate.update(
                "INSERT INTO asset_assignment (asset_assignment_id, create_at, update_at, tangible_asset_id, member_id, " +
                        "assign_type, assigned_at, released_at, assigned_by) " +
                        "VALUES (?, now(), now(), ?, ?, 'PERSONAL', now(), NULL, NULL)",
                UUID.randomUUID(), assetId, memberId);
    }

    @Test
    void 한_자산에_활성_배정_1건_생성은_성공한다() {
        UUID assetId = createAsset("AST-9211-0001");

        insertActiveAssignment(assetId, UUID.randomUUID());

        Integer activeCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM asset_assignment WHERE tangible_asset_id = ? AND released_at IS NULL",
                Integer.class, assetId);
        assertThat(activeCount).isEqualTo(1);
    }

    @Test
    void 같은_자산에_두번째_활성_배정을_직접_INSERT하면_DB_제약으로_실패한다() {
        UUID assetId = createAsset("AST-9211-0002");
        insertActiveAssignment(assetId, UUID.randomUUID());

        assertThatThrownBy(() -> insertActiveAssignment(assetId, UUID.randomUUID()))
                .isInstanceOf(DataIntegrityViolationException.class);

        Integer activeCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM asset_assignment WHERE tangible_asset_id = ? AND released_at IS NULL",
                Integer.class, assetId);
        assertThat(activeCount).isEqualTo(1);
    }

    @Test
    void 여러_자산의_배정은_서로_차단하지_않는다() {
        UUID assetId1 = createAsset("AST-9211-0003");
        UUID assetId2 = createAsset("AST-9211-0004");

        insertActiveAssignment(assetId1, UUID.randomUUID());
        insertActiveAssignment(assetId2, UUID.randomUUID());

        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM asset_assignment WHERE tangible_asset_id IN (?, ?) AND released_at IS NULL",
                Integer.class, assetId1, assetId2);
        assertThat(total).isEqualTo(2);
    }

    /**
     * 이미 유니크 인덱스가 있는 한 중복을 만들 수 없으므로, 마이그레이션 적용 "이전" 상황(인덱스
     * 없음 + 중복 데이터 존재)을 일시적으로 재현한다. V11의 중복 검사 블록(파일 앞부분의 DO $$ ...
     * END $$;)을 그대로 추출해 재실행해, 실제로 그 블록이 중복을 감지해 실패하는지 확인한다 - 별도로
     * 베껴 쓴 SQL이 아니라 실제 마이그레이션 파일의 문구 그대로를 검증하므로 파일과 어긋날 위험이 없다.
     * 인덱스는 finally에서 반드시 원상 복구한다.
     */
    @Test
    void 활성_배정_중복이_있으면_마이그레이션의_중복_검사가_실패한다() throws IOException {
        String duplicateCheckSql = extractDuplicateCheckBlock(readV11Sql());
        UUID assetId = createAsset("AST-9211-0005");

        jdbcTemplate.execute("DROP INDEX idx_asset_assignment_tangible_asset_active");
        try {
            insertActiveAssignment(assetId, UUID.randomUUID());
            insertActiveAssignment(assetId, UUID.randomUUID());

            assertThatThrownBy(() -> jdbcTemplate.execute(duplicateCheckSql))
                    .hasMessageContaining("자산별 활성 배정")
                    .hasMessageContaining(assetId.toString());
        } finally {
            jdbcTemplate.update("DELETE FROM asset_assignment WHERE tangible_asset_id = ?", assetId);
            jdbcTemplate.execute(
                    "CREATE UNIQUE INDEX idx_asset_assignment_tangible_asset_active ON asset_assignment (tangible_asset_id) WHERE released_at IS NULL");
        }
    }

    private String readV11Sql() throws IOException {
        try (InputStream is = new ClassPathResource("db/migration/V11__asset_assignment_unique_active.sql").getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String extractDuplicateCheckBlock(String fullSql) {
        int start = fullSql.indexOf("DO $$");
        int end = fullSql.indexOf("END $$;", start) + "END $$;".length();
        return fullSql.substring(start, end);
    }
}
