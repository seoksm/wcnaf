import { WiniBox, WiniGridItem, WiniGridLayout, WiniTypography } from '@/shared/ui/wini';
import { winiDate } from '@/shared/lib';
import { ASSIGN_TYPE_LABEL, LIFE_STATUS_LABEL } from '@/entities/tangibleAsset';

const InfoItem = ({ label, value }) => (
  <WiniGridItem size={{ xs: 12 }}>
    <WiniBox className="flex items-baseline justify-between gap-3 border-0 border-b border-solid border-gray-100 py-2 last:border-b-0">
      <WiniTypography variant="span" className="shrink-0 text-xs text-gray-500">
        {label}
      </WiniTypography>
      <WiniTypography variant="span" className="break-all text-right text-sm font-semibold">
        {value || '-'}
      </WiniTypography>
    </WiniBox>
  </WiniGridItem>
);

/**
 * canViewValue(가치조회 추가권한)가 없으면 취득가액 행 자체를 렌더링하지 않는다 -
 * 값을 가리는 게 아니라 필드를 만들지 않아, DOM에서 값을 읽어가는 것도 막는다.
 */
export const AssetScanInfo = ({ asset, canViewValue = false }) => (
  <WiniBox ui="line" className="mb-4 px-4 py-1">
    <WiniGridLayout container rowSpacing={0}>
      <InfoItem label="종류" value={asset.categoryName} />
      <InfoItem label="위치" value={asset.locationName} />
      <InfoItem label="생애 상태" value={LIFE_STATUS_LABEL[asset.lifeStatus] || asset.lifeStatus} />
      <InfoItem label="배정 형태" value={ASSIGN_TYPE_LABEL[asset.assignType] || asset.assignType} />
      <InfoItem
        label="취득일"
        value={asset.acquisitionDate ? winiDate.dateFormat(winiDate(asset.acquisitionDate), 'YYYY-MM-DD') : null}
      />
      {canViewValue && (
        <InfoItem
          label="취득가액"
          value={asset.acquisitionAmount != null ? `${Number(asset.acquisitionAmount).toLocaleString()}원` : null}
        />
      )}
      <InfoItem label="모델명" value={asset.modelName} />
      <InfoItem label="제조사" value={asset.manufacturer} />
      <InfoItem label="시리얼번호" value={asset.serialNo} />
      <InfoItem label="메모" value={asset.memo} />
    </WiniGridLayout>
  </WiniBox>
);
