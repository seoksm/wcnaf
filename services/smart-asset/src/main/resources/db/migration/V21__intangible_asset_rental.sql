-- S-500~502 무형자산, S-510~512 렌탈·구독 (Step 7 Phase 4-1).
--
-- Q-46: 무형자산과 라이선스는 분리한다 - 무형자산은 도메인·인증서·상표권, SW는 라이선스만.
-- 이 테이블에 SW 라이선스를 넣지 않는다(이중 관리 방지).
CREATE TABLE intangible_asset
(
    intangible_asset_id  uuid                    NOT NULL,
    create_at            timestamp without time zone,
    update_at            timestamp without time zone,
    intangible_type      character varying(20)   NOT NULL,
    name                 character varying(200)  NOT NULL,
    issuer               character varying(200),
    registered_date      date,
    expiry_date          date                    NOT NULL,
    owner_member_id      uuid,
    alert_days           character varying(50)   NOT NULL DEFAULT '30,7,3,1',
    memo                 character varying(1000),
    status               character varying(10)   NOT NULL DEFAULT 'ENABLE',
    CONSTRAINT intangible_asset_pkey PRIMARY KEY (intangible_asset_id)
);

COMMENT ON TABLE intangible_asset IS 'S-500~502 무형자산 - 도메인·인증서·상표권 (Q-46, SW 라이선스 제외)';
COMMENT ON COLUMN intangible_asset.intangible_type IS 'DOMAIN(도메인)/CERTIFICATE(인증서)/TRADEMARK(상표권)/OTHER(기타)';
COMMENT ON COLUMN intangible_asset.issuer IS '발급·등록 기관';
COMMENT ON COLUMN intangible_asset.owner_member_id IS '담당자 - system 서비스 member, cross-DB라 FK 없음';
COMMENT ON COLUMN intangible_asset.alert_days IS '만료 알림 시점 CSV(예: 30,7,3,1) - 실제 발송은 알림 인프라 부재로 미구현, 목록 압축 표기용';
COMMENT ON COLUMN intangible_asset.status IS 'ENABLE/DISABLE - 소프트 삭제';

CREATE INDEX idx_intangible_asset_expiry_date ON intangible_asset (expiry_date);

-- S-502 상세 - 갱신 이력. "만료되면 갱신 조치로 만료일이 밀려 정상으로 돌아간다"(설계문서 §2)의
-- 근거 기록. append-only.
CREATE TABLE intangible_asset_action_log
(
    intangible_asset_action_log_id  uuid                   NOT NULL,
    create_at                       timestamp without time zone,
    intangible_asset_id             uuid                   NOT NULL,
    action_type                     character varying(20)  NOT NULL,
    previous_expiry_date            date,
    new_expiry_date                 date,
    note                            character varying(500),
    acted_by                        uuid                   NOT NULL,
    acted_at                        timestamp without time zone NOT NULL,
    CONSTRAINT intangible_asset_action_log_pkey PRIMARY KEY (intangible_asset_action_log_id),
    CONSTRAINT intangible_asset_action_log_fk FOREIGN KEY (intangible_asset_id) REFERENCES intangible_asset (intangible_asset_id)
);

COMMENT ON TABLE intangible_asset_action_log IS 'S-502 갱신 이력 - append-only';
COMMENT ON COLUMN intangible_asset_action_log.action_type IS 'RENEW(갱신)';

CREATE INDEX idx_intangible_asset_action_log_asset_id ON intangible_asset_action_log (intangible_asset_id);

-- S-510~512 렌탈·구독
CREATE TABLE rental_asset
(
    rental_asset_id  uuid                    NOT NULL,
    create_at        timestamp without time zone,
    update_at        timestamp without time zone,
    name             character varying(200) NOT NULL,
    billing_mode     character varying(20)  NOT NULL DEFAULT 'FIXED',
    start_date       date                   NOT NULL,
    end_date         date,
    status           character varying(20)  NOT NULL DEFAULT 'ACTIVE',
    memo             character varying(1000),
    CONSTRAINT rental_asset_pkey PRIMARY KEY (rental_asset_id)
);

COMMENT ON TABLE rental_asset IS 'S-510~512 렌탈·구독 1건';
COMMENT ON COLUMN rental_asset.billing_mode IS 'FIXED(고정액)/USAGE_BASED(사용량기반, 매월 금액이 다름 - AWS 등)';
COMMENT ON COLUMN rental_asset.status IS 'ACTIVE(구독중)/CANCELLED(해지) - 해지는 삭제가 아니다, 지출 이력 보존';

-- S-512 결제 스케줄 탭. Q-53 - 예상액·실제액을 분리 입력한다(사용량 기반 계약이 매월 다르므로).
-- accrual_month(귀속 월) ≠ due_date(결제일) - 8월 사용분이 9월 1일 결제되는 경우를 표현.
CREATE TABLE payment_schedule
(
    payment_schedule_id  uuid                    NOT NULL,
    create_at            timestamp without time zone,
    update_at            timestamp without time zone,
    rental_asset_id      uuid                    NOT NULL,
    accrual_month        date                    NOT NULL,
    due_date             date,
    expected_amount      numeric(15,2),
    actual_amount        numeric(15,2),
    confirmed_yn         boolean                 NOT NULL DEFAULT false,
    CONSTRAINT payment_schedule_pkey PRIMARY KEY (payment_schedule_id),
    CONSTRAINT payment_schedule_rental_fk FOREIGN KEY (rental_asset_id) REFERENCES rental_asset (rental_asset_id),
    CONSTRAINT payment_schedule_unique UNIQUE (rental_asset_id, accrual_month)
);

COMMENT ON TABLE payment_schedule IS 'S-512 결제 스케줄 - 귀속월 1건당 1행';
COMMENT ON COLUMN payment_schedule.accrual_month IS '귀속 월(그 달 1일로 저장) - due_date(결제일)와 분리(설계문서 §2)';
COMMENT ON COLUMN payment_schedule.confirmed_yn IS 'true가 되면 actual_amount가 확정되어 expense_record에 반영됨';

-- Q-52 - 렌탈·라이선스가 공용으로 쓰는 지출 원장(대시보드 비용 집계용, Step 8에서 소비 예정).
-- src_type/src_id는 폴리모픽 참조라 FK를 걸지 않는다(양쪽 테이블 타입이 다름).
CREATE TABLE expense_record
(
    expense_record_id  uuid                    NOT NULL,
    create_at          timestamp without time zone,
    update_at          timestamp without time zone,
    src_type           character varying(20)  NOT NULL,
    src_id             uuid                    NOT NULL,
    accrual_month      date                    NOT NULL,
    amount_type        character varying(20)  NOT NULL,
    amount             numeric(15,2)           NOT NULL,
    memo               character varying(500),
    CONSTRAINT expense_record_pkey PRIMARY KEY (expense_record_id)
);

COMMENT ON TABLE expense_record IS 'Q-52 렌탈·라이선스 공용 지출 원장 - src_type(RENTAL/LICENSE)+src_id로 폴리모픽 참조';
COMMENT ON COLUMN expense_record.amount_type IS 'EXPECTED(예상)/ACTUAL(실제)';

CREATE INDEX idx_expense_record_src ON expense_record (src_type, src_id);
CREATE INDEX idx_expense_record_accrual_month ON expense_record (accrual_month);
