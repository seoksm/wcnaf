-- api-gateway가 인가 판단에 쓰는 로컬 복제 테이블(common_authorization_group_permission,
-- common_menu_action)이 CDC(Kafka) 로만 채워지는데, 원인 불명으로 대부분 유실된 상태였다
-- (system 서비스 쪽은 V2__restore_admin_menu_permission.sql 로 원본 menu_permission을 복구함).
-- 이 마이그레이션은 원본이 복구된 것과 동일한 최상위관리자(ADMIN) 권한 데이터를 게이트웨이
-- 로컬 캐시에도 직접 반영해 "권한관리"·"메뉴등록"·"프로그램별 액션관리" 화면 접근을 복구한다.
-- 복구 후에는 실제 화면(권한관리 > 전체 동기화)에서 syncAll을 한 번 실행해 나머지 메뉴까지
-- 정식 CDC 경로로 재동기화하는 것을 권장한다 - 이 마이그레이션은 부트스트랩 탈출용 최소 조치다.

INSERT INTO common_authorization_group_permission (authorization_group_id, menu_id, create_at, update_at,
    custom1status, custom2status, custom3status, delete_status, down_status, group_code,
    insert_status, manage_status, print_status, select_status, update_status)
SELECT '019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid, target.menu_id, now(), now(),
       target.c1, target.c2, target.c3, target.del, target.down, 'ADMIN',
       target.ins, target.manage, target.print, target.sel, target.upd
FROM (VALUES
    ('01a0a985-e5f8-7a5b-90ef-c050af1226f5'::uuid, 'NONE','NONE','NONE','NONE','NONE','NONE','NONE','NONE','ALLOW','ALLOW'),
    ('0194cee4-1c36-7200-b2ab-bc7bea1d9c15'::uuid, 'ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW'),
    ('0194cf30-6822-7009-8a8c-ee7aadefb9bf'::uuid, 'ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW'),
    ('0194cf30-680c-7b08-96c1-54f72ad2fd85'::uuid, 'ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW'),
    ('01956e77-b818-7ffe-a6d8-8e2d405cff55'::uuid, 'ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW'),
    ('01951d1b-2ecc-7ffb-90b9-932022d26811'::uuid, 'ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW'),
    ('01961877-3b1e-7ffb-8a8b-5df2f1d0a543'::uuid, 'ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW'),
    ('0194cf50-32af-720b-99ad-84e57a5568b0'::uuid, 'ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW'),
    ('01951d1b-2eca-7ff9-b640-48294e8a87af'::uuid, 'ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW'),
    ('0194f42b-e435-7416-aabc-049e66e74adb'::uuid, 'ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW'),
    ('01951d1b-2eca-7ffa-912b-0fefcebbf17a'::uuid, 'ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW'), -- 권한관리
    ('01954ae5-1205-7603-b9fe-e0ae2b2871cc'::uuid, 'ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW'),
    ('019535a2-f209-7d09-8ace-6b8889249192'::uuid, 'ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW'),
    ('01955fb4-9417-7a21-870d-599df6229b86'::uuid, 'ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW'), -- 프로그램별 액션관리
    ('0195655f-0499-7ff8-8dab-e2e9dca051c4'::uuid, 'ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW'),
    ('01955fb4-9464-7d22-aec6-becd977dd1ce'::uuid, 'ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW'), -- 메뉴등록
    ('01959336-4e5c-7e0f-9328-dda718538a18'::uuid, 'ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW'),
    ('0195932e-d471-720b-a111-80ed7f263b71'::uuid, 'ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW'),
    ('019db82d-617a-7ffd-9259-2e606c2fd7ca'::uuid, 'ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW'),
    ('019db915-aea3-7ff9-adcf-e94196ba86ba'::uuid, 'ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW')
) AS target(menu_id, c1, c2, c3, del, down, ins, manage, print, sel, upd)
WHERE NOT EXISTS (
    SELECT 1 FROM common_authorization_group_permission cap
    WHERE cap.authorization_group_id = '019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid
      AND cap.menu_id = target.menu_id
);

