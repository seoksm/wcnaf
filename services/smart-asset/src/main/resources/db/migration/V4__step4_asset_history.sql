CREATE TABLE asset_history (
    asset_history_id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    tangible_asset_id uuid NOT NULL,
    history_type character varying(20) NOT NULL,
    changed_fields text,
    snapshot text,
    batch_id uuid,
    created_by uuid,
    CONSTRAINT asset_history_pkey PRIMARY KEY (asset_history_id),
    CONSTRAINT asset_history_tangible_asset_fk FOREIGN KEY (tangible_asset_id) REFERENCES tangible_asset(tangible_asset_id)
);
COMMENT ON TABLE asset_history IS '자산 히스토리 - append-only, 변경 필드만 저장 (S-220/221, Step4 §2)';
COMMENT ON COLUMN asset_history.asset_history_id IS '히스토리 ID';
COMMENT ON COLUMN asset_history.create_at IS '생성일시';
COMMENT ON COLUMN asset_history.update_at IS '수정일시 (append-only이므로 사실상 create_at과 동일)';
COMMENT ON COLUMN asset_history.tangible_asset_id IS '유형자산 ID';
COMMENT ON COLUMN asset_history.history_type IS '이력 유형 (REGISTER/MODIFY/STATUS_CHANGE/ASSIGNMENT/INVENTORY/LOAN/ACKNOWLEDGEMENT/DISUSE) - 뒤 3종은 Step5~6 구현 시 사용';
COMMENT ON COLUMN asset_history.changed_fields IS '변경 필드 목록 JSON [{field,before,after}] - 전체 스냅샷 아님';
COMMENT ON COLUMN asset_history.snapshot IS '전체 상태 스냅샷 JSON - 배정/상태변경 등 감사 필요 유형만 채움 (Q-23)';
COMMENT ON COLUMN asset_history.batch_id IS '일괄 처리 배치 ID - 엑셀 업서트/일괄변경 시 같은 실행 건을 공유, 단건 처리는 NULL';
COMMENT ON COLUMN asset_history.created_by IS '처리자 ID (common_user 참조)';

CREATE INDEX idx_asset_history_tangible_asset ON asset_history (tangible_asset_id, create_at DESC);
CREATE INDEX idx_asset_history_batch ON asset_history (batch_id) WHERE batch_id IS NOT NULL;
CREATE INDEX idx_asset_history_create_at ON asset_history (create_at DESC);
