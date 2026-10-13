import { useEffect, useState } from 'react';
import QRCode from 'qrcode';
import {
  WiniBox,
  WiniButton,
  WiniText,
  WiniTypography,
} from '@/shared/ui/wini';

const CodeConfirmForm = ({ enrollData, isSubmitting, onConfirm, onCancel }) => {
  const [code, setCode] = useState('');
  const [qrDataUrl, setQrDataUrl] = useState(null);

  useEffect(() => {
    let cancelled = false;

    if (enrollData?.otpAuthUri) {
      QRCode.toDataURL(enrollData.otpAuthUri, { margin: 1, width: 200 }).then((url) => {
        if (!cancelled) setQrDataUrl(url);
      });
    }

    return () => {
      cancelled = true;
    };
  }, [enrollData?.otpAuthUri]);

  return (
    <WiniBox className="flex flex-col gap-3">
      <WiniTypography variant="span" className="block text-sm text-text-default">
        Google Authenticator 또는 Microsoft Authenticator 앱으로 아래 QR코드를 스캔하세요.
      </WiniTypography>

      {qrDataUrl && (
        <WiniBox ui="noAutoGap" className="flex justify-center">
          <img src={qrDataUrl} alt="MFA 등록 QR코드" width={200} height={200} />
        </WiniBox>
      )}

      <WiniTypography variant="span" className="block text-xs text-text-sub">
        카메라를 쓸 수 없다면 앱에 아래 키를 직접 입력하세요.
      </WiniTypography>
      <WiniTypography variant="span" className="block break-all rounded border border-solid border-gray-200 bg-gray-50 px-2 py-1 font-mono text-sm">
        {enrollData?.manualEntrySecret}
      </WiniTypography>

      <WiniText
        ui="column"
        label="앱에 표시된 6자리 코드"
        name="code"
        value={code}
        onChange={(e) => setCode(e.target.value)}
        disabled={isSubmitting}
        className="w-full"
      />

      <WiniBox className="flex gap-2">
        <WiniButton onClick={() => onConfirm(code)} loading={isSubmitting} disabled={isSubmitting || !code}>
          확인
        </WiniButton>
        <WiniButton ui="lineGray" onClick={onCancel} disabled={isSubmitting}>
          취소
        </WiniButton>
      </WiniBox>
    </WiniBox>
  );
};

const BackupCodesView = ({ backupCodes, onDismiss }) => (
  <WiniBox className="flex flex-col gap-3">
    <WiniTypography variant="span" className="block text-sm font-bold text-red-600">
      백업코드 (이 화면을 벗어나면 다시 볼 수 없습니다)
    </WiniTypography>
    <WiniTypography variant="span" className="block text-xs text-text-sub">
      휴대폰을 분실했을 때 이 코드로 로그인할 수 있습니다. 안전한 곳에 따로 적어 보관하세요.
      분실 시 재발급은 본인이 직접 할 수 없고 관리자 승인을 거쳐야 합니다.
    </WiniTypography>
    <WiniBox className="grid grid-cols-2 gap-2 rounded border border-solid border-gray-200 bg-gray-50 p-3 font-mono text-sm">
      {backupCodes.map((code) => (
        <WiniTypography variant="span" key={code}>{code}</WiniTypography>
      ))}
    </WiniBox>
    <WiniButton onClick={onDismiss} className="w-fit">저장했습니다</WiniButton>
  </WiniBox>
);

/**
 * 마이페이지 - 본인 MFA(TOTP) 등록/해제 섹션. 개인 선택(opt-in)이므로 강제 노출 없이
 * 설정 화면 한 군데에만 둔다.
 */
export const MfaSettingsSection = ({ mfaModel }) => {
  const { status, enrollData, backupCodes, isSubmitting, startEnroll, confirmEnroll, cancelEnroll, disable, dismissBackupCodes } = mfaModel;

  return (
    <WiniBox className="mt-6 rounded border border-solid border-gray-200 p-4">
      <WiniTypography variant="h3" className="mb-2 text-base font-bold">2단계 인증(MFA)</WiniTypography>

      {status === 'loading' && (
        <WiniTypography variant="span" className="text-sm text-text-sub">불러오는 중...</WiniTypography>
      )}

      {status === 'disabled' && (
        <WiniBox className="flex flex-col gap-2">
          <WiniTypography variant="span" className="block text-sm text-text-sub">
            Google Authenticator / Microsoft Authenticator 앱으로 로그인 시 2차 인증을 추가할 수 있습니다.
          </WiniTypography>
          <WiniButton onClick={startEnroll} loading={isSubmitting} disabled={isSubmitting} className="w-fit">
            OTP 앱 등록
          </WiniButton>
        </WiniBox>
      )}

      {status === 'enrolling' && (
        <CodeConfirmForm
          enrollData={enrollData}
          isSubmitting={isSubmitting}
          onConfirm={confirmEnroll}
          onCancel={cancelEnroll}
        />
      )}

      {status === 'enabled' && backupCodes && (
        <BackupCodesView backupCodes={backupCodes} onDismiss={dismissBackupCodes} />
      )}

      {status === 'enabled' && !backupCodes && (
        <WiniBox className="flex flex-col gap-2">
          <WiniTypography variant="span" className="block text-sm text-text-default">
            OTP 앱 2차 인증이 사용 중입니다.
          </WiniTypography>
          <WiniButton ui="lineGray" onClick={disable} loading={isSubmitting} disabled={isSubmitting} className="w-fit">
            MFA 해제
          </WiniButton>
        </WiniBox>
      )}
    </WiniBox>
  );
};
