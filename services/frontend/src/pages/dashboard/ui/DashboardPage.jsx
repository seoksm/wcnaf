import SettingsOutlinedIcon from '@mui/icons-material/SettingsOutlined';
import CategoryOutlinedIcon from '@mui/icons-material/CategoryOutlined';
import PlaceOutlinedIcon from '@mui/icons-material/PlaceOutlined';
import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import { WiniBox, WiniIconButton, WiniTypography } from '@/shared/ui/wini';
import { formatDateTime } from '@/shared/lib/payment';
import { useDashboard } from '@/features/dashboard';
import { AssetSummaryWidget } from '@/features/dashboard/summary';
import { BarDistributionWidget, StatusDistributionWidget } from '@/features/dashboard/distribution';
import { CostTrendWidget } from '@/features/dashboard/trend';
import { ExpiringSoonWidget, TicketStatusWidget } from '@/features/dashboard/action';
import { LoanStatusWidget, InventoryProgressWidget, LicenseConsistencyWidget } from '@/features/dashboard/process';
import { DashboardSettingsDialog } from '@/features/dashboard/settings';

/** 대시보드 5개 구획을 나누는 소제목 - "얼마나 있나→어디에 있나→어떻게 변하나→무엇을 해야 하나" 순서를 눈으로도 드러낸다 */
const SectionLabel = ({ children }) => (
  <WiniTypography variant="span" className="mb-2 mt-6 block text-xs font-semibold uppercase tracking-wide text-text-sub first:mt-0">
    {children}
  </WiniTypography>
);

/** S-700 대시보드 · S-701 설정. 배치 순서는 설계문서 §2를 그대로 따른다: 요약→분포→추세→조치필요→프로세스현황 */
export const DashboardPage = () => {
  const ctl = useDashboard();
  const summary = ctl.summary;
  const isVisible = (key) => ctl.visibleWidgets.includes(key);

  return (
    <WiniFormNormal>
      <WiniBox className="mb-1 flex flex-wrap items-center justify-between gap-2">
        <WiniTypography variant="h1">대시보드</WiniTypography>
        <WiniBox className="flex items-center gap-2">
          {summary && (
            <WiniTypography variant="span" className="text-xs text-text-sub">
              기준: {formatDateTime(summary.aggregatedAt)}
            </WiniTypography>
          )}
          <WiniIconButton size="small" onClick={ctl.openSettings} aria-label="대시보드 설정">
            <SettingsOutlinedIcon fontSize="small" />
          </WiniIconButton>
        </WiniBox>
      </WiniBox>

      {ctl.isLoading && !summary ? (
        <WiniTypography variant="span" className="text-xs text-text-sub">불러오는 중...</WiniTypography>
      ) : (
        <>
          {isVisible('ASSET_SUMMARY') && <AssetSummaryWidget data={summary?.assetSummary} />}

          {(isVisible('CATEGORY_DISTRIBUTION') || isVisible('LOCATION_DISTRIBUTION') || isVisible('STATUS_DISTRIBUTION')) && (
            <>
              <SectionLabel>분포</SectionLabel>
              {/* 뷰포트가 아니라 실제 콘텐츠 폭 기준(사이드바 유무·열림닫힘과 무관) - WiniFormNormal이
                  연 @container를 사용한다. 이름 있는 컨테이너 크기(@4xl 등)가 아니라 픽셀
                  임의값을 쓰는 이유는 AssetSummaryWidget의 주석(62.5% 루트 폰트크기 함정) 참고 */}
              <WiniBox className="grid grid-cols-1 gap-4 @min-[896px]:grid-cols-3">
                {isVisible('CATEGORY_DISTRIBUTION') && (
                  <BarDistributionWidget icon={CategoryOutlinedIcon} title="종류별 현황" items={summary?.categoryDistribution} />
                )}
                {isVisible('LOCATION_DISTRIBUTION') && (
                  <BarDistributionWidget icon={PlaceOutlinedIcon} title="위치별 현황" items={summary?.locationDistribution} />
                )}
                {isVisible('STATUS_DISTRIBUTION') && (
                  <StatusDistributionWidget items={summary?.statusDistribution} />
                )}
              </WiniBox>
            </>
          )}

          {isVisible('MONTHLY_COST_TREND') && (
            <>
              <SectionLabel>추세</SectionLabel>
              <CostTrendWidget items={summary?.monthlyCostTrend} />
            </>
          )}

          {(isVisible('EXPIRING_SOON') || isVisible('TICKET_STATUS')) && (
            <>
              <SectionLabel>조치 필요</SectionLabel>
              <WiniBox className="grid grid-cols-1 gap-4 @min-[672px]:grid-cols-2">
                {isVisible('EXPIRING_SOON') && (
                  <ExpiringSoonWidget data={summary?.expiringSoon} onNavigate={ctl.goToIntangibleAssetManagement} />
                )}
                {isVisible('TICKET_STATUS') && (
                  <TicketStatusWidget data={summary?.ticketStatus} onNavigate={ctl.goToTicketManagement} />
                )}
              </WiniBox>
            </>
          )}

          {(isVisible('LOAN_STATUS') || isVisible('INVENTORY_PROGRESS') || isVisible('LICENSE_CONSISTENCY')) && (
            <>
              <SectionLabel>프로세스 현황</SectionLabel>
              <WiniBox className="grid grid-cols-1 gap-4 @min-[896px]:grid-cols-3">
                {isVisible('LOAN_STATUS') && <LoanStatusWidget data={summary?.loanStatus} />}
                {isVisible('INVENTORY_PROGRESS') && <InventoryProgressWidget items={summary?.inventoryProgress} />}
                {isVisible('LICENSE_CONSISTENCY') && <LicenseConsistencyWidget data={summary?.licenseConsistency} />}
              </WiniBox>
            </>
          )}
        </>
      )}

      <DashboardSettingsDialog
        open={ctl.settingsOpen}
        widgetConfig={ctl.widgetConfig}
        isSaving={ctl.isSaving}
        onClose={ctl.closeSettings}
        onSave={ctl.saveSettings}
        onReset={ctl.resetSettings}
      />
    </WiniFormNormal>
  );
};

export default DashboardPage;
