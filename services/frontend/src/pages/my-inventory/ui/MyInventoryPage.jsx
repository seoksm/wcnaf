import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import { WiniBox, WiniTypography } from '@/shared/ui/wini';
import { useMyInventoryStatus, MyStatusList } from '@/features/inventory/myStatus';
import { ScanView } from '@/features/inventory/scan';

/**
 * S-310(내 전수조사 대상 목록) · S-311(연속 스캔 검수) - 임직원 본인용 모바일웹 화면.
 * 별도 라우트/메뉴 대신 한 페이지 안에서 목록↔스캔 모드를 전환한다(이 코드베이스에 아직 없는
 * L-EMP 모바일 셸을 새로 만들지 않고, 기존 관리자 콘솔의 메뉴 등록 방식을 그대로 재사용하기
 * 위함 - 운영 문서 참고).
 */
export const MyInventoryPage = () => {
  const ctl = useMyInventoryStatus();

  return (
    <WiniFormNormal>
      {ctl.isLoading && (
        <WiniBox className="p-6 text-center">
          <WiniTypography variant="span" className="text-text-sub">불러오는 중...</WiniTypography>
        </WiniBox>
      )}

      {!ctl.isLoading && ctl.mode === 'scan' && (
        <ScanView
          onDecoded={ctl.confirmByScan}
          lastFeedback={ctl.lastFeedback}
          remainingCount={ctl.unconfirmedResults.length}
          onFinish={ctl.backToList}
          onBack={ctl.backToList}
        />
      )}

      {!ctl.isLoading && ctl.mode === 'list' && (
        <MyStatusList
          status={ctl.status}
          isActing={ctl.isActing}
          onStartScan={ctl.startScan}
          onConfirmWithPhoto={ctl.confirmWithPhoto}
          onReportWrongHolder={ctl.reportWrongHolder}
        />
      )}
    </WiniFormNormal>
  );
};

export default MyInventoryPage;
