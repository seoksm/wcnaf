-- 감가상각 현황(01a0a985-e5f8-7a5b-90ef-c050af1226f5) 메뉴가 실제 화면(program 연결)임에도
-- menu_type='MENU'(폴더/그룹 노드용)로 잘못 등록되어 있었다. 이로 인해 두 가지 문제가 있었다:
--   1) system의 MenuPermissionQueryRepository.getMenuPermissionTreeByUserId가 menuType=MENU인
--      노드만 별도 1차 조회(sortSeq 전역 오름차순 순회) 하는데, 이 메뉴의 sortSeq(4, 부모 "자산관리"
--      내에서의 순번)가 부모 "자산관리"의 전역 sortSeq(20)보다 작아 부모보다 먼저 순회되며
--      children이 아직 초기화되지 않아 NPE가 발생했다(2026-09-17, 로그인 직후 메뉴 트리 조회 시 500 에러).
--      (코드 자체도 순회 순서에 안전하도록 함께 수정함 - MenuPermissionQueryRepository.java)
--   2) api-gateway로 전파되는 CDC 동기화 쿼리(ProgramActionRepository.getMenuActionByProgramIdList)가
--      menuType='PROGRAM'인 메뉴만 조인하도록 되어 있어, 이 메뉴는 CDC 동기화 대상에서 항상 제외되고
--      있었다. 실제 화면(프로그램)에 연결된 메뉴는 반드시 menuType='PROGRAM'이어야 한다.
UPDATE menu
SET menu_type = 'PROGRAM', update_at = now()
WHERE id = '01a0a985-e5f8-7a5b-90ef-c050af1226f5'
  AND menu_type = 'MENU';
