import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import { WiniBox, WiniTypography } from '@/shared/ui/wini';
import { useAckTemplateForm, AckTemplateForm } from '@/features/ackTemplate/manage';

/** S-433 확인서 문구 관리 */
export const AckTemplateManagementPage = () => {
  const ctl = useAckTemplateForm();

  return (
    <WiniFormNormal>
      {ctl.isLoading ? (
        <WiniBox className="p-6 text-center">
          <WiniTypography variant="span" className="text-text-sub">불러오는 중...</WiniTypography>
        </WiniBox>
      ) : (
        <AckTemplateForm
          activeType={ctl.activeType}
          onTypeChange={ctl.setActiveType}
          body={ctl.body}
          onBodyChange={ctl.setBody}
          textareaRef={ctl.textareaRef}
          onInsertVariable={ctl.insertVariable}
          isSaving={ctl.isSaving}
          onSave={ctl.save}
        />
      )}
    </WiniFormNormal>
  );
};

export default AckTemplateManagementPage;
