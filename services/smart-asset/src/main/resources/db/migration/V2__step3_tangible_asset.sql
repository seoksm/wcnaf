CREATE TABLE tangible_asset (
    tangible_asset_id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    asset_code character varying(30) NOT NULL,
    asset_name character varying(200) NOT NULL,
    asset_category_id uuid NOT NULL,
    asset_location_id uuid NOT NULL,
    life_status character varying(20) NOT NULL DEFAULT 'USE',
    assign_type character varying(20) NOT NULL DEFAULT 'UNASSIGNED',
    acquisition_date date NOT NULL,
    acquisition_amount numeric(15,2) NOT NULL,
    model_name character varying(200),
    manufacturer character varying(200),
    serial_no character varying(200),
    current_member_id uuid,
    memo character varying(1000),
    ext_txt1 character varying(500), ext_txt2 character varying(500), ext_txt3 character varying(500),
    ext_txt4 character varying(500), ext_txt5 character varying(500), ext_txt6 character varying(500),
    ext_txt7 character varying(500), ext_txt8 character varying(500), ext_txt9 character varying(500), ext_txt10 character varying(500),
    ext_num1 numeric(15,2), ext_num2 numeric(15,2), ext_num3 numeric(15,2), ext_num4 numeric(15,2), ext_num5 numeric(15,2),
    ext_dt1 date, ext_dt2 date, ext_dt3 date,
    status character varying(10) NOT NULL DEFAULT 'ENABLE',
    CONSTRAINT tangible_asset_pkey PRIMARY KEY (tangible_asset_id),
    CONSTRAINT tangible_asset_asset_code_key UNIQUE (asset_code),
    CONSTRAINT tangible_asset_category_fk FOREIGN KEY (asset_category_id) REFERENCES asset_category(asset_category_id),
    CONSTRAINT tangible_asset_location_fk FOREIGN KEY (asset_location_id) REFERENCES asset_location(asset_location_id)
);
COMMENT ON TABLE tangible_asset IS '유형자산 마스터 (S-210~212)';
COMMENT ON COLUMN tangible_asset.tangible_asset_id IS '유형자산 ID';
COMMENT ON COLUMN tangible_asset.create_at IS '생성일시';
COMMENT ON COLUMN tangible_asset.update_at IS '수정일시';
COMMENT ON COLUMN tangible_asset.asset_code IS '자산코드 - Q-14 AST-YYYY-NNNN 자동채번, 업서트 키';
COMMENT ON COLUMN tangible_asset.asset_name IS '자산명';
COMMENT ON COLUMN tangible_asset.asset_category_id IS '자산 종류 ID (asset_category 참조)';
COMMENT ON COLUMN tangible_asset.asset_location_id IS '자산 위치 ID (asset_location 참조)';
COMMENT ON COLUMN tangible_asset.life_status IS '생애 상태 - Q-15 2축 분리 (USE/STORAGE/REPAIR/DISUSE/DISPOSED)';
COMMENT ON COLUMN tangible_asset.assign_type IS '배정 형태 - Q-15 2축 분리 (UNASSIGNED/PERSONAL/SHARED/LOANABLE/ON_LOAN)';
COMMENT ON COLUMN tangible_asset.acquisition_date IS '취득일';
COMMENT ON COLUMN tangible_asset.acquisition_amount IS '취득가액 - 감가상각 기준, 필수';
COMMENT ON COLUMN tangible_asset.model_name IS '모델명';
COMMENT ON COLUMN tangible_asset.manufacturer IS '제조사';
COMMENT ON COLUMN tangible_asset.serial_no IS '시리얼번호';
COMMENT ON COLUMN tangible_asset.current_member_id IS '현재 배정 사용자 ID (common_user 참조, 조회성능용 비정규화) - Q-16';
COMMENT ON COLUMN tangible_asset.memo IS '메모';
COMMENT ON COLUMN tangible_asset.ext_txt1 IS '사용자정의 텍스트필드 1 - Q-13 예비컬럼';
COMMENT ON COLUMN tangible_asset.ext_txt2 IS '사용자정의 텍스트필드 2';
COMMENT ON COLUMN tangible_asset.ext_txt3 IS '사용자정의 텍스트필드 3';
COMMENT ON COLUMN tangible_asset.ext_txt4 IS '사용자정의 텍스트필드 4';
COMMENT ON COLUMN tangible_asset.ext_txt5 IS '사용자정의 텍스트필드 5';
COMMENT ON COLUMN tangible_asset.ext_txt6 IS '사용자정의 텍스트필드 6';
COMMENT ON COLUMN tangible_asset.ext_txt7 IS '사용자정의 텍스트필드 7';
COMMENT ON COLUMN tangible_asset.ext_txt8 IS '사용자정의 텍스트필드 8';
COMMENT ON COLUMN tangible_asset.ext_txt9 IS '사용자정의 텍스트필드 9';
COMMENT ON COLUMN tangible_asset.ext_txt10 IS '사용자정의 텍스트필드 10';
COMMENT ON COLUMN tangible_asset.ext_num1 IS '사용자정의 숫자필드 1 - Q-13 예비컬럼';
COMMENT ON COLUMN tangible_asset.ext_num2 IS '사용자정의 숫자필드 2';
COMMENT ON COLUMN tangible_asset.ext_num3 IS '사용자정의 숫자필드 3';
COMMENT ON COLUMN tangible_asset.ext_num4 IS '사용자정의 숫자필드 4';
COMMENT ON COLUMN tangible_asset.ext_num5 IS '사용자정의 숫자필드 5';
COMMENT ON COLUMN tangible_asset.ext_dt1 IS '사용자정의 날짜필드 1 - Q-13 예비컬럼';
COMMENT ON COLUMN tangible_asset.ext_dt2 IS '사용자정의 날짜필드 2';
COMMENT ON COLUMN tangible_asset.ext_dt3 IS '사용자정의 날짜필드 3';
COMMENT ON COLUMN tangible_asset.status IS '사용 상태 (ENABLE/DISABLE, 삭제는 소프트 삭제)';

