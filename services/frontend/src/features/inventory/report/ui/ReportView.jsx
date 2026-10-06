import {
  WiniBox,
  WiniButton,
  WiniDialog,
  WiniDialogActions,
  WiniDialogContent,
  WiniDialogTitle,
  WiniTypography,
} from '@/shared/ui/wini';
import { winiDate } from '@/shared/lib';
import {
  INVENTORY_TYPE_LABEL,
  INVENTORY_STATUS_LABEL,
  CLOSURE_ACTION_LABEL,
  CLOSURE_REASON_CODE_LABEL,
  ANOMALY_TYPE_LABEL,
} from '@/entities/inventory';

const formatDateTime = (value) => (value ? winiDate.dateFormat(winiDate(value), 'YYYY-MM-DD HH:mm') : '-');
const formatAmount = (value) => (value == null ? '-' : Number(value).toLocaleString());

/**
 * S-305 리포트 - §4 핵심 섹션인 "미확인 처리 내역"(closureBreakdown)과 분실 목록을 앞세운다.
 * PDF(종료 확정 후 감사 제출용)는 이 서비스에 PDF 생성 인프라가 없어 미구현 - 엑셀(참고용)만 제공한다.
 */
export const ReportView = ({ open, report, isLoading, isDownloading, onClose, onDownloadExcel }) => {
  return (
    <WiniDialog open={open} onClose={onClose} fullWidth maxWidth="md">
      <WiniDialogTitle>전수조사 리포트{report ? ` - ${report.title}` : ''}</WiniDialogTitle>

      <WiniDialogContent className="pt-4 pb-4">
        {isLoading && <WiniTypography variant="span" className="text-sm text-text-sub">불러오는 중...</WiniTypography>}

        {!isLoading && !report && (
          <WiniTypography variant="span" className="text-sm text-red-500">
            리포트를 불러오지 못했습니다. 다시 시도해주세요.
          </WiniTypography>
        )}

        {!isLoading && report && (
          <WiniBox className="flex flex-col gap-4">
            <WiniBox className="flex flex-wrap gap-4 text-sm text-text-sub">
              <span>{INVENTORY_TYPE_LABEL[report.inventoryType] || report.inventoryType}</span>
              <span>{INVENTORY_STATUS_LABEL[report.status] || report.status}</span>
              <span>시작 {formatDateTime(report.createAt)}</span>
              <span>종료 {report.status === 'CLOSED' ? formatDateTime(report.closedAt) : '진행중'}</span>
            </WiniBox>

            <WiniBox className="flex gap-4">
              {[
                { key: 'confirmedCount', label: '확인완료' },
                { key: 'pendingApprovalCount', label: '승인대기' },
                { key: 'anomalyCount', label: '이상' },
                { key: 'unconfirmedCount', label: '미확인' },
              ].map((item) => (
                <WiniBox ui="noAutoGap" key={item.key} className="flex-1 rounded border border-solid border-gray-200 bg-white px-3 py-2 text-center">
                  <WiniTypography variant="span" className="block text-xl font-bold">{report[item.key] ?? 0}</WiniTypography>
                  <WiniTypography variant="span" className="block text-xs text-text-sub">{item.label}</WiniTypography>
                </WiniBox>
              ))}
            </WiniBox>

            <WiniBox>
              <WiniTypography variant="span" className="mb-1 block text-sm font-semibold">
                미확인 처리 내역 (핵심 섹션)
              </WiniTypography>
              {report.closureBreakdown?.length > 0 ? (
                <table className="w-full border-collapse text-sm">
                  <thead>
                    <tr className="border-b border-solid border-gray-200 text-left text-text-sub">
                      <th className="py-1">처리 방법</th>
                      <th className="py-1">사유</th>
                      <th className="py-1 text-right">건수</th>
                    </tr>
                  </thead>
                  <tbody>
                    {report.closureBreakdown.map((item, idx) => (
                      <tr key={idx} className="border-b border-solid border-gray-100">
                        <td className="py-1">{CLOSURE_ACTION_LABEL[item.closureAction] || item.closureAction}</td>
                        <td className="py-1">{CLOSURE_REASON_CODE_LABEL[item.closureReasonCode] || item.closureReasonCode}</td>
                        <td className="py-1 text-right">{item.count}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              ) : (
                <WiniTypography variant="span" className="text-sm text-gray-400">미확인 처리 내역이 없습니다.</WiniTypography>
              )}
            </WiniBox>

            {report.lostItems?.length > 0 && (
              <WiniBox>
                <WiniTypography variant="span" className="mb-1 block text-sm font-semibold text-red-600">
                  분실 처리 자산 (처분손실 대상)
                </WiniTypography>
                <table className="w-full border-collapse text-sm">
                  <thead>
                    <tr className="border-b border-solid border-gray-200 text-left text-text-sub">
                      <th className="py-1">자산코드</th>
                      <th className="py-1">자산명</th>
                      <th className="py-1 text-right">장부가</th>
                    </tr>
                  </thead>
                  <tbody>
                    {report.lostItems.map((item) => (
                      <tr key={item.tangibleAssetId} className="border-b border-solid border-gray-100">
                        <td className="py-1">{item.assetCode}</td>
                        <td className="py-1">{item.assetName}</td>
                        <td className="py-1 text-right">{formatAmount(item.bookValue)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </WiniBox>
            )}

            {report.anomalyBreakdown?.length > 0 && (
              <WiniBox>
                <WiniTypography variant="span" className="mb-1 block text-sm font-semibold">이상 보고 분해</WiniTypography>
                <WiniBox className="flex flex-wrap gap-3 text-sm">
                  {report.anomalyBreakdown.map((item, idx) => (
                    <span key={idx}>{ANOMALY_TYPE_LABEL[item.anomalyType] || item.anomalyType} {item.count}건</span>
                  ))}
                </WiniBox>
              </WiniBox>
            )}
          </WiniBox>
        )}
      </WiniDialogContent>

      <WiniDialogActions>
        <WiniButton
          ui="lineGray"
          onClick={() => onDownloadExcel(report?.inventoryId)}
          loading={isDownloading}
          disabled={isDownloading || !report}
        >
          엑셀 다운로드 (참고용)
        </WiniButton>
        <WiniButton ui="lineGray" onClick={onClose}>닫기</WiniButton>
      </WiniDialogActions>
    </WiniDialog>
  );
};
