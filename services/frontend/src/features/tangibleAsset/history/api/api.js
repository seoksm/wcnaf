/**
 * 유형자산 변경 이력 · 전체 활동 로그 API (S-220/221)
 */

export {
  fetchTangibleAssetHistory as getAssetHistory,
  fetchTangibleAssetActivityLog as getActivityLog,
} from '@/entities/tangibleAsset';
