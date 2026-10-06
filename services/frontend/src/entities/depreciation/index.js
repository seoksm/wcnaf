/**
 * Depreciation 엔티티 Public API
 */

export {
  fetchDepreciationStatus,
  fetchDepreciationSchedule,
  confirmDepreciation,
  releaseDepreciation,
  fetchDepreciationConfirmationLog,
} from './api/depreciationApi';

export { QUARTER_LABEL, QUARTER_OPTIONS, EXCLUDED_REASON_LABEL } from './model/labels';
