import { useState } from 'react';
import {
  WiniBox,
  WiniButton,
  WiniText,
  WiniTypography,
  WiniInputLabel,
} from '@/shared/ui/wini';

/**
 * 2차 인증(OTP 앱 코드 또는 백업코드) 입력 섹션 - 1차 인증(아이디/비밀번호) 통과 후에만 보인다.
 */
export const MfaVerifySection = ({ isLoading, handleVerifyMfaCode, cancelMfa }) => {
  const [code, setCode] = useState('');

  const onKeyDownCode = (e) => {
    if (e.keyCode === 13) {
      handleVerifyMfaCode(code);
    }
  };

  return (
    <WiniBox component="form" noValidate autoComplete="off">
      <WiniBox className="my-[30%]">
        <WiniTypography fontWeight={'bold'}>인증 코드 입력</WiniTypography>
        <WiniTypography variant="span" className="mt-2 block text-sm text-text-sub">
          OTP 앱에 표시된 6자리 코드를 입력하세요.
        </WiniTypography>
      </WiniBox>

      <WiniBox>
        <WiniInputLabel
          htmlFor="mfacode"
          className="block text-left text-[20px] font-bold text-[#010066]"
        >
          CODE
        </WiniInputLabel>
        <WiniText
          id="mfacode"
          autoFocus={true}
          className="min-w-[310px]"
          name="code"
          value={code}
          onChange={(e) => setCode(e.target.value)}
          onKeyDown={onKeyDownCode}
          disabled={isLoading}
          inputProps={{ inputMode: 'numeric', maxLength: 10 }}
        />
      </WiniBox>

      <WiniButton
        className="w-full mt-15"
        onClick={() => handleVerifyMfaCode(code)}
        disabled={isLoading || !code}
      >
        {isLoading ? 'LOADING...' : '확인'}
      </WiniButton>

      <WiniButton
        ui="lineGray"
        className="w-full mt-8"
        onClick={cancelMfa}
        disabled={isLoading}
      >
        다시 로그인
      </WiniButton>
    </WiniBox>
  );
};
