import {
  WiniBox,
  WiniButton,
  WiniGridItem,
  WiniGridLayout,
  WiniNumber,
  WiniText,
} from '@/shared/ui/wini';
import { winiCom } from '@/shared/lib';

/**
 * 자산 종류 편집기
 */
export const Editor = ({
  formData,
  categoryCodeDisabled,
  deletable,
  onChange,
  onReset,
  onCreate,
  onUpdate,
  onDelete,
  disabled,
  isSubmitting,
  submittingAction,
}) => {
  return (
    <WiniBox ui="form" aria-busy={isSubmitting}>
      <WiniGridLayout container ui="form" columnSpacing={1} rowSpacing={1}>
        <WiniGridItem xs={12}>
          <WiniText
            ui="column"
            label="종류 코드"
            name="categoryCode"
            required
            disabled={disabled || categoryCodeDisabled}
            value={formData.categoryCode || ''}
            slotProps={{ inputLabel: { shrink: true } }}
            className="w-full"
            onChange={onChange}
          />
        </WiniGridItem>
        <WiniGridItem xs={12}>
          <WiniText
            ui="column"
            label="종류명"
            name="categoryName"
            required
            disabled={disabled}
            value={formData.categoryName || ''}
            slotProps={{ inputLabel: { shrink: true } }}
            className="w-full"
            onChange={onChange}
          />
        </WiniGridItem>
        <WiniGridItem xs={12}>
          <WiniNumber
            ui="column"
            label="정렬순서"
            name="sortSeq"
            value={formData.sortSeq}
            disabled={disabled}
            slotProps={{ inputLabel: { shrink: true } }}
            className="w-full"
            onChange={onChange}
            inputProps={{
              allowNegative: false,
              decimalScale: 0,
              inputMode: 'numeric',
            }}
            inputSx={{ '& input': { textAlign: 'left' } }}
          />
        </WiniGridItem>
        <WiniGridItem xs={12}>
          <WiniNumber
            ui="column"
            label="내용연수(개월)"
            name="usefulLifeMonths"
            value={formData.usefulLifeMonths}
            disabled={disabled}
            slotProps={{ inputLabel: { shrink: true } }}
            className="w-full"
            onChange={onChange}
            inputProps={{
              allowNegative: false,
              decimalScale: 0,
              inputMode: 'numeric',
            }}
            inputSx={{ '& input': { textAlign: 'left' } }}
          />
        </WiniGridItem>
        <WiniGridItem xs={12}>
          <WiniNumber
            ui="column"
            label="잔존가치율(%)"
            name="residualRate"
            value={formData.residualRate}
            disabled={disabled}
            slotProps={{ inputLabel: { shrink: true } }}
            className="w-full"
            onChange={onChange}
            inputProps={{
              allowNegative: false,
              decimalScale: 2,
              inputMode: 'decimal',
            }}
            inputSx={{ '& input': { textAlign: 'left' } }}
          />
        </WiniGridItem>
        <WiniGridItem xs={12}>
          <WiniNumber
            ui="column"
            label="비망가액(원)"
            name="memorandumValue"
            value={formData.memorandumValue}
            disabled={disabled}
            slotProps={{ inputLabel: { shrink: true } }}
            className="w-full mb-2"
            onChange={onChange}
            inputProps={{
              allowNegative: false,
              decimalScale: 0,
              inputMode: 'numeric',
            }}
            inputSx={{ '& input': { textAlign: 'left' } }}
          />
        </WiniGridItem>
      </WiniGridLayout>
      <WiniBox ui="btnbox">
        <WiniBox ui="btnitem">
          {winiCom.checkMenuAut(
            'delete',
            <WiniButton
              ui="delete"
              className="w-20"
              onClick={onDelete}
              loading={submittingAction === 'delete'}
              disabled={disabled || !deletable}
            >
              삭제
            </WiniButton>,
          )}
        </WiniBox>
        <WiniBox ui="btnitem">
          <WiniButton
            ui="lineGray"
            className="w-20"
            onClick={onReset}
            disabled={disabled}
          >
            초기화
          </WiniButton>
          {winiCom.checkMenuAut(
            'update',
            <WiniButton
              ui="line"
              className="w-20"
              onClick={onUpdate}
              loading={submittingAction === 'update'}
              disabled={disabled || !formData.categoryId}
            >
              수정
            </WiniButton>,
          )}
          {winiCom.checkMenuAut(
            'insert',
            <WiniButton
              ui="default"
              className="w-20"
              onClick={onCreate}
              loading={submittingAction === 'create'}
              disabled={disabled || !!formData.categoryId}
            >
              등록
            </WiniButton>,
          )}
        </WiniBox>
      </WiniBox>
    </WiniBox>
  );
};
