import { useCallback, useRef, useState } from 'react';
import { getTangibleAssetList } from '../api/api';
import { winiCom } from '@/shared/lib';
import { winiMsg, menuStore, useInitialFetch } from '@/shared/model';

const EMPTY_PAGE_INFO = {
  currentPage: 0,
  pageSize: 20,
  totalElements: 0,
  totalPages: 0,
};

/**
 * 유형자산 목록 - 서버 페이지네이션(page/size/sort/keyword) 응답을 그대로 반영한다.
 * 응답 형태: { content: [...], currentPage, pageSize, totalElements, totalPages }
 */
export const useTangibleAssetList = () => {
  const [tangibleAssetList, setTangibleAssetList] = useState([]);
  const [pageInfo, setPageInfo] = useState(EMPTY_PAGE_INFO);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  const { connector, id: formMenuId } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  const formMenuIdRef = useRef(formMenuId);
  const loadingRef = useRef(false);

  connectorRef.current = connector;
  formMenuIdRef.current = formMenuId;

  const fetchTangibleAssetList = useCallback(async (params) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector || loadingRef.current) return null;

    const currentMenuId = menuStore.getCurrentMenuId();
    if (formMenuIdRef.current && currentMenuId !== formMenuIdRef.current)
      return null;

    loadingRef.current = true;
    setIsLoading(true);
    setError(null);
    try {
      const data = await getTangibleAssetList(currentConnector, params);
      if (data?.result === 'SUCCESS') {
        const pageData = data?.data || {};
        setTangibleAssetList(
          Array.isArray(pageData.content) ? pageData.content : [],
        );
        setPageInfo({
          currentPage: pageData.currentPage ?? 0,
          pageSize: pageData.pageSize ?? 20,
          totalElements: pageData.totalElements ?? 0,
          totalPages: pageData.totalPages ?? 0,
        });
      } else {
        const message = data?.message || '유형자산 목록 조회에 실패했습니다.';
        setError(message);
        winiMsg.showSnackbar(message);
      }
      return data;
    } catch {
      const message = '유형자산 목록 조회 중 오류가 발생했습니다.';
      setError(message);
      winiMsg.showSnackbar(message);
      return null;
    } finally {
      loadingRef.current = false;
      setIsLoading(false);
    }
  }, []);

  useInitialFetch(fetchTangibleAssetList, Boolean(connector));

  return {
    tangibleAssetList,
    pageInfo,
    isLoading,
    error,
    fetchTangibleAssetList,
  };
};
