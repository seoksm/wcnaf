-- 최상위관리자(ADMIN) 그룹의 메뉴 권한(menu_permission)이 원인 불명으로 대부분 유실되어
-- (감가상각 화면 등록 중 발견 - 2026-09-17), "메뉴등록"·"권한관리"·"프로그램별 액션관리" 화면
-- 조차 접근 불가능해 스스로 복구할 방법이 없는 상태였다. R__01_seed.sql에 원래 부여돼 있던
-- 항목들을 그대로 복원한다. Repeatable(R__) 마이그레이션은 기존 데이터와 충돌 시 파일 전체가
-- 실패할 위험이 있어(PK 중복 등 idempotent 처리가 안 돼 있음), 여기서는 안전하게 이미 존재하는
-- (그룹, 메뉴) 조합은 건너뛰고, 대상 메뉴가 실제로 존재하는 경우에만 삽입하는 1회성 버전 마이그레이션으로 처리한다.

INSERT INTO menu_permission (id, create_at, update_at, custom1status, custom2status, custom3status,
                              delete_status, down_status, insert_status, manage_status, print_status,
                              select_status, update_status, authorization_group_id, menu_id)
SELECT gen_random_uuid(), now(), now(), 'ALLOW', 'ALLOW', 'ALLOW',
       'ALLOW', 'ALLOW', 'ALLOW', 'ALLOW', 'ALLOW',
       'ALLOW', 'ALLOW', '019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid, target.menu_id
FROM (VALUES
    ('0194cee4-1c36-7200-b2ab-bc7bea1d9c15'::uuid),
    ('0194cf30-6822-7009-8a8c-ee7aadefb9bf'::uuid),
    ('0194cf30-680c-7b08-96c1-54f72ad2fd85'::uuid),
    ('01956e77-b818-7ffe-a6d8-8e2d405cff55'::uuid),
    ('01951d1b-2ecc-7ffb-90b9-932022d26811'::uuid),
    ('01961877-3b1e-7ffb-8a8b-5df2f1d0a543'::uuid),
    ('0194cf50-32af-720b-99ad-84e57a5568b0'::uuid),
    ('01951d1b-2eca-7ff9-b640-48294e8a87af'::uuid),
    ('0194f42b-e435-7416-aabc-049e66e74adb'::uuid),
    ('01951d1b-2eca-7ffa-912b-0fefcebbf17a'::uuid), -- 권한관리
    ('01954ae5-1205-7603-b9fe-e0ae2b2871cc'::uuid),
    ('019535a2-f209-7d09-8ace-6b8889249192'::uuid),
    ('01955fb4-9417-7a21-870d-599df6229b86'::uuid), -- 프로그램별 액션관리
    ('0195655f-0499-7ff8-8dab-e2e9dca051c4'::uuid), -- 메인
    ('01955fb4-9464-7d22-aec6-becd977dd1ce'::uuid), -- 메뉴등록
    ('01959336-4e5c-7e0f-9328-dda718538a18'::uuid),
    ('0195932e-d471-720b-a111-80ed7f263b71'::uuid),
    ('019db82d-617a-7ffd-9259-2e606c2fd7ca'::uuid),
    ('019db915-aea3-7ff9-adcf-e94196ba86ba'::uuid)
) AS target(menu_id)
WHERE EXISTS (SELECT 1 FROM menu m WHERE m.id = target.menu_id)
  AND NOT EXISTS (
      SELECT 1 FROM menu_permission mp
      WHERE mp.authorization_group_id = '019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid
        AND mp.menu_id = target.menu_id
  );
