import { useCallback, useRef, useState } from 'react';
import { getAssetLocationList } from '../api/api';
import { winiCom } from '@/shared/lib';
import { winiMsg, menuStore, useInitialFetch } from '@/shared/model';

export const useAssetLocationList = () => {
  const [assetLocationList, setAssetLocationList] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  const { connector, id: formMenuId } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  const formMenuIdRef = useRef(formMenuId);
  const loadingRef = useRef(false);

  connectorRef.current = connector;
  formMenuIdRef.current = formMenuId;

  const fetchAssetLocationList = useCallback(async () => {
    const currentConnector = connectorRef.current;
    if (!currentConnector || loadingRef.current) return null;

    const currentMenuId = menuStore.getCurrentMenuId();
    if (formMenuIdRef.current && currentMenuId !== formMenuIdRef.current)
      return null;

    loadingRef.current = true;
    setIsLoading(true);
    setError(null);
    try {
      const data = await getAssetLocationList(currentConnector);
      if (data?.result === 'SUCCESS') {
        setAssetLocationList(Array.isArray(data?.data) ? data.data : []);
      } else {
        const message = data?.message || '자산 위치 목록 조회에 실패했습니다.';
        setError(message);
        winiMsg.showSnackbar(message);
      }
      return data;
    } catch (err) {
      setError(err);
      winiMsg.showSnackbar('자산 위치 목록 조회 중 오류가 발생했습니다.');
      return null;
    } finally {
      loadingRef.current = false;
      setIsLoading(false);
    }
  }, []);

  useInitialFetch(fetchAssetLocationList, Boolean(connector));

  return {
    assetLocationList,
    isLoading,
    error,
    fetchAssetLocationList,
  };
};
