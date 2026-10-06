-- S-410~412, S-420~421 대여 관리 (Step 6 Phase 3-2).
--
-- L1: 동시 대여 경쟁은 tangible_asset의 기존 @Version 낙관적 잠금(V11)을 그대로 재사용한다 -
-- LOANABLE -> ON_LOAN 전이도 그 잠금이 지키는 갱신 중 하나일 뿐이라 이 테이블에는 별도 잠금이
-- 필요 없다. loan은 "그 대여가 있었다"는 이력/신청 상태만 갖는다.
-- L2: 연체는 컬럼이 아니라 파생값(due_date < 오늘 AND returned_at IS NULL)이라 상태 컬럼에
-- OVERDUE는 없다.
-- L5: 반납 후에도 행을 지우지 않는다(append-only 이력).
CREATE TABLE loan
(
    loan_id            uuid                   NOT NULL,
    create_at          timestamp without time zone,
    update_at          timestamp without time zone,
    tangible_asset_id  uuid                   NOT NULL,
    member_id          uuid                   NOT NULL,
    status             character varying(20)  NOT NULL DEFAULT 'ACTIVE',
    due_date           date                   NOT NULL,
    extend_count       integer                NOT NULL DEFAULT 0,
    borrowed_at        timestamp without time zone NOT NULL,
    approved_by        uuid,
    approved_at        timestamp without time zone,
    reject_reason      character varying(500),
    returned_at        timestamp without time zone,
    return_condition   character varying(10),
    created_by         uuid                   NOT NULL,
    CONSTRAINT loan_pkey PRIMARY KEY (loan_id),
    CONSTRAINT loan_tangible_asset_fk FOREIGN KEY (tangible_asset_id) REFERENCES tangible_asset (tangible_asset_id)
);

COMMENT ON TABLE loan IS 'S-410~412,420~421 대여 1건 - QR 스캔(본인) 또는 관리자 대행(S-412)으로 생성, 반납 후에도 삭제하지 않음(L5)';
COMMENT ON COLUMN loan.member_id IS '대여자(임직원) - system 서비스 member, cross-DB라 FK 없음';
COMMENT ON COLUMN loan.status IS 'PENDING_APPROVAL(승인대기)/ACTIVE(대여중)/RETURNED(반납완료)/REJECTED(반려) - Q-39 옵션 OFF면 항상 ACTIVE로 시작';
COMMENT ON COLUMN loan.due_date IS '반납 기한 - Q-38 전역 기본값(process_config.default_loan_days)으로 산출, 연장 시 갱신';
COMMENT ON COLUMN loan.extend_count IS 'Q-41 연장 횟수 - process_config.max_extend_count 한도까지만 허용';
COMMENT ON COLUMN loan.borrowed_at IS 'QR 스캔(또는 관리자 대행 처리)으로 실제 대여가 시작된 시각';
COMMENT ON COLUMN loan.approved_by IS 'Q-39 옵션 ON일 때만 - S-411 승인 처리자';
COMMENT ON COLUMN loan.reject_reason IS 'S-411에서 반려한 경우의 사유';
COMMENT ON COLUMN loan.return_condition IS 'NORMAL(정상)/ABNORMAL(이상) - L3, ABNORMAL이면 자산이 REPAIR로 전환됨';
COMMENT ON COLUMN loan.created_by IS '대여를 실행한 주체 - 본인 QR 스캔이면 본인, 관리자 대행(S-412)이면 관리자';

CREATE INDEX idx_loan_member_id ON loan (member_id);
CREATE INDEX idx_loan_tangible_asset_id ON loan (tangible_asset_id);
-- L2(연체 파생값 계산)와 L4(연체 중 신규대여 차단), Q-42(동시대여 한도) 조회가 전부
-- "이 자산/이 사람의 현재 ACTIVE·PENDING_APPROVAL 건"을 찾는 패턴이라 부분 인덱스로 좁힌다.
CREATE INDEX idx_loan_open_by_member ON loan (member_id) WHERE status IN ('ACTIVE', 'PENDING_APPROVAL');
