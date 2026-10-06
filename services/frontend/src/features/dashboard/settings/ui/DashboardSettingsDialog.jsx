import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import {
  WiniBox, WiniButton, WiniCheckbox, WiniDialog, WiniDialogActions, WiniDialogContent,
  WiniDialogTitle, WiniIconButton, WiniTypography,
} from '@/shared/ui/wini';
import { useWidgetSettingsForm } from '../model/useWidgetSettingsForm';

/** S-701 대시보드 설정 - 위젯 표시여부·순서(P-6 모달) */
export const DashboardSettingsDialog = ({ open, widgetConfig, isSaving, onClose, onSave, onReset }) => {
  const { rows, toggleVisible, move, buildSavePayload } = useWidgetSettingsForm({ open, widgetConfig });

  return (
    <WiniDialog open={open} onClose={onClose} fullWidth maxWidth="xs">
      <WiniDialogTitle>대시보드 설정</WiniDialogTitle>
      <WiniDialogContent>
        <WiniTypography variant="span" className="mb-2 block text-xs text-text-sub">
          표시할 위젯과 순서를 정합니다.
        </WiniTypography>
        {/* 각 행 안의 두 그룹(체크박스+라벨 / 위아래 버튼)은 가로로 나란히 놓이므로 일반
            div를 쓴다 - WiniBox를 가로로 나열하면 첫 번째만 auto-gap margin-top이 없어
            나머지가 아래로 밀려 보이는 버그가 생긴다(StatTile 주석 참고). 행 자체(세로
            나열)는 WiniBox로 둬도 안전하다 */}
        <WiniBox className="flex flex-col divide-y divide-solid divide-gray-100">
          {rows.map((row, index) => (
            <WiniBox key={row.widgetKey} className="flex items-center justify-between py-2">
              <div className="flex items-center gap-2">
                <WiniCheckbox checked={row.visible} onChange={() => toggleVisible(row.widgetKey)} size="small" />
                <WiniTypography variant="span" className={`text-sm ${row.visible ? 'text-text-main' : 'text-text-sub'}`}>
                  {row.label}
                </WiniTypography>
              </div>
              <div className="flex items-center">
                <WiniIconButton size="small" disabled={index === 0} onClick={() => move(index, -1)}>
                  <KeyboardArrowUpIcon fontSize="small" />
                </WiniIconButton>
                <WiniIconButton size="small" disabled={index === rows.length - 1} onClick={() => move(index, 1)}>
                  <KeyboardArrowDownIcon fontSize="small" />
                </WiniIconButton>
              </div>
            </WiniBox>
          ))}
        </WiniBox>
      </WiniDialogContent>
      <WiniDialogActions>
        <WiniButton ui="line" onClick={() => onSave(buildSavePayload())} loading={isSaving} disabled={isSaving}>저장</WiniButton>
        <WiniButton ui="lineGray" onClick={onReset} disabled={isSaving}>기본값</WiniButton>
        <WiniButton ui="lineGray" onClick={onClose}>취소</WiniButton>
      </WiniDialogActions>
    </WiniDialog>
  );
};
