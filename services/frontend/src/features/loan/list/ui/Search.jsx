import { WiniBox, WiniButton, WiniGridLayout, WiniGridItem } from '@/shared/ui/wini';
import { EnumSelect } from '@/shared/ui/enum-select';
import { winiCom } from '@/shared/lib';
import { LOAN_STATUS_LABEL } from '@/entities/loan';

export const Search = ({ searchData, onSearchChange, onSearch, isLoading, extraActions }) => {
  return (
    <WiniBox ui="search">
      <WiniGridLayout container ui="form" columnSpacing={1} rowSpacing={1}>
        <WiniGridItem xs={12}>
          <EnumSelect
            ui="row"
            label="상태"
            name="status"
            value={searchData?.status || ''}
            enums={LOAN_STATUS_LABEL}
            showAll
            allLabel="전체"
            className="w-full"
            onChange={onSearchChange}
          />
        </WiniGridItem>
      </WiniGridLayout>
      <WiniBox ui="btnbox">
        <WiniBox ui="btnitem" />
        <WiniBox ui="btnitem">
          {winiCom.checkMenuAut(
            'select',
            <WiniButton onClick={onSearch} loading={isLoading} disabled={isLoading}>
              조회
            </WiniButton>,
          )}
          {extraActions}
        </WiniBox>
      </WiniBox>
    </WiniBox>
  );
};
