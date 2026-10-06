-- S-213 엑셀 확정(commit)의 요청 레벨 멱등성을 강화한다.
-- 기존에는 commitId 하나만으로 멱등성을 보장했는데, 다음 문제가 있었다.
-- 1) 같은 commitId라도 클라이언트가 다른 내용(행 목록)을 보내면 그대로 예전 결과를 돌려주거나
--    뒤섞여 처리될 위험이 있었다 - request_hash로 "같은 내용의 요청인지"까지 확인한다.
-- 2) 프로세스가 IN_PROGRESS 상태로 죽으면 그 commitId가 영구히 재시도 불가능해졌다 - lease_expires_at로
--    유효 기간을 두고, 지나면 안전하게 재claim(재시도)할 수 있게 한다.
-- 3) 재시도/장애 이력을 추적할 방법이 없었다 - attempt_count, last_error를 추가한다.
ALTER TABLE excel_commit_request
    ADD COLUMN request_hash character varying(64),
    ADD COLUMN lease_expires_at timestamp without time zone,
    ADD COLUMN attempt_count integer NOT NULL DEFAULT 1,
    ADD COLUMN last_error text;

COMMENT ON COLUMN excel_commit_request.status IS
    'IN_PROGRESS: 처리 중(유효한 lease 동안 동일 요청 중복 실행 차단), COMPLETED: 처리 완료(결과 재사용), FAILED: 요청 레벨에서 예상 못한 예외로 중단(재claim 가능)';
COMMENT ON COLUMN excel_commit_request.request_hash IS
    '요청 내용(행 목록의 rowNum+서명)에 대한 SHA-256 지문 - 같은 commitId라도 이 값이 다르면 다른 내용의 요청으로 간주해 거부한다';
COMMENT ON COLUMN excel_commit_request.lease_expires_at IS
    'IN_PROGRESS 상태의 유효 기한 - 이 시각이 지나면(처리 프로세스가 죽은 것으로 간주) 다른 시도가 안전하게 재claim할 수 있다';
COMMENT ON COLUMN excel_commit_request.attempt_count IS '이 commitId에 대한 처리 시도 횟수(최초 1)';
COMMENT ON COLUMN excel_commit_request.last_error IS '가장 최근 요청 레벨 실패 사유 (status=FAILED일 때)';

-- 행 단위 멱등성 - commitId 재시도 시 이미 끝난 행(성공/실패 모두)은 다시 반영하지 않고 그 결과를
-- 그대로 재사용한다. row_key는 행번호만으로는 재요청 시 내용이 바뀐 행을 구분할 수 없으므로
-- 행번호와 서명 해시를 함께 사용한다. 자산 저장과 이 표의 완료 기록은 ExcelCommitRowExecutor의
-- 같은 REQUIRES_NEW 트랜잭션 안에서 함께 커밋되거나 함께 롤백된다(프로세스가 중간에 죽어도 두 상태가
-- 어긋나지 않는다).
CREATE TABLE excel_commit_row
(
    commit_id         uuid                   NOT NULL,
    row_key           character varying(128) NOT NULL,
    row_num           integer                NOT NULL,
    status            character varying(20)  NOT NULL, -- COMPLETED | FAILED
    result_action     character varying(20),            -- CREATED | UPDATED (status=COMPLETED일 때)
    tangible_asset_id uuid,
    error_message     text,
    create_at         timestamp without time zone NOT NULL DEFAULT now(),
    update_at         timestamp without time zone NOT NULL DEFAULT now(),
    CONSTRAINT excel_commit_row_pkey PRIMARY KEY (commit_id, row_key),
    CONSTRAINT excel_commit_row_request_fk FOREIGN KEY (commit_id) REFERENCES excel_commit_request (commit_id)
);

COMMENT ON TABLE excel_commit_row IS 'S-213 엑셀 확정의 행 단위 멱등성 - 같은 commitId를 재시도해도 이미 끝난 행은 다시 반영하지 않는다';
COMMENT ON COLUMN excel_commit_row.row_key IS '행번호 + 서명 해시 - 행번호만으로는 재요청 시 내용이 바뀐 행을 구분할 수 없어 서명 해시를 함께 쓴다';
COMMENT ON COLUMN excel_commit_row.status IS 'COMPLETED: 자산 반영 성공, FAILED: 이 행은 반영하지 못함(영구, 재시도해도 재사용됨)';
COMMENT ON COLUMN excel_commit_row.result_action IS 'CREATED | UPDATED - status=COMPLETED일 때만 값이 있다';
