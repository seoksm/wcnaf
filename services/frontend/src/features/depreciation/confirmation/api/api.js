/**
 * 결산 확정/해제/이력 API (S-232)
 */

export {
  confirmDepreciation as postConfirmDepreciation,
  releaseDepreciation as postReleaseDepreciation,
  fetchDepreciationConfirmationLog as getDepreciationConfirmationLog,
} from '@/entities/depreciation';
