import { useCallback, useEffect, useRef, useState } from 'react';
import { fetchUser } from '@/entities/user';
import { enrollMfa, confirmMfaEnroll, disableMfa } from '../api/api';
import { winiMsg, userStore } from '@/shared/model';
import { winiCom } from '@/shared/lib';

/**
 * STATUS: 'loading' | 'disabled' | 'enrolling' | 'enabled'
 * - disabled: 미사용 - "OTP 앱 등록" 버튼만 노출
 * - enrolling: otpAuthUri/manualEntrySecret으로 QR·수동키 표시 + 코드 확인 입력 대기
 * - enabled: 사용 중 - "해제" 버튼만 노출
 */
export function useMfaSettings(connector) {
  const connectorRef = useRef(connector);
  connectorRef.current = connector;

  const [status, setStatus] = useState('loading');
  const [enrollData, setEnrollData] = useState(null); // { otpAuthUri, manualEntrySecret }
  const [backupCodes, setBackupCodes] = useState(null); // 등록 확정 직후 1회만 노출
  const [isSubmitting, setIsSubmitting] = useState(false);

  const loadStatus = useCallback(async () => {
    const userId = userStore.getUserId();
    if (!userId) return;

    try {
      const data = await fetchUser(connectorRef.current, userId);
      if (data?.result === 'SUCCESS') {
        setStatus(data.data?.totpEnabled ? 'enabled' : 'disabled');
      }
    } catch (error) {
      winiMsg.showSnackbar(winiCom.getErrorMessage(error?.response?.data?.message));
    }
  }, []);

  useEffect(() => {
    loadStatus();
  }, [loadStatus]);

  const startEnroll = useCallback(async () => {
    setIsSubmitting(true);
    try {
      const data = await enrollMfa(connectorRef.current);
      if (data?.result === 'SUCCESS') {
        setEnrollData(data.data);
        setStatus('enrolling');
      } else {
        winiMsg.showSnackbar(data?.message || 'OTP 등록을 시작하지 못했습니다.');
      }
    } catch (error) {
      winiMsg.showSnackbar(winiCom.getErrorMessage(error?.response?.data?.message));
    } finally {
      setIsSubmitting(false);
    }
  }, []);

  const confirmEnroll = useCallback(async (code) => {
    setIsSubmitting(true);
    try {
      const data = await confirmMfaEnroll(connectorRef.current, code);
      if (data?.result === 'SUCCESS') {
        setBackupCodes(data.data || []);
        setEnrollData(null);
        setStatus('enabled');
        winiMsg.showSnackbar('OTP 앱 등록이 완료되었습니다.');
      } else {
        winiMsg.showSnackbar(data?.message || '인증 코드가 올바르지 않습니다.');
      }
    } catch (error) {
      winiMsg.showSnackbar(winiCom.getErrorMessage(error?.response?.data?.message) || '인증 코드가 올바르지 않습니다.');
    } finally {
      setIsSubmitting(false);
    }
  }, []);

  const cancelEnroll = useCallback(() => {
    setEnrollData(null);
    setStatus('disabled');
  }, []);

  const disable = useCallback(async () => {
    const answer = await winiMsg.showConfirm('MFA(OTP 앱) 사용을 해제하시겠습니까?\n기존 백업코드도 모두 폐기됩니다.');
    if (answer !== 'Y') return;

    setIsSubmitting(true);
    try {
      const data = await disableMfa(connectorRef.current);
      if (data?.result === 'SUCCESS') {
        setStatus('disabled');
        setBackupCodes(null);
        winiMsg.showSnackbar('MFA가 해제되었습니다.');
      } else {
        winiMsg.showSnackbar(data?.message || 'MFA 해제 중 오류가 발생했습니다.');
      }
    } catch (error) {
      winiMsg.showSnackbar(winiCom.getErrorMessage(error?.response?.data?.message));
    } finally {
      setIsSubmitting(false);
    }
  }, []);

  const dismissBackupCodes = useCallback(() => setBackupCodes(null), []);

  return {
    status,
    enrollData,
    backupCodes,
    isSubmitting,
    startEnroll,
    confirmEnroll,
    cancelEnroll,
    disable,
    dismissBackupCodes,
  };
}
