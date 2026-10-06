-- 근본 원인 확정(2026-09-17): api-gateway의 CDC 컨슈머(CommonAuthorizationGroupPermissionEventHandlerImpl 등,
-- common 모듈)가 spring.kafka.listener.ack-mode=manual 인데도 Acknowledgment.acknowledge()를 호출하지 않아,
-- 컨슈머가 재기동될 때마다 오프셋이 커밋되지 않은 채 토픽 전체를 처음부터 재생하고 있었다. 그 결과 V2에서
-- 복구한 ADMIN 그룹 권한이 재기동 때마다 과거 어느 시점에 남아있던 낡은(1건짜리) 스냅샷으로 매번 덮어써졌다.
-- 컨슈머 코드는 이번에 ack.acknowledge()를 호출하도록 수정했고(커밋 확인됨: consumer-groups 조회 결과 lag=0),
-- 이 마이그레이션이 적용된 이후로는 재기동해도 더 이상 덮어써지지 않는다. 내용은 V2와 동일하다.

INSERT INTO common_authorization_group_permission (authorization_group_id, menu_id, create_at, update_at,
    custom1status, custom2status, custom3status, delete_status, down_status, group_code,
    insert_status, manage_status, print_status, select_status, update_status)
SELECT '019512c8-95c1-7ffa-aeba-c0b58748478f'::uuid, target.menu_id, now(), now(),
       target.c1, target.c2, target.c3, target.del, target.down, 'ADMIN',
       target.ins, target.manage, target.print, target.sel, target.upd
FROM (VALUES
    ('01a0a985-e5f8-7a5b-90ef-c050af1226f5'::uuid, 'ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW','ALLOW'), -- 감가상각
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
