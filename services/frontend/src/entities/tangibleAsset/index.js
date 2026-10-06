/**
 * TangibleAsset 엔티티 Public API
 */

export {
  fetchTangibleAssets,
  fetchTangibleAsset,
  createTangibleAsset,
  updateTangibleAsset,
  fetchCommonUsers,
  fetchAssignmentHistory,
  releaseAssignment,
  duplicateTangibleAsset,
  batchModifyTangibleAsset,
  fetchTangibleAssetExcelTemplate,
  previewTangibleAssetExcelUpsert,
  commitTangibleAssetExcelUpsert,
  fetchTangibleAssetHistory,
  fetchTangibleAssetActivityLog,
  disuseTangibleAsset,
  restoreTangibleAsset,
  disposeTangibleAsset,
  fetchDisposalAssetList,
} from './api/tangibleAssetApi';

export {
  LIFE_STATUS_LABEL,
  ASSIGN_TYPE_LABEL,
  HISTORY_TYPE_LABEL,
  LIFE_STATUS_OPTIONS,
  ASSIGN_TYPE_OPTIONS,
  HISTORY_TYPE_OPTIONS,
  DISPOSAL_REASON_CODE_LABEL,
  DISPOSAL_REASON_CODE_OPTIONS,
  requiresMember,
} from './model/statusLabels';
