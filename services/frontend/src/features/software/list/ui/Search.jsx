import { WiniBox, WiniButton, WiniText, WiniGridLayout, WiniGridItem } from '@/shared/ui/wini';
import { winiCom } from '@/shared/lib';

export const Search = ({ searchData, onSearchChange, onSearch, isLoading }) => {
  return (
    <WiniBox ui="search">
      <WiniGridLayout container ui="form" columnSpacing={1} rowSpacing={1}>
        <WiniGridItem xs={12}>
          <WiniText
            ui="row"
            label="소프트웨어명"
            variant="outlined"
            slotProps={{ inputLabel: { shrink: true } }}
            name="keyword"
            value={searchData?.keyword || ''}
            onChange={onSearchChange}
            disabled={isLoading}
          />
        </WiniGridItem>
      </WiniGridLayout>
      <WiniBox ui="btnbox">
        <WiniBox ui="btnitem" />
        <WiniBox ui="btnitem">
          {winiCom.checkMenuAut(
            'select',
            <WiniButton onClick={onSearch} loading={isLoading} disabled={isLoading}>조회</WiniButton>,
          )}
        </WiniBox>
      </WiniBox>
    </WiniBox>
  );
};
