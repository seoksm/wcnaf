import { WiniBox, WiniButton, WiniText } from '@/shared/ui/wini';
import { winiCom } from '@/shared/lib';

/** S-520 소프트웨어 편집기 */
export const Editor = ({ formData, onChange, onReset, onCreate, onUpdate, onDelete, disabled, isSubmitting, submittingAction }) => {
  return (
    <WiniBox ui="form" aria-busy={isSubmitting}>
      <WiniBox className="flex-1 flex flex-col gap-2">
        <WiniText
          ui="column"
          label="소프트웨어명"
          name="name"
          required
          disabled={disabled}
          value={formData.name || ''}
          slotProps={{ inputLabel: { shrink: true } }}
          className="w-full"
          onChange={onChange}
        />
        <WiniText
          ui="column"
          label="제조사"
          name="publisher"
          disabled={disabled}
          value={formData.publisher || ''}
          slotProps={{ inputLabel: { shrink: true } }}
          className="w-full"
          onChange={onChange}
        />
        <WiniText
          ui="column"
          label="분류"
          name="category"
          disabled={disabled}
          value={formData.category || ''}
          slotProps={{ inputLabel: { shrink: true } }}
          className="w-full"
          onChange={onChange}
        />
        <WiniText
          ui="column"
          label="메모"
          name="memo"
          multiline
          minRows={2}
          disabled={disabled}
          value={formData.memo || ''}
          className="w-full mb-2"
          onChange={onChange}
        />
      </WiniBox>
      <WiniBox ui="btnbox">
        <WiniBox ui="btnitem">
          {winiCom.checkMenuAut(
            'delete',
            <WiniButton
              ui="delete"
              className="w-20"
              onClick={onDelete}
              loading={submittingAction === 'delete'}
              disabled={disabled || !formData.softwareId}
            >
              삭제
            </WiniButton>,
          )}
        </WiniBox>
        <WiniBox ui="btnitem">
          <WiniButton ui="lineGray" className="w-20" onClick={onReset} disabled={disabled}>
            초기화
          </WiniButton>
          {winiCom.checkMenuAut(
            'update',
            <WiniButton
              ui="line"
              className="w-20"
              onClick={onUpdate}
              loading={submittingAction === 'update'}
              disabled={disabled || !formData.softwareId}
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
              disabled={disabled || !!formData.softwareId}
            >
              등록
            </WiniButton>,
          )}
        </WiniBox>
      </WiniBox>
    </WiniBox>
  );
};
