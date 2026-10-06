import { WiniBox, WiniButton, WiniGridLayout, WiniGridItem, WiniMenuItem, WiniSelect, WiniText } from '@/shared/ui/wini';
import { winiCom } from '@/shared/lib';
import { TICKET_STATUS_LABEL } from '@/entities/ticket';

export const Search = ({ searchData, onSearchChange, onSearch, isLoading, extraActions }) => {
  return (
    <WiniBox ui="search">
      <WiniGridLayout container ui="form" columnSpacing={1} rowSpacing={1}>
        <WiniGridItem xs={12} sm={6} md={4}>
          <WiniText ui="row" label="제목" name="keyword" className="w-full" value={searchData?.keyword || ''} onChange={onSearchChange} />
        </WiniGridItem>
        <WiniGridItem xs={12} sm={6} md={4}>
          <WiniSelect ui="row" label="상태" name="status" className="w-full" value={searchData?.status || ''} displayEmpty onChange={onSearchChange}>
            <WiniMenuItem value="">(전체)</WiniMenuItem>
            {Object.entries(TICKET_STATUS_LABEL).map(([value, label]) => (
              <WiniMenuItem value={value} key={value}>{label}</WiniMenuItem>
            ))}
          </WiniSelect>
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
