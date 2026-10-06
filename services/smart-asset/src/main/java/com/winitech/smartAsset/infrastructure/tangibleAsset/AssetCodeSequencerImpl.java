package com.winitech.smartAsset.infrastructure.tangibleAsset;

import com.winitech.common.exception.IllegalStatusException;
import com.winitech.smartAsset.domain.tangibleAsset.AssetCodeSequencer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * asset_code_sequence 테이블에 대해 INSERT ... ON CONFLICT ... DO UPDATE ... RETURNING 을
 * 사용해 연도별 다음 순번을 원자적으로 발급한다. Postgres는 이 문장을 실행하는 동안 충돌 대상
 * (fiscal_year) 행에 row lock을 걸어, 동시에 들어온 다른 요청은 이 트랜잭션이 끝날 때까지
 * 대기했다가 그 다음 값을 받아가므로 두 요청이 같은 순번을 받는 일이 없다.
 * <p>
 * 이 메서드는 자체 트랜잭션을 시작하지 않고 호출자(TangibleAssetServiceImpl)의 트랜잭션에
 * 참여한다 - 자산 등록 자체가 실패해 트랜잭션이 롤백되면 이 UPDATE도 함께 롤백되므로,
 * 롤백으로 인한 채번 공백(번호를 소비했지만 실제로는 사용되지 않는 경우)이 발생하지 않는다.
 * (자체 트랜잭션(REQUIRES_NEW)으로 분리했다면 등록 실패와 무관하게 번호가 즉시 소비되어
 * 일반적인 DB 시퀀스처럼 공백이 허용됐겠지만, 여기서는 의도적으로 그렇게 하지 않았다.)
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class AssetCodeSequencerImpl implements AssetCodeSequencer {

    private static final String NEXT_SEQUENCE_SQL =
            "INSERT INTO asset_code_sequence (fiscal_year, last_seq, create_at, update_at) " +
            "VALUES (?, 1, now(), now()) " +
            "ON CONFLICT (fiscal_year) DO UPDATE SET last_seq = asset_code_sequence.last_seq + 1, update_at = now() " +
            "RETURNING last_seq";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public int nextSequence(int fiscalYear) {
        try {
            Integer nextSeq = jdbcTemplate.queryForObject(NEXT_SEQUENCE_SQL, Integer.class, fiscalYear);
            if (nextSeq == null) {
                throw new IllegalStatusException("자산코드 채번에 실패했습니다.");
            }
            return nextSeq;
        } catch (DataAccessException e) {
            log.error("자산코드 채번 중 DB 오류 (fiscalYear={}): {}", fiscalYear, e.getMessage());
            throw new IllegalStatusException("자산코드 채번 중 오류가 발생했습니다.");
        }
    }
}
