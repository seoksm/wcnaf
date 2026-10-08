import { useCallback, useMemo, useState } from 'react';
import { ASSET_MANUAL_PROGRAM_MAP, ASSET_MANUAL_SECTIONS } from './assetManualContent';

/**
 * 자산관리 가이드(도움말) 다이얼로그 상태 + 현재 화면에 맞는 섹션 매핑.
 * menuUrl(programMapping, 예: "tangible-asset-management/ui/TangibleAssetManagementPage")의
 * 첫 segment로 해당 화면의 매뉴얼 섹션을 찾는다. 매핑이 없으면(자산관리 영역 밖 화면) hasManual이
 * false가 되어 PageHeader가 도움말 버튼 자체를 숨긴다.
 */
export const useAssetManualDialog = (menuUrl) => {
  const [open, setOpen] = useState(false);
  const [activeSectionId, setActiveSectionId] = useState(null);

  const defaultSectionId = useMemo(() => {
    const pageKey = (menuUrl || '').split('/')[0];
    return ASSET_MANUAL_PROGRAM_MAP[pageKey] || null;
  }, [menuUrl]);

  const hasManual = Boolean(defaultSectionId);

  const openManual = useCallback(() => {
    setActiveSectionId(defaultSectionId || ASSET_MANUAL_SECTIONS[0]?.id || null);
    setOpen(true);
  }, [defaultSectionId]);

  const closeManual = useCallback(() => setOpen(false), []);

  return {
    open,
    hasManual,
    activeSectionId,
    setActiveSectionId,
    openManual,
    closeManual,
  };
};
