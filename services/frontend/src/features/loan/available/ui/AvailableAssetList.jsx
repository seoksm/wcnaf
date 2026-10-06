import { WiniBox, WiniButton, WiniTypography } from '@/shared/ui/wini';
import { winiDate } from '@/shared/lib';

/** S-420 대여 가능 자산 목록 - 대여중인 자산도 대여자·기한과 함께 그대로 남긴다(§3) */
export const AvailableAssetList = ({ assets, isLoading, onStartScan }) => {
  return (
    <WiniBox className="flex flex-col gap-3">
      <WiniBox className="flex items-center justify-between">
        <WiniTypography variant="h2">대여 가능 자산</WiniTypography>
        <WiniButton onClick={onStartScan}>QR 스캔으로 대여하기</WiniButton>
      </WiniBox>

      {isLoading ? (
        <WiniBox className="p-6 text-center">
          <WiniTypography variant="span" className="text-text-sub">불러오는 중...</WiniTypography>
        </WiniBox>
      ) : (
        <WiniBox className="flex flex-col gap-2">
          {(assets || []).map((asset) => (
            <WiniBox
              key={asset.tangibleAssetId}
              className="flex flex-col gap-1 rounded border border-solid border-gray-200 bg-white p-3"
            >
              <WiniBox className="flex items-center justify-between">
                <WiniTypography variant="span" className="text-sm font-semibold">
                  {asset.assetName} ({asset.assetCode})
                </WiniTypography>
                <span className={`rounded border border-solid px-2 py-0.5 text-xs font-semibold ${
                  asset.available ? 'border-green-300 text-green-600' : 'border-gray-300 text-gray-500'
                }`}>
                  {asset.available ? '대여가능' : '대여중'}
                </span>
              </WiniBox>
              <WiniTypography variant="span" className="text-xs text-text-sub">{asset.categoryName}</WiniTypography>
              {!asset.available && asset.dueDate && (
                <WiniTypography variant="span" className="text-xs text-text-sub">
                  반납기한 {winiDate.dateFormat(winiDate(asset.dueDate), 'YYYY-MM-DD')}
                </WiniTypography>
              )}
            </WiniBox>
          ))}
          {(assets || []).length === 0 && (
            <WiniBox ui="info" className="p-4 text-center">
              <WiniTypography variant="span" className="text-text-sub">대여 가능한 자산이 없습니다.</WiniTypography>
            </WiniBox>
          )}
        </WiniBox>
      )}
    </WiniBox>
  );
};
