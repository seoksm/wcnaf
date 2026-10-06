CREATE TABLE asset_category (
    asset_category_id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    category_code character varying(50) NOT NULL,
    category_name character varying(100) NOT NULL,
    sort_seq integer,
    editable boolean NOT NULL DEFAULT true,
    useful_life_months integer,
    residual_rate numeric(5,2),
    memorandum_value numeric(15,2) NOT NULL DEFAULT 1000,
    status character varying(10) NOT NULL DEFAULT 'ENABLE',
    CONSTRAINT asset_category_pkey PRIMARY KEY (asset_category_id),
    CONSTRAINT asset_category_category_code_key UNIQUE (category_code)
);
COMMENT ON TABLE asset_category IS '자산 종류 - Q-15 감가상각 기준 포함 (S-200)';
COMMENT ON COLUMN asset_category.asset_category_id IS '자산 종류 ID';
COMMENT ON COLUMN asset_category.create_at IS '생성일시';
COMMENT ON COLUMN asset_category.update_at IS '수정일시';
COMMENT ON COLUMN asset_category.category_code IS '자산 종류 코드 (등록 후 변경 불가)';
COMMENT ON COLUMN asset_category.category_name IS '자산 종류명';
COMMENT ON COLUMN asset_category.sort_seq IS '정렬 순서';
COMMENT ON COLUMN asset_category.editable IS '기본 4종(노트북/데스크탑PC/모니터/기타)은 false - 삭제 불가, 명칭만 수정 가능';
COMMENT ON COLUMN asset_category.useful_life_months IS '내용연수(개월) - 감가상각 기준';
COMMENT ON COLUMN asset_category.residual_rate IS '잔존가치율(%) - 감가상각 기준';
COMMENT ON COLUMN asset_category.memorandum_value IS '비망가액(원) - Q-25, 기본 1000';
COMMENT ON COLUMN asset_category.status IS '사용 상태 (ENABLE/DISABLE, 삭제는 소프트 삭제)';

CREATE TABLE asset_location (
    asset_location_id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    location_name character varying(100) NOT NULL,
    sort_seq integer,
    status character varying(10) NOT NULL DEFAULT 'ENABLE',
    CONSTRAINT asset_location_pkey PRIMARY KEY (asset_location_id)
);
COMMENT ON TABLE asset_location IS '자산 위치 - Q-17 단순 목록(계층 없음) (S-201)';
COMMENT ON COLUMN asset_location.asset_location_id IS '자산 위치 ID';
COMMENT ON COLUMN asset_location.create_at IS '생성일시';
COMMENT ON COLUMN asset_location.update_at IS '수정일시';
COMMENT ON COLUMN asset_location.location_name IS '자산 위치명';
COMMENT ON COLUMN asset_location.sort_seq IS '정렬 순서';
COMMENT ON COLUMN asset_location.status IS '사용 상태 (ENABLE/DISABLE, 삭제는 소프트 삭제)';
