-- 확정 화면을 tangible_asset/asset_category 재조회 없이 그대로 재구성할 수 있도록, 확정 시점의
-- 표시값(자산 코드/명, 분류명, 취득일/가액, 내용연수)과 상각 제외 사유를 스냅샷 행에 함께 저장한다.
-- 확정된 분기 조회는 이제 이 컬럼들만 사용하고 tangible_asset/asset_category를 다시 읽지 않으므로,
-- 확정 이후 자산이 수정·불용 처리되거나 분류 기준이 바뀌어도 이미 확정된 분기의 결과는 바뀌지 않는다.
ALTER TABLE depreciation_snapshot
    ADD COLUMN asset_code character varying(50),
    ADD COLUMN asset_name character varying(200),
    ADD COLUMN category_name character varying(200),
    ADD COLUMN acquisition_date date,
    ADD COLUMN acquisition_amount numeric(15,2),
    ADD COLUMN useful_life_months integer,
    ADD COLUMN excluded_reason character varying(30);

-- 이 마이그레이션 이전에 이미 확정되어 저장된 스냅샷 행이 있다면, 지금 시점의 tangible_asset 값으로
-- 새 컬럼을 최선을 다해 채워둔다(완전한 소급 재현은 불가능하므로 호환성 차원의 backfill).
-- 이 UPDATE 이후로는 tangible_asset이 바뀌어도 이 값들은 더 이상 따라 변하지 않는다.
UPDATE depreciation_snapshot s
SET asset_code = t.asset_code,
    asset_name = t.asset_name,
    acquisition_date = t.acquisition_date,
    acquisition_amount = t.acquisition_amount
FROM tangible_asset t
WHERE s.tangible_asset_id = t.tangible_asset_id
  AND s.asset_code IS NULL;

ALTER TABLE depreciation_snapshot
    ALTER COLUMN asset_code SET NOT NULL,
    ALTER COLUMN asset_name SET NOT NULL,
    ALTER COLUMN acquisition_date SET NOT NULL,
    ALTER COLUMN acquisition_amount SET NOT NULL;

-- 상각 제외 자산도 스냅샷 행으로 보존하므로(제외 자산은 계산값이 없다) NOT NULL 제약을 완화한다.
ALTER TABLE depreciation_snapshot
    ALTER COLUMN opening_accumulated DROP NOT NULL,
    ALTER COLUMN period_depreciation DROP NOT NULL,
    ALTER COLUMN closing_accumulated DROP NOT NULL;

COMMENT ON COLUMN depreciation_snapshot.asset_code IS '확정 시점 자산 코드 - 이후 자산이 바뀌거나 삭제돼도 불변';
COMMENT ON COLUMN depreciation_snapshot.asset_name IS '확정 시점 자산명';
COMMENT ON COLUMN depreciation_snapshot.category_name IS '확정 시점 자산종류명 - 종류 미지정/삭제 시 NULL';
COMMENT ON COLUMN depreciation_snapshot.acquisition_date IS '확정 시점 취득일';
COMMENT ON COLUMN depreciation_snapshot.acquisition_amount IS '확정 시점 취득가액';
COMMENT ON COLUMN depreciation_snapshot.useful_life_months IS '확정 시점 내용연수(개월) - 상각 기준 미설정 자산은 NULL';
COMMENT ON COLUMN depreciation_snapshot.excluded_reason IS '확정 시점 상각 제외 사유 (NO_AMOUNT/NO_BASIS/NOT_DEPRECIABLE_STATUS) - 상각 대상이었으면 NULL';
COMMENT ON COLUMN depreciation_snapshot.opening_accumulated IS '기초 감가상각누계액 - 제외 자산은 NULL';
COMMENT ON COLUMN depreciation_snapshot.period_depreciation IS '당기 상각액 - 제외 자산은 NULL';
COMMENT ON COLUMN depreciation_snapshot.closing_accumulated IS '기말 감가상각누계액 - 제외 자산은 NULL';
