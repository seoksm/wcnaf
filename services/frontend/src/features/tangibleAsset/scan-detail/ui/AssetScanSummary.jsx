import { WiniBox, WiniTypography } from '@/shared/ui/wini';

export const AssetScanSummary = ({ asset }) => (
  <WiniBox className="mb-4 rounded-lg bg-gray-900 px-4 py-4 text-white">
    <WiniTypography variant="span" className="mb-1 block text-xs text-gray-300">
      {asset.assetCode}
    </WiniTypography>
    <WiniTypography variant="h5" className="break-words font-bold leading-snug text-white">
      {asset.assetName}
    </WiniTypography>
  </WiniBox>
);
