import { WiniBox, WiniButton, WiniGridLayout, WiniGridItem, WiniMenuItem, WiniSelect, WiniText } from '@/shared/ui/wini';
import { winiCom } from '@/shared/lib';

const WITHIN_DAYS_OPTIONS = [
  { value: '', label: '전체' },
  { value: '30', label: '30일 내 만료' },
  { value: '90', label: '90일 내 만료' },
  { value: '180', label: '180일 내 만료' },
];

export const Search = ({ searchData, onSearchChange, onSearch, isLoading, extraActions }) => {
  return (
    <WiniBox ui="search">
      <WiniGridLayout container ui="form" columnSpacing={1} rowSpacing={1}>
        <WiniGridItem xs={12} sm={6} md={4}>
          <WiniText
            ui="row"
            label="자산명"
            name="keyword"
            className="w-full"
            value={searchData?.keyword || ''}
            onChange={onSearchChange}
          />
        </WiniGridItem>
        <WiniGridItem xs={12} sm={6} md={4}>
          <WiniSelect ui="row" label="만료 구간" name="withinDays" className="w-full" value={searchData?.withinDays ?? ''} onChange={onSearchChange}>
            {WITHIN_DAYS_OPTIONS.map((opt) => (
              <WiniMenuItem value={opt.value} key={opt.value}>{opt.label}</WiniMenuItem>
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
