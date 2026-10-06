/**
 * 전수조사 생성(임직원형/관리자형) API
 */

export {
  previewMemberInventory,
  registerMemberInventory,
  previewAdminInventory,
  registerAdminInventory,
} from '@/entities/inventory';

// 제외 임직원(S-301) · 검수자(S-302) 선택 후보 - 유형자산의 배정 대상 후보와 같은 공용 사용자 목록을 재사용한다
export { fetchCommonUsers } from '@/entities/tangibleAsset';
