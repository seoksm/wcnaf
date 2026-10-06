import {
  WiniBox,
  WiniButton,
  WiniDatePicker,
  WiniGridItem,
  WiniGridLayout,
  WiniMenuItem,
  WiniNumber,
  WiniSelect,
  WiniText,
} from '@/shared/ui/wini';
import { EnumSelect } from '@/shared/ui/enum-select';
import { winiCom, winiDate } from '@/shared/lib';
import {
  ASSIGN_TYPE_LABEL,
  LIFE_STATUS_LABEL,
  requiresMember,
} from '@/entities/tangibleAsset';

const FormField = ({ children }) => (
  <WiniGridItem size={{ xs: 12 }}>{children}</WiniGridItem>
);

/** 유형자산 등록·수정 폼 */
export const Editor = ({
  formData,
  categoryList,
  locationList,
  userList,
  onChange,
  onReset,
  onCreate,
  onUpdate,
  isSaving,
  isOptionsLoading,
  disabled,
}) => {
  const isDisabled = disabled || isSaving;

  return (
    <WiniBox ui="form">
      <WiniGridLayout container ui="form" rowSpacing={1} rowItem={1}>
        {formData.assetCode && (
          <FormField>
            <WiniText
              ui="column"
              label="자산코드"
              name="assetCode"
              disabled
              value={formData.assetCode || ''}
              slotProps={{ inputLabel: { shrink: true } }}
              className="w-full"
            />
          </FormField>
        )}

        <FormField>
          <WiniText
            ui="column"
            label="자산명"
            name="assetName"
            required
            value={formData.assetName || ''}
            slotProps={{ inputLabel: { shrink: true } }}
            className="w-full"
            onChange={onChange}
            disabled={isDisabled}
          />
        </FormField>

        <FormField>
          <WiniSelect
            ui="column"
            label="자산 종류"
            name="categoryId"
            required
            value={formData.categoryId || ''}
            slotProps={{ inputLabel: { shrink: true } }}
            displayEmpty
            className="w-full"
            onChange={onChange}
            disabled={isDisabled || isOptionsLoading}
          >
            {(categoryList || []).map((category) => (
              <WiniMenuItem
                value={category.categoryId}
                key={category.categoryId}
              >
                {category.categoryName}
              </WiniMenuItem>
            ))}
          </WiniSelect>
        </FormField>

        <FormField>
          <WiniSelect
            ui="column"
            label="자산 위치"
            name="locationId"
            required
            value={formData.locationId || ''}
            slotProps={{ inputLabel: { shrink: true } }}
            displayEmpty
            className="w-full"
            onChange={onChange}
            disabled={isDisabled || isOptionsLoading}
          >
            {(locationList || []).map((location) => (
              <WiniMenuItem
                value={location.locationId}
                key={location.locationId}
              >
                {location.locationName}
              </WiniMenuItem>
            ))}
          </WiniSelect>
        </FormField>

        <FormField>
          <EnumSelect
            ui="column"
            label="생애 상태"
            name="lifeStatus"
            value={formData.lifeStatus}
            enums={LIFE_STATUS_LABEL}
            slotProps={{ inputLabel: { shrink: true } }}
            onChange={onChange}
            disabled={isDisabled}
          />
        </FormField>

        <FormField>
          <EnumSelect
            ui="column"
            label="배정 형태"
            name="assignType"
            value={formData.assignType}
            enums={ASSIGN_TYPE_LABEL}
            slotProps={{ inputLabel: { shrink: true } }}
            onChange={onChange}
            disabled={isDisabled}
          />
        </FormField>

        {requiresMember(formData.assignType) && (
          <FormField>
            <WiniSelect
              ui="column"
              label="배정 사용자"
              name="currentMemberId"
              required
              value={formData.currentMemberId || ''}
              slotProps={{ inputLabel: { shrink: true } }}
              displayEmpty
              className="w-full"
              onChange={onChange}
              disabled={isDisabled || isOptionsLoading}
            >
              {(userList || []).map((user) => (
                <WiniMenuItem value={user.id} key={user.id}>
                  {user.fullName || user.username}
                </WiniMenuItem>
              ))}
            </WiniSelect>
          </FormField>
        )}

        <FormField>
          <WiniDatePicker
            ui="column"
            label="취득일"
            required
            name="acquisitionDate"
            value={formData.acquisitionDate || ''}
            slotProps={{ inputLabel: { shrink: true } }}
            className="w-full"
            disabled={isDisabled}
            onChange={(event) => {
              const raw = event?.value ?? event?.target?.value;
              onChange({
                target: {
                  name: 'acquisitionDate',
                  value: raw ? winiDate.dateFormat(raw, 'YYYY-MM-DD') : '',
                },
              });
            }}
          />
        </FormField>

        <FormField>
          <WiniNumber
            ui="column"
            label="취득가액"
            name="acquisitionAmount"
            required
            value={formData.acquisitionAmount}
            slotProps={{ inputLabel: { shrink: true } }}
            className="w-full"
            onChange={onChange}
            disabled={isDisabled}
            inputProps={{
              allowNegative: false,
              decimalScale: 0,
              inputMode: 'numeric',
            }}
            inputSx={{ '& input': { textAlign: 'left' } }}
          />
        </FormField>

        <FormField>
          <WiniText
            ui="column"
            label="모델명"
            name="modelName"
            value={formData.modelName || ''}
            slotProps={{ inputLabel: { shrink: true } }}
            className="w-full"
            onChange={onChange}
            disabled={isDisabled}
          />
        </FormField>
        <FormField>
          <WiniText
            ui="column"
            label="제조사"
            name="manufacturer"
            value={formData.manufacturer || ''}
            slotProps={{ inputLabel: { shrink: true } }}
            className="w-full"
            onChange={onChange}
            disabled={isDisabled}
          />
        </FormField>
        <FormField>
          <WiniText
            ui="column"
            label="시리얼번호"
            name="serialNo"
            value={formData.serialNo || ''}
            slotProps={{ inputLabel: { shrink: true } }}
            className="w-full"
            onChange={onChange}
            disabled={isDisabled}
          />
        </FormField>
        <FormField>
          <WiniText
            ui="column"
            label="메모"
            name="memo"
            multiline
            minRows={2}
            value={formData.memo || ''}
            slotProps={{ inputLabel: { shrink: true } }}
            className="mb-2 w-full"
            onChange={onChange}
            disabled={isDisabled}
          />
        </FormField>
      </WiniGridLayout>

      <WiniBox ui="btnbox">
        <WiniBox ui="btnitem" />
        <WiniBox ui="btnitem">
          <WiniButton
            ui="lineGray"
            className="w-20"
            onClick={onReset}
            disabled={isSaving}
          >
            초기화
          </WiniButton>
          {winiCom.checkMenuAut(
            'update',
            <WiniButton
              ui="line"
              className="w-20"
              onClick={onUpdate}
              loading={isSaving}
              disabled={!formData.tangibleAssetId || isDisabled}
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
              loading={isSaving}
              disabled={!!formData.tangibleAssetId || isDisabled}
            >
              등록
            </WiniButton>,
          )}
        </WiniBox>
      </WiniBox>
    </WiniBox>
  );
};
