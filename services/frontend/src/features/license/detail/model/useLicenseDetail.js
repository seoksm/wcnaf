import { useCallback, useRef, useState } from 'react';
import {
  fetchLicensePurchaseRecords,
  addLicensePurchaseRecord,
  fetchLicenseAssignedUsers,
  assignLicenseUser,
  releaseLicenseAssignedUser,
  fetchLicenseIncludedSoftware,
  addLicenseIncludedSoftware,
  removeLicenseIncludedSoftware,
  deleteLicense,
} from '@/entities/license';
import { fetchVendors } from '@/entities/vendor';
import { fetchCommonUsers } from '@/entities/tangibleAsset';
import { winiCom, winiDate } from '@/shared/lib';
import { winiMsg } from '@/shared/model';

const EMPTY_NEW_RECORD = { vendorId: '', purchaseDate: '', quantity: '', unitPrice: '', totalAmount: '', memo: '' };
const EMPTY_NEW_ASSIGN = { memberId: '', licensePurchaseRecordId: '' };

/** S-532/533 라이선스 상세 - 구매내역·배정 사용자·포함 SW를 한 화면에서 관리한다 */
export const useLicenseDetail = ({ onChanged }) => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;

  const [selectedRow, setSelectedRow] = useState(null);
  const [purchaseRecords, setPurchaseRecords] = useState([]);
  const [assignedUsers, setAssignedUsers] = useState([]);
  const [includedSoftware, setIncludedSoftware] = useState([]);
  const [vendorList, setVendorList] = useState([]);
  const [userList, setUserList] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isActing, setIsActing] = useState(false);
  const actingRef = useRef(false);
  const [newRecord, setNewRecord] = useState(EMPTY_NEW_RECORD);
  const [newAssign, setNewAssign] = useState(EMPTY_NEW_ASSIGN);
  const [newSoftwareName, setNewSoftwareName] = useState('');

  const userNameById = useCallback((memberId) => {
    const user = userList.find((u) => u.id === memberId);
    return user ? (user.fullName || user.username) : memberId;
  }, [userList]);

  const loadAll = useCallback(async (licenseId) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return;
    setIsLoading(true);
    try {
      const [recordsRes, usersRes, softwareRes] = await Promise.all([
        fetchLicensePurchaseRecords(currentConnector, licenseId),
        fetchLicenseAssignedUsers(currentConnector, licenseId),
        fetchLicenseIncludedSoftware(currentConnector, licenseId),
      ]);
      if (recordsRes?.result === 'SUCCESS') setPurchaseRecords(recordsRes.data || []);
      if (usersRes?.result === 'SUCCESS') setAssignedUsers(usersRes.data || []);
      if (softwareRes?.result === 'SUCCESS') setIncludedSoftware(softwareRes.data || []);
    } catch {
      winiMsg.showSnackbar('상세 정보 조회 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  const openDetail = useCallback((row) => {
    setSelectedRow(row);
    setNewRecord(EMPTY_NEW_RECORD);
    setNewAssign(EMPTY_NEW_ASSIGN);
    setNewSoftwareName('');
    loadAll(row.licenseId);
    const currentConnector = connectorRef.current;
    if (currentConnector) {
      fetchVendors(currentConnector).then((data) => {
        if (data?.result === 'SUCCESS') setVendorList(data.data || []);
      });
      fetchCommonUsers(currentConnector).then((data) => {
        if (data?.result === 'SUCCESS') setUserList(data.data || []);
      });
    }
  }, [loadAll]);

  const closeDetail = useCallback(() => setSelectedRow(null), []);

  /**
   * 총액은 기본적으로 수량×단가로 자동 계산한다 - 수량 또는 단가를 바꿀 때만 재계산하고,
   * 총액 자체를 직접 수정하는 입력(name === 'totalAmount')은 그대로 반영해 수동 override를
   * 허용한다(예: 할인·번들 구매처럼 단가×수량과 실제 총액이 다른 경우).
   */
  const handleNewRecordChange = useCallback((name, value) => {
    setNewRecord((prev) => {
      const next = { ...prev, [name]: value };
      if (name === 'quantity' || name === 'unitPrice') {
        const quantity = Number(next.quantity);
        const unitPrice = Number(next.unitPrice);
        if (next.quantity !== '' && next.unitPrice !== '' && Number.isFinite(quantity) && Number.isFinite(unitPrice)) {
          next.totalAmount = String(quantity * unitPrice);
        }
      }
      return next;
    });
  }, []);

  /** WiniDatePicker 전용 - 다른 날짜 입력 화면과 동일한 표준 컨트롤로 맞춘다 */
  const handleNewRecordDateChange = useCallback((name) => (event) => {
    const raw = event?.value ?? event?.target?.value;
    setNewRecord((prev) => ({ ...prev, [name]: raw ? winiDate.dateFormat(raw, 'YYYY-MM-DD') : '' }));
  }, []);

  /**
   * 구매내역 추가/배정/회수/포함SW 추가·삭제/라이선스 삭제가 공유하는 단일 처리 지점.
   * actingRef 가드로 확인창이 열린 시점부터 빠른 연타에도 API가 한 번만 호출되게 하고,
   * 성공/실패 메시지와 후처리(onSuccess)만 호출부에서 다르게 지정한다.
   */
  const runAction = useCallback(async (action, { successMessage, errorMessage, onSuccess } = {}) => {
    if (actingRef.current || !connectorRef.current) return false;
    actingRef.current = true;
    setIsActing(true);
    try {
      const data = await action();
      if (data?.result === 'SUCCESS') {
        if (successMessage) winiMsg.showSnackbar(successMessage);
        await onSuccess?.();
        return true;
      }
      winiMsg.showSnackbar(data?.message || errorMessage || '처리 중 오류가 발생했습니다.');
      return false;
    } catch {
      winiMsg.showSnackbar(errorMessage || '처리 중 오류가 발생했습니다.');
      return false;
    } finally {
      actingRef.current = false;
      setIsActing(false);
    }
  }, []);

  const addPurchaseRecord = useCallback(async () => {
    if (!newRecord.purchaseDate) {
      winiMsg.showAlert('구매일을 입력해주세요.');
      return;
    }
    if (!newRecord.quantity) {
      winiMsg.showAlert('수량을 입력해주세요.');
      return;
    }
    await runAction(
      () => addLicensePurchaseRecord(connectorRef.current, selectedRow.licenseId, {
        vendorId: newRecord.vendorId || undefined,
        purchaseDate: newRecord.purchaseDate,
        quantity: Number(newRecord.quantity),
        unitPrice: newRecord.unitPrice ? Number(newRecord.unitPrice) : undefined,
        totalAmount: newRecord.totalAmount ? Number(newRecord.totalAmount) : undefined,
        memo: newRecord.memo || undefined,
      }),
      {
        successMessage: '구매내역이 추가되었습니다.',
        errorMessage: '추가 중 오류가 발생했습니다.',
        onSuccess: async () => {
          setNewRecord(EMPTY_NEW_RECORD);
          await loadAll(selectedRow.licenseId);
          await onChanged?.();
        },
      },
    );
  }, [selectedRow, newRecord, loadAll, onChanged, runAction]);

  const handleNewAssignChange = useCallback((name, value) => {
    setNewAssign((prev) => ({ ...prev, [name]: value }));
  }, []);

  const assignUser = useCallback(async () => {
    if (!newAssign.memberId) {
      winiMsg.showAlert('배정할 임직원을 선택해주세요.');
      return;
    }
    await runAction(
      () => assignLicenseUser(connectorRef.current, selectedRow.licenseId, {
        memberId: newAssign.memberId,
        licensePurchaseRecordId: newAssign.licensePurchaseRecordId || undefined,
      }),
      {
        successMessage: '배정되었습니다.',
        errorMessage: '배정 중 오류가 발생했습니다.',
        onSuccess: async () => {
          setNewAssign(EMPTY_NEW_ASSIGN);
          await loadAll(selectedRow.licenseId);
          await onChanged?.();
        },
      },
    );
  }, [selectedRow, newAssign, loadAll, onChanged, runAction]);

  const releaseUser = useCallback(async (licenseAssignedUserId) => {
    const answer = await winiMsg.showConfirm('배정을 회수하시겠습니까?');
    if (answer !== 'Y') return;

    await runAction(
      () => releaseLicenseAssignedUser(connectorRef.current, licenseAssignedUserId),
      {
        successMessage: '회수되었습니다.',
        errorMessage: '회수 중 오류가 발생했습니다.',
        onSuccess: async () => {
          await loadAll(selectedRow.licenseId);
          await onChanged?.();
        },
      },
    );
  }, [selectedRow, loadAll, onChanged, runAction]);

  const addIncludedSoftware = useCallback(async () => {
    if (!newSoftwareName.trim()) {
      winiMsg.showAlert('소프트웨어명을 입력해주세요.');
      return;
    }
    await runAction(
      () => addLicenseIncludedSoftware(connectorRef.current, selectedRow.licenseId, newSoftwareName.trim()),
      {
        successMessage: '추가되었습니다.',
        errorMessage: '추가 중 오류가 발생했습니다.',
        onSuccess: async () => {
          setNewSoftwareName('');
          await loadAll(selectedRow.licenseId);
        },
      },
    );
  }, [selectedRow, newSoftwareName, loadAll, runAction]);

  const removeIncludedSoftware = useCallback(async (licenseIncludedSoftwareId) => {
    await runAction(
      () => removeLicenseIncludedSoftware(connectorRef.current, licenseIncludedSoftwareId),
      {
        errorMessage: '삭제 중 오류가 발생했습니다.',
        onSuccess: () => loadAll(selectedRow.licenseId),
      },
    );
  }, [selectedRow, loadAll, runAction]);

  const removeLicense = useCallback(async () => {
    const answer = await winiMsg.showConfirm('라이선스를 삭제(사용 해제) 하시겠습니까?');
    if (answer !== 'Y') return;

    await runAction(
      () => deleteLicense(connectorRef.current, selectedRow.licenseId),
      {
        successMessage: '삭제되었습니다.',
        errorMessage: '삭제 중 오류가 발생했습니다.',
        onSuccess: async () => {
          setSelectedRow(null);
          await onChanged?.();
        },
      },
    );
  }, [selectedRow, onChanged, runAction]);

  return {
    selectedRow, purchaseRecords, assignedUsers, includedSoftware, vendorList, userList,
    isLoading, isActing, newRecord, newAssign, newSoftwareName,
    userNameById,
    openDetail, closeDetail,
    handleNewRecordChange, handleNewRecordDateChange, addPurchaseRecord,
    handleNewAssignChange, assignUser, releaseUser,
    setNewSoftwareName, addIncludedSoftware, removeIncludedSoftware,
    removeLicense,
  };
};
