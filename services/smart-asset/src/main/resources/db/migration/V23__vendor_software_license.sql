-- S-520 소프트웨어 마스터, S-530~533 라이선스, S-540 공급사 (Step 7 Phase 4-2).
--
-- 수량 정합성(보유·배정·잔여)은 컬럼으로 저장하지 않는다 - license_purchase_record.quantity 합계와
-- license_assigned_user(released_at IS NULL) 건수에서 매번 계산하는 파생값이다(L2와 동일한 이유).
CREATE TABLE vendor
(
    vendor_id      uuid                    NOT NULL,
    create_at      timestamp without time zone,
    update_at      timestamp without time zone,
    name           character varying(200) NOT NULL,
    contact_name   character varying(100),
    contact_phone  character varying(50),
    contact_email  character varying(200),
    memo           character varying(1000),
    status         character varying(10)  NOT NULL DEFAULT 'ENABLE',
    CONSTRAINT vendor_pkey PRIMARY KEY (vendor_id)
);

COMMENT ON TABLE vendor IS 'S-540 공급사 - 렌탈·라이선스 공용(설계문서 §3)';

CREATE TABLE software
(
    software_id  uuid                    NOT NULL,
    create_at    timestamp without time zone,
    update_at    timestamp without time zone,
    name         character varying(200) NOT NULL,
    publisher    character varying(200),
    category     character varying(50),
    memo         character varying(1000),
    status       character varying(10)  NOT NULL DEFAULT 'ENABLE',
    CONSTRAINT software_pkey PRIMARY KEY (software_id),
    CONSTRAINT software_name_uk UNIQUE (name)
);

COMMENT ON TABLE software IS 'S-520 소프트웨어 마스터 - 라이선스 "포함 SW" 자동완성 소스(R2, 벤더 DB 연동 제외)';

CREATE TABLE license
(
    license_id  uuid                    NOT NULL,
    create_at   timestamp without time zone,
    update_at   timestamp without time zone,
    name        character varying(200) NOT NULL,
    memo        character varying(1000),
    status      character varying(10)  NOT NULL DEFAULT 'ENABLE',
    CONSTRAINT license_pkey PRIMARY KEY (license_id)
);

COMMENT ON TABLE license IS 'S-530~533 라이선스 상품 1건 (예: "Adobe Creative Cloud"). 보유·배정·잔여는 하위 테이블에서 계산하는 파생값';

-- S-532 구매내역 탭
CREATE TABLE license_purchase_record
(
    license_purchase_record_id  uuid                    NOT NULL,
    create_at                   timestamp without time zone,
    update_at                   timestamp without time zone,
    license_id                  uuid                    NOT NULL,
    vendor_id                   uuid,
    purchase_date               date                    NOT NULL,
    quantity                    integer                 NOT NULL,
    unit_price                  numeric(15,2),
    total_amount                numeric(15,2),
    memo                        character varying(500),
    CONSTRAINT license_purchase_record_pkey PRIMARY KEY (license_purchase_record_id),
    CONSTRAINT license_purchase_record_license_fk FOREIGN KEY (license_id) REFERENCES license (license_id),
    CONSTRAINT license_purchase_record_vendor_fk FOREIGN KEY (vendor_id) REFERENCES vendor (vendor_id)
);

COMMENT ON TABLE license_purchase_record IS 'S-532 구매내역 탭 - 구매 1건(수량 포함). "보유"는 이 테이블 quantity 합계';
COMMENT ON COLUMN license_purchase_record.vendor_id IS '공급사 - S-540과 공용. nullable(구매내역 없이 배정부터 하는 실무 허용, Q-47)';

-- S-532 사용자배정 탭(기본 탭). 회수는 삭제가 아니라 released_at을 채우는 소프트 릴리스 -
-- asset_assignment/loan과 동일한 append-only 이력 방식.
CREATE TABLE license_assigned_user
(
    license_assigned_user_id    uuid                    NOT NULL,
    create_at                   timestamp without time zone,
    update_at                   timestamp without time zone,
    license_id                  uuid                    NOT NULL,
    member_id                   uuid                    NOT NULL,
    license_purchase_record_id  uuid,
    assigned_at                 timestamp without time zone NOT NULL,
    assigned_by                 uuid                    NOT NULL,
    released_at                 timestamp without time zone,
    release_requested_yn        boolean                 NOT NULL DEFAULT false,
    release_requested_at        timestamp without time zone,
    CONSTRAINT license_assigned_user_pkey PRIMARY KEY (license_assigned_user_id),
    CONSTRAINT license_assigned_user_license_fk FOREIGN KEY (license_id) REFERENCES license (license_id),
    CONSTRAINT license_assigned_user_purchase_fk FOREIGN KEY (license_purchase_record_id) REFERENCES license_purchase_record (license_purchase_record_id)
);

COMMENT ON TABLE license_assigned_user IS 'S-532/533 사용자 배정 - "배정"은 released_at IS NULL 건수(파생값)';
COMMENT ON COLUMN license_assigned_user.license_purchase_record_id IS 'nullable - 미연결 배정(Q-47 "선배정 후구매" 실무 허용, 화면에 명시 표시)';
COMMENT ON COLUMN license_assigned_user.release_requested_yn IS 'S-550 임직원 자발적 회수 요청 - 서비스데스크 티켓 연동은 Phase 5 완료 후 연결 예정(현재는 요청 표시만)';

CREATE INDEX idx_license_assigned_user_license_id ON license_assigned_user (license_id);
CREATE INDEX idx_license_assigned_user_member_id ON license_assigned_user (member_id);

-- S-532 포함SW 탭 - software 마스터를 참조해 자동완성 일관성을 유지한다(수기 등록이지만 매번
-- 새 문자열을 만들지 않고 마스터에 없으면 그 자리에서 새로 만들어 참조한다).
CREATE TABLE license_included_software
(
    license_included_software_id  uuid  NOT NULL,
    create_at                     timestamp without time zone,
    update_at                     timestamp without time zone,
    license_id                    uuid  NOT NULL,
    software_id                   uuid  NOT NULL,
    CONSTRAINT license_included_software_pkey PRIMARY KEY (license_included_software_id),
    CONSTRAINT license_included_software_license_fk FOREIGN KEY (license_id) REFERENCES license (license_id),
    CONSTRAINT license_included_software_software_fk FOREIGN KEY (software_id) REFERENCES software (software_id),
    CONSTRAINT license_included_software_unique UNIQUE (license_id, software_id)
);

COMMENT ON TABLE license_included_software IS 'S-532 포함SW 탭 - 자산 배정과 무관한 참조 정보(예: Adobe CC → Photoshop, Illustrator)';