-- "메뉴등록"·"권한관리"·"프로그램별 액션관리" 3개 화면의 program_action 매핑 복구
-- (system DB의 program_action + menu 조인 결과를 그대로 반영, id는 원본 program_action.id 그대로 사용)
INSERT INTO common_menu_action (id, create_at, update_at, action_type, auth_type, menu_id, program_code, program_id, uri)
SELECT target.id, now(), now(), 'RESTAPI', target.auth_type, target.menu_id, target.program_code, target.program_id, target.uri
FROM (VALUES
    ('01955eec-f120-7c39-9825-5708be0ef1b8'::uuid, 'SELECT', '01951d1b-2eca-7ffa-912b-0fefcebbf17a'::uuid, 'SYS_AUTH_MNG', '01951d1a-5301-7ff8-bd3d-b19f997179d6'::uuid, 'system/authorization-group'),
    ('01955eec-f118-7036-b849-fe1cab3cbfb9'::uuid, 'INSERT', '01951d1b-2eca-7ffa-912b-0fefcebbf17a'::uuid, 'SYS_AUTH_MNG', '01951d1a-5301-7ff8-bd3d-b19f997179d6'::uuid, 'system/authorization-group'),
    ('01955eec-f117-7935-b500-e6c8ce3a9975'::uuid, 'UPDATE', '01951d1b-2eca-7ffa-912b-0fefcebbf17a'::uuid, 'SYS_AUTH_MNG', '01951d1a-5301-7ff8-bd3d-b19f997179d6'::uuid, 'system/authorization-group/*'),
    ('01955eec-f118-7f37-b230-b539509e1668'::uuid, 'DELETE', '01951d1b-2eca-7ffa-912b-0fefcebbf17a'::uuid, 'SYS_AUTH_MNG', '01951d1a-5301-7ff8-bd3d-b19f997179d6'::uuid, 'system/authorization-group/*'),
    ('01955eec-f121-783a-921d-59a7731030f5'::uuid, 'INSERT', '01951d1b-2eca-7ffa-912b-0fefcebbf17a'::uuid, 'SYS_AUTH_MNG', '01951d1a-5301-7ff8-bd3d-b19f997179d6'::uuid, 'system/authorization-group/*/menu-permission/batch'),
    ('01a0a2a9-ed8d-7714-9b5d-82231731f387'::uuid, 'SELECT', '01951d1b-2eca-7ffa-912b-0fefcebbf17a'::uuid, 'SYS_AUTH_MNG', '01951d1a-5301-7ff8-bd3d-b19f997179d6'::uuid, 'system/authorization-group/menu-permission/syncAll'),
    ('01955eec-f11f-7538-a3a3-ece677c2988b'::uuid, 'SELECT', '01951d1b-2eca-7ffa-912b-0fefcebbf17a'::uuid, 'SYS_AUTH_MNG', '01951d1a-5301-7ff8-bd3d-b19f997179d6'::uuid, 'system/authorization-group/*/menu-permission/tree-list'),
    ('01955eec-f125-7e3b-9a08-91421bcffba0'::uuid, 'SELECT', '01951d1b-2eca-7ffa-912b-0fefcebbf17a'::uuid, 'SYS_AUTH_MNG', '01951d1a-5301-7ff8-bd3d-b19f997179d6'::uuid, 'system/authorization-group/tree'),
    ('01954128-e2de-7ff9-8c2d-e8aeaa1d7ffc'::uuid, 'SELECT', '01955fb4-9417-7a21-870d-599df6229b86'::uuid, 'SYS_MENU_ACTION', '01950393-5265-7501-b27e-a13fe8dcf927'::uuid, 'system/program'),
    ('01954119-d7bc-7ff6-9270-dbdf443a6381'::uuid, 'SELECT', '01955fb4-9417-7a21-870d-599df6229b86'::uuid, 'SYS_MENU_ACTION', '01950393-5265-7501-b27e-a13fe8dcf927'::uuid, 'system/program/*/action'),
    ('0195412e-0807-7ff8-9650-e146fe49f6be'::uuid, 'INSERT', '01955fb4-9417-7a21-870d-599df6229b86'::uuid, 'SYS_MENU_ACTION', '01950393-5265-7501-b27e-a13fe8dcf927'::uuid, 'system/program/*/action'),
    ('0195412d-7370-7ff6-b7dc-4d7114f46c7b'::uuid, 'UPDATE', '01955fb4-9417-7a21-870d-599df6229b86'::uuid, 'SYS_MENU_ACTION', '01950393-5265-7501-b27e-a13fe8dcf927'::uuid, 'system/program/*/action/*'),
    ('0195458b-356c-7ff6-b3dd-20f1047c81a8'::uuid, 'DELETE', '01955fb4-9417-7a21-870d-599df6229b86'::uuid, 'SYS_MENU_ACTION', '01950393-5265-7501-b27e-a13fe8dcf927'::uuid, 'system/program/*/action/*'),
    ('01956580-40b3-7e34-bbe7-b1438ce97673'::uuid, 'INSERT', '01955fb4-9464-7d22-aec6-becd977dd1ce'::uuid, 'SYS_MENU', '0194f34d-897b-7f06-9aa7-b0b1a38c16f2'::uuid, 'system/menu'),
    ('01956580-40e6-7f36-b820-8593ef8cfcc1'::uuid, 'DELETE', '01955fb4-9464-7d22-aec6-becd977dd1ce'::uuid, 'SYS_MENU', '0194f34d-897b-7f06-9aa7-b0b1a38c16f2'::uuid, 'system/menu/*'),
    ('01956580-40cb-7835-9cda-6eaaa023c200'::uuid, 'UPDATE', '01955fb4-9464-7d22-aec6-becd977dd1ce'::uuid, 'SYS_MENU', '0194f34d-897b-7f06-9aa7-b0b1a38c16f2'::uuid, 'system/menu/*'),
    ('01956580-410b-7c39-b776-01587b4d1974'::uuid, 'SELECT', '01955fb4-9464-7d22-aec6-becd977dd1ce'::uuid, 'SYS_MENU', '0194f34d-897b-7f06-9aa7-b0b1a38c16f2'::uuid, 'system/menu/tree'),
    ('01955eee-4f36-7c51-bebc-fc90737e6d59'::uuid, 'INSERT', '01955fb4-9464-7d22-aec6-becd977dd1ce'::uuid, 'SYS_MENU', '0194f34d-897b-7f06-9aa7-b0b1a38c16f2'::uuid, 'system/program'),
    ('01956580-4132-783a-a0f9-2e609f3f7551'::uuid, 'SELECT', '01955fb4-9464-7d22-aec6-becd977dd1ce'::uuid, 'SYS_MENU', '0194f34d-897b-7f06-9aa7-b0b1a38c16f2'::uuid, 'system/program'),
    ('0195657f-f48f-7a2d-ae9e-25bb889ed696'::uuid, 'UPDATE', '01955fb4-9464-7d22-aec6-becd977dd1ce'::uuid, 'SYS_MENU', '0194f34d-897b-7f06-9aa7-b0b1a38c16f2'::uuid, 'system/program/*'),
    ('01956580-4185-7e40-a757-24b8e0737caa'::uuid, 'DELETE', '01955fb4-9464-7d22-aec6-becd977dd1ce'::uuid, 'SYS_MENU', '0194f34d-897b-7f06-9aa7-b0b1a38c16f2'::uuid, 'system/program/*'),
    ('01956580-415a-7a3f-a0e0-f2f1ae94e8b0'::uuid, 'SELECT', '01955fb4-9464-7d22-aec6-becd977dd1ce'::uuid, 'SYS_MENU', '0194f34d-897b-7f06-9aa7-b0b1a38c16f2'::uuid, 'system/program/*')
) AS target(id, auth_type, menu_id, program_code, program_id, uri)
WHERE NOT EXISTS (SELECT 1 FROM common_menu_action cma WHERE cma.id = target.id);
