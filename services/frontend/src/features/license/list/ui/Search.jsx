import { WiniBox, WiniButton, WiniGridLayout, WiniGridItem, WiniText } from '@/shared/ui/wini';
import { winiCom } from '@/shared/lib';

export const Search = ({ searchData, onSearchChange, onSearch, isLoading, extraActions }) => {
  return (
    <WiniBox ui="search">
      <WiniGridLayout container ui="form" columnSpacing={1} rowSpacing={1}>
        <WiniGridItem xs={12}>
          <WiniText ui="row" label="라이선스명" name="keyword" className="w-full" value={searchData?.keyword || ''} onChange={onSearchChange} />
        </WiniGridItem>
      </WiniGridLayout>
      <WiniBox ui="btnbox">
        <WiniBox ui="btnitem" />
        <WiniBox ui="btnitem">
          {winiCom.checkMenuAut(
            'select',
            <WiniButton onClick={onSearch} loading={isLoading} disabled={isLoading}>조회</WiniButton>,
          )}
          {extraActions}
        </WiniBox>
      </WiniBox>
    </WiniBox>
  );
};
