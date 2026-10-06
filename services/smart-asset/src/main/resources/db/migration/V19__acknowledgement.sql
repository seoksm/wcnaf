-- S-430~433,440 수령·반납 승인 (Step 6 Phase 3-3).
--
-- K1: acknowledgement.body_snapshot은 요청 시점에 ack_template.body_tpl을 변수 치환해 만든
-- 완성된 문서 전문이다 - 템플릿이 나중에 바뀌어도 이미 요청된 건은 이 스냅샷을 그대로 유지한다
-- (ack_template 자체를 FK로 참조하지 않음, K7).
-- K2/K3: acknowledgement_approval은 승인 단계(임직원/담당자)마다 새 행을 추가하는
-- append-only 증빙이다 - 수정·삭제 API를 두지 않는다.
CREATE TABLE ack_template
(
    ack_template_id  uuid                    NOT NULL,
    create_at        timestamp without time zone,
    update_at        timestamp without time zone,
    type             character varying(20)  NOT NULL,
    body_tpl         character varying(2000) NOT NULL,
    updated_by       uuid,
    CONSTRAINT ack_template_pkey PRIMARY KEY (ack_template_id),
    CONSTRAINT ack_template_type_uk UNIQUE (type)
);

COMMENT ON TABLE ack_template IS 'S-433 확인서 문구 - RECEIPT(수령)/RETURN(반납) 2종 고정. 편집은 이후 요청에만 적용(K7)';
COMMENT ON COLUMN ack_template.type IS 'RECEIPT(수령용)/RETURN(반납용)';
COMMENT ON COLUMN ack_template.body_tpl IS '{{자산명}}과 같은 변수 치환 문구 - 자산명/자산코드/취득가액/지급일/담당자/대상자';

CREATE TABLE acknowledgement
(
    acknowledgement_id  uuid                    NOT NULL,
    create_at           timestamp without time zone,
    update_at           timestamp without time zone,
    tangible_asset_id   uuid                    NOT NULL,
    member_id           uuid                    NOT NULL,
    type                character varying(20)  NOT NULL,
    body_snapshot       character varying(4000) NOT NULL,
    status              character varying(20)  NOT NULL DEFAULT 'PENDING_EMPLOYEE',
    due_date            date,
    requested_by        uuid                    NOT NULL,
    requested_at        timestamp without time zone NOT NULL,
    cancelled_yn        boolean                 NOT NULL DEFAULT false,
    cancelled_reason    character varying(500),
    CONSTRAINT acknowledgement_pkey PRIMARY KEY (acknowledgement_id),
    CONSTRAINT acknowledgement_tangible_asset_fk FOREIGN KEY (tangible_asset_id) REFERENCES tangible_asset (tangible_asset_id)
);

COMMENT ON TABLE acknowledgement IS 'S-430~432,440 확인서 요청 1건 - S-431/432/440이 함께 참조';
COMMENT ON COLUMN acknowledgement.member_id IS '대상자(임직원) - system 서비스 member, cross-DB라 FK 없음';
COMMENT ON COLUMN acknowledgement.type IS 'RECEIPT(수령확인서, 1단 승인)/RETURN(반납확인서, 옵션에 따라 담당자 2단 승인·K5)';
COMMENT ON COLUMN acknowledgement.body_snapshot IS 'K1 - 요청 시점 ack_template 렌더링 결과 전문. 템플릿이 나중에 바뀌어도 유지';
COMMENT ON COLUMN acknowledgement.status IS 'PENDING_EMPLOYEE/PENDING_MANAGER(RETURN+담당자승인옵션 ON일 때만)/COMPLETED/CANCELLED';
COMMENT ON COLUMN acknowledgement.due_date IS '승인 기한 - process_config.approval_due_days로 산출';
COMMENT ON COLUMN acknowledgement.cancelled_yn IS 'K3 무효화 - 승인 완료 전 요청 자체를 취소한 경우';

CREATE INDEX idx_acknowledgement_member_id ON acknowledgement (member_id);
CREATE INDEX idx_acknowledgement_tangible_asset_id ON acknowledgement (tangible_asset_id);

CREATE TABLE acknowledgement_approval
(
    acknowledgement_approval_id  uuid                    NOT NULL,
    create_at                    timestamp without time zone,
    acknowledgement_id           uuid                    NOT NULL,
    approval_step                character varying(20)  NOT NULL,
    approved_by                  uuid                    NOT NULL,
    approved_at                  timestamp without time zone NOT NULL,
    approver_ip                  character varying(45),
    asset_snapshot               character varying(2000),
    return_condition             character varying(10),
    next_life_status             character varying(20),
    next_assign_type             character varying(20),
    CONSTRAINT acknowledgement_approval_pkey PRIMARY KEY (acknowledgement_approval_id),
    CONSTRAINT acknowledgement_approval_ack_fk FOREIGN KEY (acknowledgement_id) REFERENCES acknowledgement (acknowledgement_id)
);

COMMENT ON TABLE acknowledgement_approval IS 'K2/K3 - 승인 단계(임직원/담당자)마다 쌓이는 append-only 증빙. 수정·삭제 없음';
COMMENT ON COLUMN acknowledgement_approval.approval_step IS 'EMPLOYEE(임직원 승인, K4 2단계 확인)/MANAGER(담당자 승인, RETURN만)';
COMMENT ON COLUMN acknowledgement_approval.approver_ip IS 'K2 증빙 - 접속 IP (WiniCom.getClientIp)';
COMMENT ON COLUMN acknowledgement_approval.asset_snapshot IS 'K2 증빙 - 승인 시점 자산 상태 요약(사람이 읽는 문자열, 재파싱하지 않음)';
COMMENT ON COLUMN acknowledgement_approval.return_condition IS 'NORMAL(정상)/ABNORMAL(이상) - MANAGER 단계(RETURN)에서만';
COMMENT ON COLUMN acknowledgement_approval.next_life_status IS '담당자가 승인과 동시에 지정한 반납 후 생애상태 - MANAGER 단계에서만';
COMMENT ON COLUMN acknowledgement_approval.next_assign_type IS '담당자가 승인과 동시에 지정한 반납 후 배정형태 - MANAGER 단계에서만';

CREATE INDEX idx_acknowledgement_approval_ack_id ON acknowledgement_approval (acknowledgement_id);

-- S-433 기본 문구 2종 시드 - 관리자가 나중에 수정할 수 있게 빈 값이 아닌 실사용 가능한 기본값을 둔다.
INSERT INTO ack_template (ack_template_id, create_at, update_at, type, body_tpl)
VALUES
    ('00000000-0000-0000-0000-000000000101', now(), now(), 'RECEIPT',
     '본인은 아래 자산을 정상적으로 수령하였음을 확인합니다.' || chr(10) ||
     '자산명: {{자산명}} ({{자산코드}})' || chr(10) ||
     '취득가액: {{취득가액}}원' || chr(10) ||
     '지급일: {{지급일}}' || chr(10) ||
     '담당자: {{담당자}}' || chr(10) ||
     '대상자: {{대상자}}'),
    ('00000000-0000-0000-0000-000000000102', now(), now(), 'RETURN',
     '본인은 아래 자산을 반납하였음을 확인합니다.' || chr(10) ||
     '자산명: {{자산명}} ({{자산코드}})' || chr(10) ||
     '취득가액: {{취득가액}}원' || chr(10) ||
     '지급일: {{지급일}}' || chr(10) ||
     '담당자: {{담당자}}' || chr(10) ||
     '대상자: {{대상자}}');
