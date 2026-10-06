-- V14에서 inventory_inspector/inventory_excluded_member를 "생성만 되고 갱신되지 않는 단순 join
-- 테이블"이라 판단해 update_at 없이 만들었으나, 두 엔티티 모두 AbstractEntity를 상속해
-- @UpdateTimestamp가 매핑되어 있으므로 Hibernate 스키마 검증(ddl-auto=validate)이 시작 시점에
-- 실패한다 - 모든 AbstractEntity 하위 테이블은 예외 없이 create_at/update_at 둘 다 있어야 한다.
ALTER TABLE inventory_inspector ADD COLUMN update_at timestamp without time zone;
ALTER TABLE inventory_excluded_member ADD COLUMN update_at timestamp without time zone;
