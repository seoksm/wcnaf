package com.winitech.smartAsset.infrastructure.tangibleAsset;

import com.winitech.smartAsset.domain.assetCategory.AssetCategory;
import com.winitech.smartAsset.domain.assetLocation.AssetLocation;
import com.winitech.smartAsset.domain.tangibleAsset.AssetCodeSequencer;
import com.winitech.smartAsset.domain.tangibleAsset.TangibleAsset;
import com.winitech.smartAsset.infrastructure.assetCategory.AssetCategoryRepository;
import com.winitech.smartAsset.infrastructure.assetLocation.AssetLocationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * V10__asset_code_sequence_backfill.sql 백필 로직 검증.
 * <p>
 * 한 번 적용된 Flyway 마이그레이션은 체크섬이 고정되어 Flyway가 다시 실행해주지 않으므로, 이 테스트는
 * classpath에 있는 그 파일의 "실제 내용"을 읽어 그대로 실행한다 - 별도로 베껴 둔 SQL 문자열이 아니라
 * 실제 배포되는 산출물 자체를 검증하므로 파일과 테스트가 어긋날 위험이 없다.
 * <p>
 * asset_code_sequence 원자적 채번 검증(AssetCodeSequencerImplTest)과 동일하게, 실제 Postgres의
 * 정규식/GREATEST/ON CONFLICT 동작에 의존하므로 실제 DB에 붙는 통합 테스트로 작성한다. 실제 회계연도와
 * 절대 겹치지 않는 9000번대 가상 연도만 사용하고, 테스트 종료 후 해당 데이터를 모두 정리한다.
 */
@SpringBootTest
class AssetCodeSequenceBackfillMigrationTest {

    private static final int YEAR_A = 9001;
    private static final int YEAR_B = 9002;
    private static final int YEAR_EMPTY = 9003;

    @Autowired
    private TangibleAssetRepository tangibleAssetRepository;

    @Autowired
    private AssetCategoryRepository assetCategoryRepository;

    @Autowired
    private AssetLocationRepository assetLocationRepository;

    @Autowired
    private AssetCodeSequencer assetCodeSequencer;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private AssetCategory testCategory;
    private AssetLocation testLocation;

    @BeforeEach
    void setUp() {
        // tangible_asset.asset_category_id/asset_location_id는 DB에서 NOT NULL이므로(V2), 백필
        // 대상 자산을 저장하려면 유효한 카테고리/위치가 있어야 한다 - 이 테스트만을 위한 값을 만든다.
        testCategory = assetCategoryRepository.save(AssetCategory.builder()
                .categoryCode("BACKFILL-TEST-CAT")
                .categoryName("백필테스트카테고리")
                .usefulLifeMonths(36)
                .build());
        testLocation = assetLocationRepository.save(AssetLocation.builder()
                .locationName("백필테스트위치")
                .build());
    }

    @AfterEach
    void cleanUp() {
        jdbcTemplate.update("DELETE FROM tangible_asset WHERE asset_code LIKE 'AST-900%' OR asset_code = '수동입력자산코드'");
        jdbcTemplate.update("DELETE FROM asset_code_sequence WHERE fiscal_year >= 9000 AND fiscal_year < 9100");
        jdbcTemplate.update("DELETE FROM asset_category WHERE asset_category_id = ?", testCategory.getId());
        jdbcTemplate.update("DELETE FROM asset_location WHERE asset_location_id = ?", testLocation.getId());
    }

    private String backfillSql() throws IOException {
        try (InputStream is = new ClassPathResource("db/migration/V10__asset_code_sequence_backfill.sql").getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void saveAsset(String assetCode) {
        TangibleAsset asset = TangibleAsset.builder()
                .assetCode(assetCode)
                .assetName("백필테스트")
                .category(testCategory)
                .location(testLocation)
                .acquisitionDate(LocalDate.of(2024, 1, 1))
                .acquisitionAmount(BigDecimal.valueOf(1_000_000))
                .build();
        tangibleAssetRepository.save(asset);
    }

    private Integer lastSeq(int fiscalYear) {
        List<Integer> rows = jdbcTemplate.queryForList(
                "SELECT last_seq FROM asset_code_sequence WHERE fiscal_year = ?", Integer.class, fiscalYear);
        return rows.isEmpty() ? null : rows.get(0);
    }

    @Test
    void 여러_연도의_기존_코드가_각각_올바르게_초기화된다() throws IOException {
        saveAsset("AST-9001-0001");
        saveAsset("AST-9001-0042");
        saveAsset("AST-9001-0017");
        saveAsset("AST-9002-0005");

        jdbcTemplate.execute(backfillSql());

        assertThat(lastSeq(YEAR_A)).isEqualTo(42);
        assertThat(lastSeq(YEAR_B)).isEqualTo(5);
    }

    @Test
    void 채번_테이블_값이_자산_최대값보다_크면_그대로_유지된다() throws IOException {
        saveAsset("AST-9001-0010");
        jdbcTemplate.update(
                "INSERT INTO asset_code_sequence (fiscal_year, last_seq, create_at, update_at) VALUES (?, ?, now(), now())",
                YEAR_A, 500);

        jdbcTemplate.execute(backfillSql());

        assertThat(lastSeq(YEAR_A)).isEqualTo(500);
    }

    @Test
    void 잘못된_형식의_코드는_무시된다() throws IOException {
        saveAsset("AST-9001-0003");
        saveAsset("AST-9001-abcd");
        saveAsset("수동입력자산코드");

        jdbcTemplate.execute(backfillSql());

        assertThat(lastSeq(YEAR_A)).isEqualTo(3);
    }

    @Test
    void 기존_자산이_없는_연도는_첫_발급이_0001부터_시작한다() throws IOException {
        jdbcTemplate.execute(backfillSql());

        assertThat(lastSeq(YEAR_EMPTY)).isNull();
        assertThat(assetCodeSequencer.nextSequence(YEAR_EMPTY)).isEqualTo(1);
    }

    @Test
    void 같은_입력에_대해_재실행해도_값이_감소하지_않는다() throws IOException {
        saveAsset("AST-9001-0025");
        String sql = backfillSql();

        jdbcTemplate.execute(sql);
        assertThat(lastSeq(YEAR_A)).isEqualTo(25);

        jdbcTemplate.execute(sql);
        assertThat(lastSeq(YEAR_A)).isEqualTo(25);
    }

    @Test
    void 백필_이후_다음_채번은_기존_최대값과_충돌하지_않는다() throws IOException {
        saveAsset("AST-9001-0001");
        saveAsset("AST-9001-0002");
        saveAsset("AST-9001-0003");

        jdbcTemplate.execute(backfillSql());

        assertThat(assetCodeSequencer.nextSequence(YEAR_A)).isEqualTo(4);
    }
}
