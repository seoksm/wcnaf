import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import { WiniBox, WiniTypography } from '@/shared/ui/wini';
import { useProcessConfigForm, ProcessConfigForm } from '@/features/processConfig/manage';

/**
 * S-400 프로세스 설정 - 대여/수령·반납 프로세스 On-Off와 부속 정책. 워크스페이스당 단일 행이라
 * 목록 없이 폼 하나만 있다(P-3).
 */
export const ProcessConfigSettingsPage = () => {
  const { form, isLoading, isSaving, handleChange, handleToggle, save } = useProcessConfigForm();

  return (
    <WiniFormNormal>
      {isLoading || !form ? (
        <WiniBox className="p-6 text-center">
          <WiniTypography variant="span" className="text-text-sub">불러오는 중...</WiniTypography>
        </WiniBox>
      ) : (
        <ProcessConfigForm
          form={form}
          isSaving={isSaving}
          onChange={handleChange}
          onToggle={handleToggle}
          onSave={save}
        />
      )}
    </WiniFormNormal>
  );
};

export default ProcessConfigSettingsPage;
