import { useParams } from 'react-router-dom';
import { WiniFormContextProvider } from '@/shared/ui';
import { WiniFormEmpty } from '@/shared/ui/blocks/form-layout';
import { WiniBox } from '@/shared/ui/wini';
import { userStore } from '@/shared/model';
import { useStandaloneMenuContext } from '@/features/navigation/authenticated-layout';
import { AssignmentHistoryPanel } from '@/features/tangibleAsset/assignment';
import { AssetScanInfo, AssetScanState, AssetScanSummary, useAssetScanDetail } from '@/features/tangibleAsset/scan-detail';

const AssetScanDetailContent = ({ canViewValue }) => {
  const { tangibleAssetId } = useParams();
  const { asset, history, userList, isLoading, isReleasing, notFound, error, release } =
    useAssetScanDetail(tangibleAssetId);

  if (isLoading) {
    return <AssetScanState>불러오는 중...</AssetScanState>;
  }

  if (error) {
    return <AssetScanState>{error}</AssetScanState>;
  }

  if (notFound || !asset) {
    return <AssetScanState>자산 정보를 찾을 수 없습니다.</AssetScanState>;
  }

  return (
    <WiniBox className="mx-auto w-full max-w-[480px] p-4">
      <AssetScanSummary asset={asset} />
      <AssetScanInfo asset={asset} canViewValue={canViewValue} />
      <AssignmentHistoryPanel
        history={history}
        userList={userList}
        onRelease={release}
        isReleasing={isReleasing}
      />
    </WiniBox>
  );
};

/**
 * QR 스캔 진입 - 유형자산 상세 (모바일웹 공용 진입점, S-217)
 */
export const AssetScanDetailPage = () => {
  const userId = userStore((state) => state.userId?.toString());
  const { contextValue, isLoading, error } = useStandaloneMenuContext(
    userId,
    '/tangible-asset-management',
  );

  if (isLoading) {
    return (
      <WiniFormEmpty>
        <AssetScanState>권한 정보를 확인하는 중...</AssetScanState>
      </WiniFormEmpty>
    );
  }

  if (error || !contextValue) {
    return (
      <WiniFormEmpty>
        <AssetScanState>{error || '화면을 열 수 없습니다.'}</AssetScanState>
      </WiniFormEmpty>
    );
  }

  return (
    <WiniFormContextProvider value={contextValue}>
      <WiniFormEmpty>
        <AssetScanDetailContent canViewValue={contextValue.winiAut?.valueView === 'ALLOW'} />
      </WiniFormEmpty>
    </WiniFormContextProvider>
  );
};

export default AssetScanDetailPage;
