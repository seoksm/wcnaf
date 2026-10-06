package com.winitech.smartAsset.infrastructure.tangibleAsset;

import com.winitech.smartAsset.domain.tangibleAsset.AssetCodeSequencer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * asset_code_sequence 기반 원자적 채번(AssetCodeSequencerImpl) 검증.
 * 실제 Postgres의 INSERT ... ON CONFLICT ... RETURNING 행 잠금 동작에 의존하는 부분을
 * 검증해야 하므로 실제 DB에 붙는 통합 테스트로 작성한다. 다른 테스트/운영 데이터와 겹치지
 * 않도록 실존할 수 없는 연도 값(음수)을 키로 사용하고, 각 테스트 종료 후 해당 행을 정리한다.
 */
@SpringBootTest
class AssetCodeSequencerImplTest {

    @Autowired
    private AssetCodeSequencer assetCodeSequencer;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private int testFiscalYear;

    @AfterEach
    void cleanUp() {
        jdbcTemplate.update("DELETE FROM asset_code_sequence WHERE fiscal_year < 0");
    }

    private int freshTestYear() {
        // 실제로는 나올 수 없는 음수 연도를 매 테스트마다 새로 발급해 서로 다른 테스트끼리도
        // 절대 겹치지 않게 한다.
        testFiscalYear = -(int) (System.nanoTime() % 1_000_000_000L) - 1;
        return testFiscalYear;
    }

    @Test
    void 동일_연도_순차_발급() {
        int year = freshTestYear();

        assertThat(assetCodeSequencer.nextSequence(year)).isEqualTo(1);
        assertThat(assetCodeSequencer.nextSequence(year)).isEqualTo(2);
        assertThat(assetCodeSequencer.nextSequence(year)).isEqualTo(3);
    }

    @Test
    void 연도가_바뀌면_1부터_다시_시작한다() {
        int yearA = freshTestYear();
        int yearB = yearA - 1;

        assertThat(assetCodeSequencer.nextSequence(yearA)).isEqualTo(1);
        assertThat(assetCodeSequencer.nextSequence(yearA)).isEqualTo(2);

        // 다른 연도는 완전히 독립적으로 1부터 시작해야 한다
        assertThat(assetCodeSequencer.nextSequence(yearB)).isEqualTo(1);

        jdbcTemplate.update("DELETE FROM asset_code_sequence WHERE fiscal_year = ?", yearB);
    }

    @Test
    void 여러_스레드가_동시에_발급해도_중복이_없다() throws Exception {
        int year = freshTestYear();
        int threadCount = 50;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        try {
            List<Callable<Integer>> tasks = IntStream.range(0, threadCount)
                    .<Callable<Integer>>mapToObj(i -> () -> assetCodeSequencer.nextSequence(year))
                    .collect(Collectors.toList());

            List<Future<Integer>> futures = executor.invokeAll(tasks, 30, TimeUnit.SECONDS);

            List<Integer> results = new java.util.ArrayList<>();
            for (Future<Integer> future : futures) {
                results.add(future.get());
            }

            Set<Integer> distinct = new java.util.HashSet<>(results);

            assertThat(results).hasSize(threadCount);
            assertThat(distinct)
                    .as("동시 발급된 순번은 모두 달라야 하며 중복이 없어야 한다")
                    .hasSize(threadCount);
            assertThat(distinct)
                    .as("1부터 threadCount까지 빠짐없이 연속으로 발급돼야 한다")
                    .containsExactlyInAnyOrderElementsOf(
                            IntStream.rangeClosed(1, threadCount).boxed().collect(Collectors.toList()));
        } finally {
            executor.shutdown();
        }
    }
}
