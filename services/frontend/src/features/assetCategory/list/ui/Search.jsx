import {
  WiniBox,
  WiniButton,
  WiniText,
  WiniGridLayout,
  WiniGridItem,
} from '@/shared/ui/wini';
import { winiCom } from '@/shared/lib';

export const Search = (props) => {
  return (
    <WiniBox ui="search">
      <WiniGridLayout container ui="form" columnSpacing={1} rowSpacing={1}>
        <WiniGridItem xs={12}>
          <WiniText
            ui="row"
            label="종류 코드/명"
            variant="outlined"
            slotProps={{ inputLabel: { shrink: true } }}
            name="keyword"
            value={props.searchData?.keyword || ''}
            onChange={props.onSearchChange}
            disabled={props.isLoading}
          />
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
