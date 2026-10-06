import {
  WiniBox,
  WiniButton,
  WiniGridItem,
  WiniGridLayout,
  WiniMenuItem,
  WiniNumber,
  WiniSelect,
  WiniSwitch,
  WiniTypography,
} from '@/shared/ui/wini';

const HALF_FIELD_SIZE = { md: 6, xs: 12 };
const NUMERIC_PROPS = { allowNegative: false, decimalScale: 0, inputMode: 'numeric' };

const RETURN_STATUS_OPTIONS = [
  { value: 'USE', label: '사용' },
  { value: 'STORAGE', label: '보관' },
  { value: 'REPAIR', label: '수리중' },
  { value: 'DISUSE', label: '불용' },
  { value: 'DISPOSED', label: '처분완료' },
];

/**
 * S-400 프로세스 설정 - §1 파급 매트릭스대로, 토글이 켜져야만 그 부속 정책이 나타난다.
 * "파급 안내" 문구는 설계문서 §1 매트릭스를 그대로 사용자 언어로 옮긴 것.
 */
export const ProcessConfigForm = ({ form, isSaving, onChange, onToggle, onSave }) => {
  return (
    <WiniBox className="flex flex-col gap-4">
      <WiniBox ui="info" className="p-4">
        <WiniBox className="mb-2 flex items-center justify-between">
          <WiniTypography variant="h6">대여 프로세스</WiniTypography>
          <WiniSwitch checked={!!form.loanEnabled} onChange={onToggle('loanEnabled')} />
        </WiniBox>
        <WiniTypography variant="span" className="mb-3 block text-sm text-text-sub">
          켜면 관리자 메뉴에 대여 현황이, 임직원 하단탭에 대여 탭이 추가되고, 자산 배정형태에
          대여가능·대여중이 생깁니다. QR 스캔으로 대여/반납 액션이 추가되며, 반납 연체 알림이
          발송됩니다.
        </WiniTypography>

        {form.loanEnabled ? (
          <WiniGridLayout container columnSpacing={2} rowSpacing={1}>
            <WiniGridItem item size={HALF_FIELD_SIZE}>
              <WiniNumber
                ui="column"
                label="전역 기본 대여 기한(일)"
                name="defaultLoanDays"
                value={form.defaultLoanDays ?? ''}
                className="w-full"
                onChange={onChange}
                inputProps={NUMERIC_PROPS}
                slotProps={{ inputLabel: { shrink: true } }}
              />
            </WiniGridItem>
            <WiniGridItem item size={HALF_FIELD_SIZE}>
              <WiniNumber
                ui="column"
                label="대여 연장 최대 횟수"
                name="maxExtendCount"
                value={form.maxExtendCount ?? ''}
                className="w-full"
                onChange={onChange}
                inputProps={NUMERIC_PROPS}
                slotProps={{ inputLabel: { shrink: true } }}
              />
            </WiniGridItem>
            <WiniGridItem item size={HALF_FIELD_SIZE}>
              <WiniNumber
                ui="column"
                label="1인 동시 대여 한도 (비우면 제한 없음)"
                name="concurrentLimit"
                value={form.concurrentLimit ?? ''}
                className="w-full"
                onChange={onChange}
                inputProps={NUMERIC_PROPS}
                slotProps={{ inputLabel: { shrink: true } }}
              />
            </WiniGridItem>
            <WiniGridItem item size={HALF_FIELD_SIZE}>
              <WiniSwitch
                label="대여 시 관리자 승인 필요"
                checked={!!form.requireApproval}
                onChange={onToggle('requireApproval')}
              />
            </WiniGridItem>
            <WiniGridItem item size={HALF_FIELD_SIZE}>
              <WiniSwitch
                label="연체 중 신규 대여 차단"
                checked={!!form.blockOnOverdue}
                onChange={onToggle('blockOnOverdue')}
              />
            </WiniGridItem>
          </WiniGridLayout>
        ) : null}
      </WiniBox>

      <WiniBox ui="info" className="p-4">
        <WiniBox className="mb-2 flex items-center justify-between">
          <WiniTypography variant="h6">수령·반납 승인 프로세스</WiniTypography>
          <WiniSwitch checked={!!form.acknowledgementEnabled} onChange={onToggle('acknowledgementEnabled')} />
        </WiniBox>
        <WiniTypography variant="span" className="mb-3 block text-sm text-text-sub">
          켜면 관리자 메뉴에 확인서 현황이 추가되고, 임직원 홈에 승인 대기 카드가 나타납니다.
          자산 상세에 확인서 요청·확인서 탭이 추가되며, 미승인 자산은 설정에 따라 조회가
          차단됩니다.
        </WiniTypography>

        {form.acknowledgementEnabled ? (
          <WiniGridLayout container columnSpacing={2} rowSpacing={1}>
            <WiniGridItem item size={HALF_FIELD_SIZE}>
              <WiniNumber
                ui="column"
                label="확인서 승인 기한(일)"
                name="approvalDueDays"
                value={form.approvalDueDays ?? ''}
                className="w-full"
                onChange={onChange}
                inputProps={NUMERIC_PROPS}
                slotProps={{ inputLabel: { shrink: true } }}
              />
            </WiniGridItem>
            <WiniGridItem item size={HALF_FIELD_SIZE}>
              <WiniNumber
                ui="column"
                label="리마인드 재발송 주기(일)"
                name="remindIntervalDays"
                value={form.remindIntervalDays ?? ''}
                className="w-full"
                onChange={onChange}
                inputProps={NUMERIC_PROPS}
                slotProps={{ inputLabel: { shrink: true } }}
              />
            </WiniGridItem>
            <WiniGridItem item size={HALF_FIELD_SIZE}>
              <WiniSelect
                ui="column"
                label="반납 승인 시 기본 제안 상태"
                name="defaultReturnStatus"
                className="w-full"
                value={form.defaultReturnStatus || ''}
                onChange={onChange}
              >
                {RETURN_STATUS_OPTIONS.map((option) => (
                  <WiniMenuItem key={option.value} value={option.value}>
                    {option.label}
                  </WiniMenuItem>
                ))}
              </WiniSelect>
            </WiniGridItem>
            <WiniGridItem item size={HALF_FIELD_SIZE}>
              <WiniSwitch
                label="배정 시 확인서 자동 요청"
                checked={!!form.autoRequestOnAssign}
                onChange={onToggle('autoRequestOnAssign')}
              />
            </WiniGridItem>
            <WiniGridItem item size={HALF_FIELD_SIZE}>
              <WiniSwitch
                label="반납 시 담당자 승인 필수"
                checked={!!form.requireManagerApproval}
                onChange={onToggle('requireManagerApproval')}
              />
            </WiniGridItem>
            <WiniGridItem item size={HALF_FIELD_SIZE}>
              <WiniSwitch
                label="미승인 자산 임직원 조회 차단"
                checked={!!form.blockUnapprovedView}
                onChange={onToggle('blockUnapprovedView')}
              />
            </WiniGridItem>
          </WiniGridLayout>
        ) : null}
      </WiniBox>

      <WiniBox ui="btnbox" className="justify-end">
        <WiniBox ui="btnitem">
          <WiniButton ui="line" className="w-20" onClick={onSave} loading={isSaving} disabled={isSaving}>
            저장
          </WiniButton>
        </WiniBox>
      </WiniBox>
    </WiniBox>
  );
};
