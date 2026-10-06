-- 유형자산 목록/대량 조회 성능 개선(페이지네이션 도입)에 맞춰 검색·정렬에 쓰는 인덱스를 정리한다.
--
-- 검색 조건이 asset_code/asset_name/serial_no에 대해 LIKE '%keyword%' (양쪽 와일드카드, 부분 문자열
-- 검색)이므로 일반 B-tree 인덱스는 도움이 되지 않는다(B-tree는 접두어 검색 LIKE 'keyword%'에만 유효).
-- PostgreSQL에서 부분 문자열 LIKE/ILIKE를 가속하는 표준 방법은 pg_trgm 확장의 트라이그램 GIN 인덱스다.
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS idx_tangible_asset_asset_code_trgm
    ON tangible_asset USING gin (asset_code gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_tangible_asset_asset_name_trgm
    ON tangible_asset USING gin (asset_name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_tangible_asset_serial_no_trgm
    ON tangible_asset USING gin (serial_no gin_trgm_ops);

-- 키워드 없이 status='ENABLE' 전체를 create_at 내림차순으로 훑는 경우(기본 목록 조회, 감가상각
-- 배치 조회)를 위한 복합 인덱스. status 필터와 정렬 기준을 하나의 인덱스로 함께 커버한다.
CREATE INDEX IF NOT EXISTS idx_tangible_asset_status_create_at
    ON tangible_asset (status, create_at DESC);
