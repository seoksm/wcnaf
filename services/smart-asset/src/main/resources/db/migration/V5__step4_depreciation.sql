CREATE TABLE depreciation_confirmation (
    depreciation_confirmation_id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    fiscal_year integer NOT NULL,
    quarter character varying(2) NOT NULL,
    confirmed_at timestamp without time zone NOT NULL,
    confirmed_by uuid,
    released_at timestamp without time zone,
    released_by uuid,
    release_reason character varying(500),
    CONSTRAINT depreciation_confirmation_pkey PRIMARY KEY (depreciation_confirmation_id)
);
COMMENT ON TABLE depreciation_confirmation IS '결산 확정/해제 이력 (S-232, D7) - 분기당 released_at IS NULL인 행이 최대 1건(현재 활성 확정)';
COMMENT ON COLUMN depreciation_confirmation.fiscal_year IS '회계연도';
COMMENT ON COLUMN depreciation_confirmation.quarter IS '누적 분기 (Q1=1~3월, Q2=1~6월, Q3=1~9월, Q4=1~12월)';
COMMENT ON COLUMN depreciation_confirmation.confirmed_at IS '확정 처리 일시';
COMMENT ON COLUMN depreciation_confirmation.confirmed_by IS '확정 처리자 ID';
COMMENT ON COLUMN depreciation_confirmation.released_at IS '해제 일시 - NULL이면 현재 확정 상태';
COMMENT ON COLUMN depreciation_confirmation.released_by IS '해제 처리자 ID';
COMMENT ON COLUMN depreciation_confirmation.release_reason IS '해제 사유';

CREATE UNIQUE INDEX idx_depreciation_confirmation_active ON depreciation_confirmation (fiscal_year, quarter) WHERE released_at IS NULL;

CREATE TABLE depreciation_snapshot (
    depreciation_snapshot_id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    tangible_asset_id uuid NOT NULL,
    fiscal_year integer NOT NULL,
    quarter character varying(2) NOT NULL,
    opening_accumulated numeric(15,2) NOT NULL,
    period_depreciation numeric(15,2) NOT NULL,
    closing_accumulated numeric(15,2) NOT NULL,
    book_value numeric(15,2) NOT NULL,
    CONSTRAINT depreciation_snapshot_pkey PRIMARY KEY (depreciation_snapshot_id),
    CONSTRAINT depreciation_snapshot_asset_fk FOREIGN KEY (tangible_asset_id) REFERENCES tangible_asset(tangible_asset_id)
);
COMMENT ON TABLE depreciation_snapshot IS '확정된 분기의 자산별 상각 산출 결과 (S-230/231/232, D7) - 확정된 기간만 존재, 해제 시 삭제';
COMMENT ON COLUMN depreciation_snapshot.fiscal_year IS '회계연도';
COMMENT ON COLUMN depreciation_snapshot.quarter IS '누적 분기 (Q1~Q4)';
COMMENT ON COLUMN depreciation_snapshot.opening_accumulated IS '기초 감가상각누계액 (전년도말 기준)';
COMMENT ON COLUMN depreciation_snapshot.period_depreciation IS '당기 상각액 (기말누계 - 기초누계)';
COMMENT ON COLUMN depreciation_snapshot.closing_accumulated IS '기말 감가상각누계액';
COMMENT ON COLUMN depreciation_snapshot.book_value IS '기말 장부가 (취득가액 - 기말누계)';

CREATE UNIQUE INDEX idx_depreciation_snapshot_asset_period ON depreciation_snapshot (tangible_asset_id, fiscal_year, quarter);
