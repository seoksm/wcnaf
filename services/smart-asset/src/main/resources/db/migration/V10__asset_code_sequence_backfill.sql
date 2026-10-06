-- V6에서 asset_code_sequence(연도별 채번 카운터)를 도입했지만, 그 이전부터 있던 tangible_asset의
-- 기존 AST-YYYY-NNNN 코드를 이 카운터에 반영하지 않았다. 따라서 채번 테이블이 비어 있는 연도에
-- 신규 등록이 들어오면 이미 사용 중인 코드와 같은 번호(예: 0001)부터 다시 발급해 유니크 제약 충돌이
-- 날 수 있다. 이 마이그레이션은 기존 tangible_asset에서 "정확히" AST-YYYY-NNNN 형식(연도 4자리,
-- 순번 4자리)인 코드만 골라 연도별 최대 순번을 계산해 asset_code_sequence.last_seq에 반영한다.
--
-- - 형식이 다른 코드(수동 입력, 오타, 자릿수가 다른 값 등)는 아래 WHERE의 정규식에 걸러져 집계에서
--   조용히 제외된다 - 이 마이그레이션은 그런 값 때문에 실패하지 않는다. 그런 값의 목록은 이 마이그레이션이
--   실행하지 않는 별도의 점검 SQL로 확인한다(운영 문서 docs/운영/자산코드_채번_마이그레이션.md 참고).
-- - 이미 asset_code_sequence에 해당 연도 행이 있으면(V6 배포 이후 이미 몇 건 채번된 경우 등)
--   GREATEST로 기존 last_seq와 tangible_asset 집계값 중 큰 값을 유지한다 - 이 INSERT를 다시 실행해도
--   (예: 다른 환경에 같은 마이그레이션을 재적용해도) 값이 줄어들지 않는다.
-- - tangible_asset에 AST-YYYY-NNNN 형식 코드가 전혀 없는 연도는 애초에 집계 결과에 나타나지 않으므로
--   그 연도의 최초 발급은 AssetCodeSequencerImpl의 기존 동작대로 자연스럽게 0001부터 시작한다.
INSERT INTO asset_code_sequence (fiscal_year, last_seq, create_at, update_at)
SELECT
    yearly.fiscal_year,
    yearly.max_seq,
    now(),
    now()
FROM (
    SELECT
        substring(asset_code FROM 5 FOR 4)::integer AS fiscal_year,
        MAX(substring(asset_code FROM 10 FOR 4)::integer) AS max_seq
    FROM tangible_asset
    WHERE asset_code ~ '^AST-[0-9]{4}-[0-9]{4}$'
    GROUP BY substring(asset_code FROM 5 FOR 4)
) yearly
ON CONFLICT (fiscal_year) DO UPDATE
    SET last_seq = GREATEST(asset_code_sequence.last_seq, EXCLUDED.last_seq),
        update_at = now();
