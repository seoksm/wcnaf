import {
  WiniBox,
  WiniButton,
  WiniGridItem,
  WiniGridLayout,
  WiniMenuItem,
  WiniSelect,
} from '@/shared/ui/wini';
import { winiCom, winiDate } from '@/shared/lib';
import { QUARTER_OPTIONS } from '@/entities/depreciation';

const currentYear = winiDate.now().year();
const YEAR_OPTIONS = Array.from({ length: 6 }, (_, i) => currentYear - i);

export const Search = (props) => {
  return (
    <WiniBox ui="search">
      <WiniGridLayout container ui="form" columnSpacing={1} rowSpacing={1}>
        <WiniGridItem xs={12} sm={6} md={4}>
          <WiniSelect
            label="회계연도"
            name="fiscalYear"
            value={props.searchData?.fiscalYear || ''}
            labelProps={{ shrink: true }}
            className="w-full"
            onChange={props.onSearchChange}
          >
            {YEAR_OPTIONS.map((year) => (
              <WiniMenuItem value={year} key={year}>
                {year}년
              </WiniMenuItem>
            ))}
          </WiniSelect>
        </WiniGridItem>
        <WiniGridItem xs={12} sm={6} md={4}>
          <WiniSelect
            label="누적분기"
            name="quarter"
            value={props.searchData?.quarter || ''}
            labelProps={{ shrink: true }}
            className="w-full"
            onChange={props.onSearchChange}
          >
            {QUARTER_OPTIONS.map((q) => (
              <WiniMenuItem value={q.value} key={q.value}>
                {q.label}
              </WiniMenuItem>
            ))}
          </WiniSelect>
        </WiniGridItem>
      </WiniGridLayout>
      <WiniBox ui="btnbox">
        <WiniBox ui="btnitem" />
        <WiniBox ui="btnitem">
          {winiCom.checkMenuAut(
            'select',
            <WiniButton
              onClick={props.onSearch}
              loading={props.isLoading}
              disabled={props.isLoading}
            >
              조회
            </WiniButton>,
          )}
        </WiniBox>
      </WiniBox>
    </WiniBox>
  );
};
