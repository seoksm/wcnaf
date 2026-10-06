import { useCallback, useEffect, useRef, useState } from 'react';
import { winiCom } from '@/shared/lib';
import { winiMsg } from '@/shared/model';
import { fetchAssignmentHistory, fetchCommonUsers, fetchTangibleAsset, releaseAssignment } from '../api/api';

export const useAssetScanDetail = (tangibleAssetId) => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;

  const [asset, setAsset] = useState(null);
  const [history, setHistory] = useState([]);
  const [userList, setUserList] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isReleasing, setIsReleasing] = useState(false);
  const releasingRef = useRef(false);
  const [notFound, setNotFound] = useState(false);
  const [error, setError] = useState(null);

  const fetchAll = useCallback(async () => {
    const currentConnector = connectorRef.current;
    if (!currentConnector || !tangibleAssetId) return;

    setIsLoading(true);
    setNotFound(false);
    setError(null);
    try {
      // 자산 기본 정보는 실패하면 화면 전체를 못 찾음/오류로 처리해야 하지만, 배정 이력·사용자
      // 목록(부가 정보)은 실패해도 자산 상세 자체는 그대로 보여줘야 한다 - Promise.all이었다면
      // 부가 정보 하나만 실패해도 전체가 실패로 묶여 정상 조회된 자산 정보까지 가려졌다.
      const [assetResult, historyResult, userResult] = await Promise.allSettled([
        fetchTangibleAsset(currentConnector, tangibleAssetId),
        fetchAssignmentHistory(currentConnector, tangibleAssetId),
        fetchCommonUsers(currentConnector),
      ]);

      if (assetResult.status === 'rejected') {
        throw assetResult.reason;
      }
      const assetRes = assetResult.value;
      if (assetRes?.result !== 'SUCCESS' || !assetRes.data) {
        setAsset(null);
        setNotFound(true);
        return;
      }

      setAsset(assetRes.data);
      setHistory(
        historyResult.status === 'fulfilled' && historyResult.value?.result === 'SUCCESS' && Array.isArray(historyResult.value.data)
          ? historyResult.value.data
          : [],
      );
      setUserList(
        userResult.status === 'fulfilled' && userResult.value?.result === 'SUCCESS' && Array.isArray(userResult.value.data)
          ? userResult.value.data
          : [],
      );
    } catch (requestError) {
      const status = requestError?.response?.status;
      if (status === 404) {
        setNotFound(true);
      } else {
        setError('자산 정보를 불러오지 못했습니다.');
        winiMsg.showSnackbar('자산 정보를 불러오지 못했습니다.');
      }
    } finally {
      setIsLoading(false);
    }
  }, [tangibleAssetId]);

  useEffect(() => {
    fetchAll();
  }, [fetchAll]);

  const release = useCallback(async () => {
    const currentConnector = connectorRef.current;
    if (!currentConnector || !tangibleAssetId || releasingRef.current) return;

    releasingRef.current = true;
    const answer = await winiMsg.showConfirm('현재 배정을 회수하시겠습니까?');
    if (answer !== 'Y') {
      releasingRef.current = false;
      return;
    }

    setIsReleasing(true);
    try {
      const data = await releaseAssignment(currentConnector, tangibleAssetId);
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('회수 되었습니다.');
        await fetchAll();
      } else {
        winiMsg.showSnackbar(data?.message || '회수 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('회수 중 오류가 발생했습니다.');
    } finally {
      releasingRef.current = false;
      setIsReleasing(false);
    }
  }, [fetchAll, tangibleAssetId]);

  return { asset, history, userList, isLoading, isReleasing, notFound, error, release };
};
