import { useState, useCallback, useRef } from 'react';
import { getInventoryList } from '../api/api';
import { winiCom } from '@/shared/lib';
import { winiMsg, menuStore, useInitialFetch } from '@/shared/model';

const EMPTY_PAGE_INFO = { currentPage: 0, pageSize: 20, totalElements: 0, totalPages: 0 };

/**
 * 전수조사 목록 (S-300) - 서버 페이지네이션 응답을 그대로 반영한다.
 */
export const useInventoryList = () => {
  const [inventoryList, setInventoryList] = useState([]);
  const [pageInfo, setPageInfo] = useState(EMPTY_PAGE_INFO);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  const { connector, id: formMenuId } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  const formMenuIdRef = useRef(formMenuId);
  connectorRef.current = connector;
  formMenuIdRef.current = formMenuId;

  const fetchInventoryList = useCallback(async (params) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return null;

    const currentMenuId = menuStore.getCurrentMenuId();
    if (formMenuIdRef.current && currentMenuId !== formMenuIdRef.current) return null;

    setIsLoading(true);
    setError(null);
    try {
      const data = await getInventoryList(currentConnector, params);
      if (data?.result === 'SUCCESS') {
        const pageData = data?.data || {};
        setInventoryList(Array.isArray(pageData.content) ? pageData.content : []);
        setPageInfo({
          currentPage: pageData.currentPage ?? 0,
          pageSize: pageData.pageSize ?? 20,
          totalElements: pageData.totalElements ?? 0,
          totalPages: pageData.totalPages ?? 0,
        });
      } else {
        const message = data?.message || '전수조사 목록 조회에 실패했습니다.';
        setError(message);
        winiMsg.showSnackbar(message);
      }
      return data;
    } catch {
      const message = '전수조사 목록 조회 중 오류가 발생했습니다.';
      setError(message);
      winiMsg.showSnackbar(message);
      return null;
    } finally {
      setIsLoading(false);
    }
  }, []);

  useInitialFetch(fetchInventoryList, Boolean(connector));

  return {
    inventoryList,
    pageInfo,
    isLoading,
    error,
    fetchInventoryList,
  };
};
