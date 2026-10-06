import { useState, useCallback, useRef } from 'react';
import { getDisposalAssetList } from '../api/api';
import { winiCom } from '@/shared/lib';
import { winiMsg, menuStore, useInitialFetch } from '@/shared/model';

const EMPTY_PAGE_INFO = { currentPage: 0, pageSize: 20, totalElements: 0, totalPages: 0 };

/**
 * 불용자산 목록 (S-240) - 서버 페이지네이션(page/size/sort/lifeStatus) 응답을 그대로 반영한다.
 * 응답 형태: { content: [...], currentPage, pageSize, totalElements, totalPages }
 */
export const useDisposalAssetList = () => {
  const [disposalAssetList, setDisposalAssetList] = useState([]);
  const [pageInfo, setPageInfo] = useState(EMPTY_PAGE_INFO);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  const { connector, id: formMenuId } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;
  const formMenuIdRef = useRef(formMenuId);
  formMenuIdRef.current = formMenuId;

  const fetchDisposalAssetList = useCallback(async (params) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return null;

    const currentMenuId = menuStore.getCurrentMenuId();
    if (formMenuIdRef.current && currentMenuId !== formMenuIdRef.current) return null;

    setIsLoading(true);
    setError(null);
    try {
      const data = await getDisposalAssetList(currentConnector, params);
      if (data?.result === 'SUCCESS') {
        const pageData = data?.data || {};
        setDisposalAssetList(Array.isArray(pageData.content) ? pageData.content : []);
        setPageInfo({
          currentPage: pageData.currentPage ?? 0,
          pageSize: pageData.pageSize ?? 20,
          totalElements: pageData.totalElements ?? 0,
          totalPages: pageData.totalPages ?? 0,
        });
      }
      return data;
    } catch (err) {
      setError(err);
      winiMsg.showSnackbar('불용자산 목록 조회 중 오류가 발생했습니다.');
      return null;
    } finally {
      setIsLoading(false);
    }
  }, []);

  // 최초 1회 로드 - connector가 준비되면 자동으로 부른다(필터 없이 불용+처분완료 전체 조회)
  useInitialFetch(fetchDisposalAssetList, Boolean(connector));

  return {
    disposalAssetList,
    pageInfo,
    isLoading,
    error,
    fetchDisposalAssetList,
  };
};
