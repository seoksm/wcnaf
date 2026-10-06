import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import { WiniBox, WiniTypography } from '@/shared/ui/wini';
import { useMyLicenses, MyLicenseList } from '@/features/license/myLicenses';

/** S-550 내 라이선스 (모바일웹) */
export const MyLicensePage = () => {
  const ctl = useMyLicenses();

  return (
    <WiniFormNormal>
      {ctl.isLoading ? (
        <WiniBox className="p-6 text-center">
          <WiniTypography variant="span" className="text-text-sub">불러오는 중...</WiniTypography>
        </WiniBox>
      ) : (
        <MyLicenseList licenses={ctl.licenses} isActing={ctl.isActing} onRequestRelease={ctl.requestRelease} />
      )}
    </WiniFormNormal>
  );
};

export default MyLicensePage;
