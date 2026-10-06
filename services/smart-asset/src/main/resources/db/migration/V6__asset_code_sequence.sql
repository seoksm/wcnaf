-- 자산코드(AST-YYYY-NNNN) 채번을 동시 요청에도 안전하게 만들기 위한 연도별 카운터 테이블.
-- 기존 countByAssetCodePrefix(prefix) + 1 방식은 두 트랜잭션이 동시에 같은 개수를 세어
-- 같은 다음 번호를 계산할 수 있어(read-then-write 경쟁 상태) 중복 코드가 생길 수 있었다.
-- INSERT ... ON CONFLICT (fiscal_year) DO UPDATE ... RETURNING 으로 원자적으로 발급한다
-- (AssetCodeSequencerImpl 참고) - Postgres가 충돌 대상 행에 row lock을 걸어 동시 요청을
-- 자동으로 직렬화하므로 애플리케이션 레벨의 별도 락이 필요 없다.
CREATE TABLE asset_code_sequence
(
    fiscal_year integer NOT NULL,
    last_seq    integer NOT NULL,
    create_at   timestamp without time zone NOT NULL DEFAULT now(),
    update_at   timestamp without time zone NOT NULL DEFAULT now(),
    CONSTRAINT asset_code_sequence_pkey PRIMARY KEY (fiscal_year)
);

COMMENT ON TABLE asset_code_sequence IS 'Q-14 자산코드(AST-YYYY-NNNN) 채번용 연도별 마지막 순번';
COMMENT ON COLUMN asset_code_sequence.fiscal_year IS '회계연도 (예: 2026)';
COMMENT ON COLUMN asset_code_sequence.last_seq IS '해당 연도에 마지막으로 발급된 순번';
