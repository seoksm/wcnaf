import { WiniBox, WiniTypography } from '@/shared/ui/wini';

export const AssetScanState = ({ children }) => (
  <WiniBox className="flex min-h-40 items-center justify-center p-6 text-center">
    <WiniTypography variant="span" className="text-sm text-gray-500">
      {children}
    </WiniTypography>
  </WiniBox>
);
