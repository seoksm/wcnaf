import {
  WiniBox,
  WiniButton,
  WiniGridLayout,
  WiniGridItem,
} from '@/shared/ui/wini';
import { EnumSelect } from '@/shared/ui/enum-select';
import { winiCom } from '@/shared/lib';
import { LIFE_STATUS_LABEL } from '@/entities/tangibleAsset';

const DISPOSAL_LIFE_STATUS_LABEL = {
  DISUSE: LIFE_STATUS_LABEL.DISUSE,
  DISPOSED: LIFE_STATUS_LABEL.DISPOSED,
};

export const Search = (props) => {
  return (
    <WiniBox ui="search">
      <WiniGridLayout container ui="form" columnSpacing={1} rowSpacing={1}>
        <WiniGridItem xs={12}>
          <EnumSelect
            ui="row"
            label="생애상태"
            name="lifeStatus"
            value={props.searchData?.lifeStatus || ''}
            enums={DISPOSAL_LIFE_STATUS_LABEL}
            showAll
            allLabel="전체(불용+처분완료)"
            className="w-48"
            onChange={props.onSearchChange}
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
