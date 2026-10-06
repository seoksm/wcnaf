import { WiniBox, WiniButton, WiniCheckbox, WiniTypography } from '@/shared/ui/wini';
import { ACK_TYPE_LABEL } from '@/entities/ackTemplate';

/** S-440 확인서 승인 상세 - K4: 본문 스크롤 + 체크박스 동의 + 승인 버튼 2단계 확인 */
export const MyAcknowledgementDetail = ({ detail, isLoading, agreed, onAgreedChange, isApproving, onApprove, onBack }) => {
  if (isLoading || !detail) {
    return (
      <WiniBox className="p-6 text-center">
        <WiniTypography variant="span" className="text-text-sub">불러오는 중...</WiniTypography>
      </WiniBox>
    );
  }

  return (
    <WiniBox className="mx-auto flex max-w-[480px] flex-col gap-3 p-4">
      <WiniBox className="flex items-center justify-between">
        <WiniButton ui="lineGray" onClick={onBack}>목록으로</WiniButton>
        <WiniTypography variant="span" className="text-sm font-semibold">{ACK_TYPE_LABEL[detail.type]}확인서</WiniTypography>
      </WiniBox>

      <WiniTypography variant="span" className="text-sm font-semibold">
        {detail.assetName} ({detail.assetCode})
      </WiniTypography>

      <WiniBox className="max-h-[320px] overflow-y-auto whitespace-pre-wrap rounded border border-solid border-gray-200 bg-white p-3 text-sm">
        {detail.bodySnapshot}
      </WiniBox>

      <WiniCheckbox
        label="위 내용을 확인하였으며 동의합니다."
        checked={agreed}
        onChange={(e) => onAgreedChange(e.target.checked)}
      />

      <WiniTypography variant="span" className="text-xs text-text-sub">
        승인 시 승인자·일시·접속정보가 기록됩니다.
      </WiniTypography>

      <WiniButton onClick={onApprove} disabled={!agreed || isApproving} loading={isApproving} className="w-full">
        승인
      </WiniButton>
    </WiniBox>
  );
};
