import {
  WiniBox,
  WiniButton,
  WiniText,
  WiniGridLayout,
  WiniGridItem,
} from '@/shared/ui/wini';
import { winiCom } from '@/shared/lib';

export const Search = (props) => {
  const isDisabled = props.isLoading || props.disabled;

  return (
    <WiniBox ui="search">
      <WiniGridLayout container ui="form" columnSpacing={1} rowSpacing={1}>
        <WiniGridItem xs={12}>
          <WiniText
            ui="row"
            label="자산명 또는 자산코드"
            variant="outlined"
            slotProps={{ inputLabel: { shrink: true } }}
            name="keyword"
            value={props.searchData?.keyword || ''}
            onChange={props.onSearchChange}
            disabled={isDisabled}
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
              disabled={isDisabled}
            >
              조회
            </WiniButton>,
          )}
          {winiCom.checkMenuAut(
            'select',
            <WiniButton
              ui="lineGray"
              onClick={props.onOpenQrScan}
              disabled={isDisabled}
            >
              QR 스캔
            </WiniButton>,
          )}
        </WiniBox>
      </WiniBox>
    </WiniBox>
  );
};
