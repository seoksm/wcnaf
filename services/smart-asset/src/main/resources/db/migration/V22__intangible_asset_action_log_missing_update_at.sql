-- V20(acknowledgement_approval)과 동일한 실수를 V21(intangible_asset_action_log)에서 또 반복했다.
-- append-only 이력 테이블도 AbstractEntity를 상속하는 한 예외 없이 update_at이 필요하다.
ALTER TABLE intangible_asset_action_log ADD COLUMN update_at timestamp without time zone;
