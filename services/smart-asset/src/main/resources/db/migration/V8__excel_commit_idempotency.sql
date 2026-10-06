-- S-213 엑셀 업서트 확정(commit)의 중복 처리를 막기 위한 멱등성 기록 테이블.
-- 클라이언트가 같은 요청을 재전송(더블클릭, 네트워크 재시도 등)해도 같은 commit_id면 한 번만
-- 실제로 처리되고, 이미 처리된 요청이면 그때 저장해 둔 결과를 그대로 돌려준다.
-- 여러 인스턴스·재시작 환경을 고려해 DB에 저장한다(애플리케이션 메모리 캐시에 의존하지 않음).
CREATE TABLE excel_commit_request
(
    commit_id         uuid NOT NULL,
    requested_by      uuid,
    status            character varying(20) NOT NULL, -- IN_PROGRESS | COMPLETED
    created_count     integer,
    updated_count     integer,
    skipped_count     integer,
    failed_rows_json  text,
    create_at         timestamp without time zone NOT NULL DEFAULT now(),
    update_at         timestamp without time zone NOT NULL DEFAULT now(),
    CONSTRAINT excel_commit_request_pkey PRIMARY KEY (commit_id)
);

COMMENT ON TABLE excel_commit_request IS 'S-213 엑셀 업서트 확정 요청 멱등성 처리 기록';
COMMENT ON COLUMN excel_commit_request.status IS 'IN_PROGRESS: 처리 중(동시 중복 요청 차단), COMPLETED: 처리 완료(결과 재사용)';
