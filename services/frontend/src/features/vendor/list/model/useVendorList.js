import { useCallback, useRef, useState } from 'react';
import { fetchVendors } from '@/entities/vendor';
import { winiCom } from '@/shared/lib';
import { winiMsg, menuStore, useInitialFetch } from '@/shared/model';

/** S-540 공급사 목록 */
export const useVendorList = () => {
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
      const data = await fetchVendors(currentConnector, params);
      if (data?.result === 'SUCCESS') {
        setList(Array.isArray(data?.data) ? data.data : []);
      } else {
        winiMsg.showSnackbar(data?.message || '공급사 목록 조회 중 오류가 발생했습니다.');
      }
      return data;
    } catch {
      winiMsg.showSnackbar('공급사 목록 조회 중 오류가 발생했습니다.');
      return null;
    } finally {
      setIsLoading(false);
    }
  }, []);

  useInitialFetch(fetchList, Boolean(connector));

  return { list, isLoading, fetchList };
};
