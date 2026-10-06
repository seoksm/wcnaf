import { useCallback, useRef, useState } from 'react';
import { fetchSoftwareList } from '@/entities/software';
import { winiCom } from '@/shared/lib';
import { winiMsg, menuStore, useInitialFetch } from '@/shared/model';

/** S-520 소프트웨어 마스터 목록 */
export const useSoftwareList = () => {
  const [list, setList] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  const { connector, id: formMenuId } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  const formMenuIdRef = useRef(formMenuId);
  connectorRef.current = connector;
  formMenuIdRef.current = formMenuId;

  const fetchList = useCallback(async (params) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return null;
    const currentMenuId = menuStore.getCurrentMenuId();
    if (formMenuIdRef.current && currentMenuId !== formMenuIdRef.current) return null;

    setIsLoading(true);
    try {
      const data = await fetchSoftwareList(currentConnector, params);
      if (data?.result === 'SUCCESS') {
        setList(Array.isArray(data?.data) ? data.data : []);
      } else {
        winiMsg.showSnackbar(data?.message || '소프트웨어 목록 조회 중 오류가 발생했습니다.');
      }
      return data;
    } catch {
      winiMsg.showSnackbar('소프트웨어 목록 조회 중 오류가 발생했습니다.');
      return null;
    } finally {
      setIsLoading(false);
    }
  }, []);

  useInitialFetch(fetchList, Boolean(connector));

  return { list, isLoading, fetchList };
};
