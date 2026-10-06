CREATE TABLE process_config (
    process_config_id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    loan_enabled boolean NOT NULL DEFAULT false,
    default_loan_days integer NOT NULL DEFAULT 7,
    require_approval boolean NOT NULL DEFAULT false,
    block_on_overdue boolean NOT NULL DEFAULT true,
    max_extend_count integer NOT NULL DEFAULT 1,
    concurrent_limit integer,
    acknowledgement_enabled boolean NOT NULL DEFAULT false,
    require_manager_approval boolean NOT NULL DEFAULT true,
    block_unapproved_view boolean NOT NULL DEFAULT true,
    auto_request_on_assign boolean NOT NULL DEFAULT false,
    approval_due_days integer NOT NULL DEFAULT 3,
    remind_interval_days integer NOT NULL DEFAULT 3,
    default_return_status character varying(20) NOT NULL DEFAULT 'STORAGE',
    CONSTRAINT process_config_pkey PRIMARY KEY (process_config_id)
);
COMMENT ON TABLE process_config IS '프로세스 설정 (S-400) - 대여/수령·반납 On-Off와 부속 정책. 워크스페이스당 단일 행(싱글턴)';
COMMENT ON COLUMN process_config.process_config_id IS '프로세스 설정 ID (싱글턴 고정값)';
COMMENT ON COLUMN process_config.create_at IS '생성일시';
COMMENT ON COLUMN process_config.update_at IS '수정일시';
COMMENT ON COLUMN process_config.loan_enabled IS '대여 프로세스 On/Off';
COMMENT ON COLUMN process_config.default_loan_days IS 'Q-38 전역 기본 대여 기한(일)';
COMMENT ON COLUMN process_config.require_approval IS 'Q-39 대여 시 관리자 승인 필요 여부 (false=QR 스캔 즉시 대여)';
COMMENT ON COLUMN process_config.block_on_overdue IS 'Q-40 연체 중 신규 대여 차단 여부';
COMMENT ON COLUMN process_config.max_extend_count IS 'Q-41 대여 연장 최대 횟수';
COMMENT ON COLUMN process_config.concurrent_limit IS 'Q-42 1인 동시 대여 한도 (NULL=제한 없음, 기본값)';
COMMENT ON COLUMN process_config.acknowledgement_enabled IS '수령·반납 승인 프로세스 On/Off';
COMMENT ON COLUMN process_config.require_manager_approval IS 'Q-43 반납 시 담당자 승인 필수 여부';
COMMENT ON COLUMN process_config.block_unapproved_view IS 'Q-44 미승인 자산 임직원 조회 차단 여부';
COMMENT ON COLUMN process_config.auto_request_on_assign IS 'Q-45 배정 시 확인서 자동 요청 여부 (false=수동 요청)';
COMMENT ON COLUMN process_config.approval_due_days IS '확인서 승인 기한(일) - 경과 시 S-431 재발송 대상';
COMMENT ON COLUMN process_config.remind_interval_days IS '확인서 리마인드 알림 재발송 주기(일)';
COMMENT ON COLUMN process_config.default_return_status IS '반납 승인 시 기본 제안 생애상태(TangibleAsset.LifeStatus) - 이상 시 담당자가 REPAIR로 변경';

INSERT INTO process_config (process_config_id, create_at, update_at)
VALUES ('00000000-0000-0000-0000-000000000001', now(), now());
