import { WiniBox, WiniButton, WiniText, WiniTypography } from '@/shared/ui/wini';
import { ACK_TEMPLATE_VARIABLES, ACK_TYPE_LABEL } from '@/entities/ackTemplate';

const TYPES = ['RECEIPT', 'RETURN'];

export const AckTemplateForm = ({ activeType, onTypeChange, body, onBodyChange, textareaRef, onInsertVariable, isSaving, onSave }) => {
  return (
    <WiniBox className="flex flex-col gap-3">
      <WiniBox className="flex gap-2">
        {TYPES.map((type) => (
          <WiniButton
            key={type}
            ui={activeType === type ? 'default' : 'lineGray'}
            onClick={() => onTypeChange(type)}
          >
            {ACK_TYPE_LABEL[type]}확인서
          </WiniButton>
        ))}
      </WiniBox>

      <WiniBox>
        <WiniTypography variant="span" className="mb-1 block text-sm text-text-sub">
          변수를 클릭하면 커서 위치에 삽입됩니다. 저장 시 정의되지 않은 변수가 있으면 저장이 차단됩니다.
        </WiniTypography>
        <WiniBox className="flex flex-wrap gap-1.5">
          {ACK_TEMPLATE_VARIABLES.map((name) => (
            <WiniButton
              key={name}
              ui="lineGray"
              onClick={() => onInsertVariable(name)}
              className="rounded-full! px-3! py-1! text-xs! font-medium! normal-case"
            >
              {`{{${name}}}`}
            </WiniButton>
          ))}
        </WiniBox>
      </WiniBox>

      <WiniText
        inputRef={textareaRef}
        value={body}
        onChange={(e) => onBodyChange(e.target.value)}
        multiline
        minRows={10}
        className="w-full"
        inputSx={{ fontSize: '0.875rem', lineHeight: 1.6 }}
      />

      <WiniBox ui="btnbox" className="justify-end">
        <WiniBox ui="btnitem">
          <WiniButton ui="line" className="w-20" onClick={onSave} loading={isSaving} disabled={isSaving}>저장</WiniButton>
        </WiniBox>
      </WiniBox>
    </WiniBox>
  );
};