CREATE TABLE asset_assignment (
    asset_assignment_id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    tangible_asset_id uuid NOT NULL,
    member_id uuid NOT NULL,
    assign_type character varying(20) NOT NULL,
    assigned_at timestamp without time zone NOT NULL,
    released_at timestamp without time zone,
    assigned_by uuid,
    CONSTRAINT asset_assignment_pkey PRIMARY KEY (asset_assignment_id),
    CONSTRAINT asset_assignment_tangible_asset_fk FOREIGN KEY (tangible_asset_id) REFERENCES tangible_asset(tangible_asset_id)
);
COMMENT ON TABLE asset_assignment IS '자산 배정 이력 - Q-16, 현재 배정은 released_at IS NULL 인 행 하나';
COMMENT ON COLUMN asset_assignment.asset_assignment_id IS '배정 이력 ID';
COMMENT ON COLUMN asset_assignment.create_at IS '생성일시';
COMMENT ON COLUMN asset_assignment.update_at IS '수정일시';
COMMENT ON COLUMN asset_assignment.tangible_asset_id IS '유형자산 ID';
COMMENT ON COLUMN asset_assignment.member_id IS '배정받은 사용자 ID (common_user 참조)';
COMMENT ON COLUMN asset_assignment.assign_type IS '배정 형태 (PERSONAL/SHARED/LOANABLE/ON_LOAN)';
COMMENT ON COLUMN asset_assignment.assigned_at IS '배정 일시';
COMMENT ON COLUMN asset_assignment.released_at IS '회수(해제) 일시 - NULL이면 현재 배정중';
COMMENT ON COLUMN asset_assignment.assigned_by IS '배정 처리자 ID (common_user 참조)';

CREATE INDEX idx_asset_assignment_tangible_asset_current ON asset_assignment (tangible_asset_id) WHERE released_at IS NULL;
