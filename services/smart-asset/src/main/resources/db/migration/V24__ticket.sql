-- Step 7 Phase 5 - 서비스데스크 (S-600~603, S-610~611)
-- ticket_type: PURCHASE(신규구매) REPAIR(수리) REPLACE(교체) RETURN(회수) EXTEND(연장) DISPOSAL(폐기)
-- status: WAITING(접수대기) RECEIVED(접수) IN_PROGRESS(처리중) DONE(완료) - DONE은 완료 잠금(불변)
CREATE TABLE ticket (
    ticket_id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    ticket_type character varying(20) NOT NULL,
    title character varying(200) NOT NULL,
    content character varying(2000),
    status character varying(20) NOT NULL DEFAULT 'WAITING',
    requested_by uuid NOT NULL,
    assignee_id uuid,
    tangible_asset_id uuid,
    license_assigned_user_id uuid,
    created_asset_id uuid,
    target_due_date date,
    completed_at timestamp without time zone,
    CONSTRAINT ticket_pkey PRIMARY KEY (ticket_id),
    CONSTRAINT ticket_tangible_asset_fk FOREIGN KEY (tangible_asset_id) REFERENCES tangible_asset (tangible_asset_id),
    CONSTRAINT ticket_license_assigned_user_fk FOREIGN KEY (license_assigned_user_id) REFERENCES license_assigned_user (license_assigned_user_id),
    CONSTRAINT ticket_created_asset_fk FOREIGN KEY (created_asset_id) REFERENCES tangible_asset (tangible_asset_id)
);
CREATE INDEX idx_ticket_status ON ticket (status);
CREATE INDEX idx_ticket_requested_by ON ticket (requested_by);
CREATE INDEX idx_ticket_assignee_id ON ticket (assignee_id);

COMMENT ON TABLE ticket IS 'S-600~603 서비스데스크 티켓 - DONE 전환 후에는 완료 잠금(불변), 새 티켓으로 재처리';
COMMENT ON COLUMN ticket.created_asset_id IS 'PURCHASE 티켓 완료 시 자동등록된 자산(Q-49) - 다른 유형은 항상 NULL';
COMMENT ON COLUMN ticket.license_assigned_user_id IS 'RETURN 티켓이 라이선스 배정 회수 요청에서 자동 생성된 경우의 연결';

CREATE TABLE ticket_comment (
    ticket_comment_id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    ticket_id uuid NOT NULL,
    content character varying(1000) NOT NULL,
    written_by uuid NOT NULL,
    is_requester boolean NOT NULL DEFAULT false,
    CONSTRAINT ticket_comment_pkey PRIMARY KEY (ticket_comment_id),
    CONSTRAINT ticket_comment_ticket_fk FOREIGN KEY (ticket_id) REFERENCES ticket (ticket_id)
);
CREATE INDEX idx_ticket_comment_ticket_id ON ticket_comment (ticket_id);

COMMENT ON COLUMN ticket_comment.is_requester IS '요청자 본인 작성 코멘트인지 - 화면에서 배경색으로 구분(설계문서 S-602)';

-- Q-50: 수동 배정 + 미배정 경고. default_assignee_id는 참고용으로만 남기고 자동배정에는 쓰지 않는다.
-- Q-51: 유형별 목표일. Q-49: 자동등록은 PURCHASE만.
CREATE TABLE ticket_type_config (
    ticket_type_config_id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    ticket_type character varying(20) NOT NULL,
    default_assignee_id uuid,
    target_days integer,
    auto_create_asset_yn boolean NOT NULL DEFAULT false,
    CONSTRAINT ticket_type_config_pkey PRIMARY KEY (ticket_type_config_id),
    CONSTRAINT ticket_type_config_type_uk UNIQUE (ticket_type)
);

COMMENT ON COLUMN ticket_type_config.default_assignee_id IS 'Q-50 확정(수동배정)에 따라 자동배정에는 쓰지 않음 - 향후 참고용 예비 컬럼';

INSERT INTO ticket_type_config (ticket_type_config_id, create_at, update_at, ticket_type, target_days, auto_create_asset_yn) VALUES
    ('00000000-0000-0000-0000-000000000101', now(), now(), 'PURCHASE', 7, true),
    ('00000000-0000-0000-0000-000000000102', now(), now(), 'REPAIR', 3, false),
    ('00000000-0000-0000-0000-000000000103', now(), now(), 'REPLACE', 5, false),
    ('00000000-0000-0000-0000-000000000104', now(), now(), 'RETURN', 3, false),
    ('00000000-0000-0000-0000-000000000105', now(), now(), 'EXTEND', 3, false),
    ('00000000-0000-0000-0000-000000000106', now(), now(), 'DISPOSAL', 5, false);
