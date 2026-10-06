/**
 * 전수조사 진행 현황(S-303) · 검수 승인/반려(S-304) · 종결 처리(S-308) API
 */

export {
  fetchInventoryProgress,
  fetchInventoryResults,
  approveInventoryResult,
  rejectInventoryResult,
  adminConfirmInventoryResult,
  adminReportInventoryAnomaly,
  closeInventoryResult,
  closeInventoryResultsBulk,
  closeInventory,
} from '@/entities/inventory';
