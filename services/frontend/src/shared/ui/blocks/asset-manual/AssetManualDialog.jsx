import {
  WiniBox,
  WiniButton,
  WiniDialog,
  WiniDialogActions,
  WiniDialogContent,
  WiniDialogTitle,
  WiniTypography,
} from '@/shared/ui/wini';
import { ASSET_MANUAL_SECTIONS } from './assetManualContent';
import { ManualSectionView } from './ManualSectionView';

/**
 * 자산관리 가이드(도움말) 다이얼로그. PageHeader의 도움말 버튼에서 연다.
 * useAssetManualDialog가 상태/현재 화면 매핑을 담당하고, 이 컴포넌트는 그 결과만 받아 그린다.
 */
export const AssetManualDialog = ({ open, activeSectionId, onSelectSection, onClose }) => {
  const activeSection = ASSET_MANUAL_SECTIONS.find((s) => s.id === activeSectionId) || ASSET_MANUAL_SECTIONS[0];

  const groupedSections = ASSET_MANUAL_SECTIONS.reduce((acc, section) => {
    (acc[section.part] ||= []).push(section);
    return acc;
  }, {});

  return (
    <WiniDialog open={open} onClose={onClose} fullWidth maxWidth="lg">
      <WiniDialogTitle>자산관리 가이드</WiniDialogTitle>

      <WiniDialogContent className="p-0">
        <WiniBox className="flex h-[70vh] min-h-[420px]">
          <WiniBox className="hidden w-[240px] flex-none overflow-y-auto border-0 border-r border-solid border-gray-200 bg-gray-50 py-2 sm:block">
            {Object.entries(groupedSections).map(([part, sections]) => (
              <WiniBox ui="noAutoGap" key={part} className="mb-1">
                <WiniTypography variant="span" className="block px-3 py-1.5 text-xs font-bold tracking-wide text-text-sub">
                  {part}
                </WiniTypography>
                {sections.map((section) => (
                  <WiniBox
                    ui="noAutoGap"
                    key={section.id}
                    component="button"
                    type="button"
                    onClick={() => onSelectSection(section.id)}
                    className={`flex w-full cursor-pointer items-center gap-2 border-0 bg-transparent px-3 py-1.5 text-left text-sm ${
                      section.id === activeSection?.id
                        ? 'bg-brand-main/10 font-semibold text-brand-main'
                        : 'text-text-default hover:bg-gray-100'
                    }`}
                  >
                    <WiniTypography variant="span" className="flex-none font-mono text-[11px] text-text-sub">
                      {section.code}
                    </WiniTypography>
                    <WiniTypography variant="span" className="truncate">{section.title}</WiniTypography>
                  </WiniBox>
                ))}
              </WiniBox>
            ))}
          </WiniBox>

          <WiniBox className="flex-1 overflow-y-auto px-5 py-4">
            <ManualSectionView section={activeSection} />
          </WiniBox>
        </WiniBox>
      </WiniDialogContent>

      <WiniDialogActions>
        <WiniButton ui="lineGray" onClick={onClose}>닫기</WiniButton>
      </WiniDialogActions>
    </WiniDialog>
  );
};
