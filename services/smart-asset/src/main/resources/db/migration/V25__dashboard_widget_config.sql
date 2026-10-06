-- S-701 대시보드 설정 (Step 8 Phase 6).
--
-- 위젯 표시여부·순서는 개인별로 다르다(총무팀·IT팀이 보고 싶은 위젯이 다름) - 이 테이블은
-- "그 사람이 무엇을 보고 싶어하는가"만 저장하고, 실제 위젯 데이터(집계값)는 저장하지 않는다.
-- 행이 없는 위젯키는 기본값(표시함, 설계문서 정의 순서)으로 취급한다 - 신규 관리자는 별도
-- 초기화 없이 전체 ON 상태로 보인다(§2 "기본값" 규칙).
CREATE TABLE dashboard_widget_config
(
    dashboard_widget_config_id  uuid                   NOT NULL,
    create_at                   timestamp without time zone,
    update_at                   timestamp without time zone,
    member_id                   uuid                   NOT NULL,
    widget_key                  character varying(30)  NOT NULL,
    visible                     boolean                NOT NULL DEFAULT true,
    sort_order                  integer                NOT NULL DEFAULT 0,
    CONSTRAINT dashboard_widget_config_pkey PRIMARY KEY (dashboard_widget_config_id),
    CONSTRAINT dashboard_widget_config_unique UNIQUE (member_id, widget_key)
);

COMMENT ON TABLE dashboard_widget_config IS 'S-701 대시보드 설정 - 개인별 위젯 표시여부·순서(설계문서 §2)';
COMMENT ON COLUMN dashboard_widget_config.member_id IS '설정 소유자 - system 서비스 member, cross-DB라 FK 없음';
COMMENT ON COLUMN dashboard_widget_config.widget_key IS 'ASSET_SUMMARY/CATEGORY_DISTRIBUTION/LOCATION_DISTRIBUTION/STATUS_DISTRIBUTION/MONTHLY_COST_TREND/EXPIRING_SOON/INVENTORY_PROGRESS/TICKET_STATUS/LOAN_STATUS/LICENSE_CONSISTENCY';
COMMENT ON COLUMN dashboard_widget_config.visible IS '이 위젯을 대시보드에 표시할지 여부';
COMMENT ON COLUMN dashboard_widget_config.sort_order IS '표시 순서(오름차순) - 같은 값이면 widget_key 순';

CREATE INDEX idx_dashboard_widget_config_member ON dashboard_widget_config (member_id);
