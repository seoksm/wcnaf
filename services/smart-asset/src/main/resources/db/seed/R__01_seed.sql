-- --------------------------------------------
-- 자산 종류 기본 4종 (편집 가능, 삭제 불가)
-- --------------------------------------------
INSERT INTO asset_category (asset_category_id, create_at, update_at, category_code, category_name, sort_seq, editable, memorandum_value, status) VALUES
    ('01900000-0000-7000-8000-000000000001', now(), now(), 'NOTEBOOK', '노트북', 1, false, 1000, 'ENABLE'),
    ('01900000-0000-7000-8000-000000000002', now(), now(), 'DESKTOP', '데스크탑PC', 2, false, 1000, 'ENABLE'),
    ('01900000-0000-7000-8000-000000000003', now(), now(), 'MONITOR', '모니터', 3, false, 1000, 'ENABLE'),
    ('01900000-0000-7000-8000-000000000004', now(), now(), 'ETC', '기타', 99, false, 1000, 'ENABLE')
ON CONFLICT (asset_category_id) DO NOTHING;

-- --------------------------------------------
-- 자산 위치 기본값
-- --------------------------------------------
INSERT INTO asset_location (asset_location_id, create_at, update_at, location_name, sort_seq, status) VALUES
    ('01900000-0000-7000-8000-000000000101', now(), now(), '본사', 1, 'ENABLE')
ON CONFLICT (asset_location_id) DO NOTHING;
