-- V19에서 acknowledgement_approval을 "생성만 되고 갱신되지 않는 append-only 이력"이라 판단해
-- update_at 없이 만들었으나, AbstractEntity를 상속해 @UpdateTimestamp가 매핑되어 있으므로
-- Hibernate 스키마 검증(ddl-auto=validate)이 시작 시점에 실패한다 - V15(inventory_inspector 등)와
-- 동일한 실수. 모든 AbstractEntity 하위 테이블은 예외 없이 create_at/update_at 둘 다 있어야 한다.
ALTER TABLE acknowledgement_approval ADD COLUMN update_at timestamp without time zone;
