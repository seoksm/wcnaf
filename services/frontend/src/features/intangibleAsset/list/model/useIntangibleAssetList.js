import { useCallback, useRef, useState } from 'react';
import { fetchIntangibleAssets } from '@/entities/intangibleAsset';
import { winiCom } from '@/shared/lib';
import { winiMsg, menuStore, useInitialFetch } from '@/shared/model';

const EMPTY_PAGE_INFO = { currentPage: 0, pageSize: 20, totalElements: 0, totalPages: 0 };

/** S-500 무형자산 목록 - 기본 필터는 "90일 내 만료"(설계문서 §2) */
export const useIntangibleAssetList = () => {
  const [list, setList] = useState([]);
  const [pageInfo, setPageInfo] = useState(EMPTY_PAGE_INFO);
  const [isLoading, setIsLoading] = useState(false);

  const { connector, id: formMenuId } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;
  const formMenuIdRef = useRef(formMenuId);
  formMenuIdRef.current = formMenuId;

  const fetchList = useCallback(async (params) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return null;
    const currentMenuId = menuStore.getCurrentMenuId();
    if (formMenuIdRef.current && currentMenuId !== formMenuIdRef.current) return null;

    setIsLoading(true);
    try {
      const data = await fetchIntangibleAssets(currentConnector, params);
      if (data?.result === 'SUCCESS') {
        const pageData = data?.data || {};
        setList(Array.isArray(pageData.content) ? pageData.content : []);
        setPageInfo({
          currentPage: pageData.currentPage ?? 0,
          pageSize: pageData.pageSize ?? 20,
          totalElements: pageData.totalElements ?? 0,
          totalPages: pageData.totalPages ?? 0,
        });
      } else {
        winiMsg.showSnackbar(data?.message || '무형자산 목록 조회 중 오류가 발생했습니다.');
      }
      return data;
    } catch {
      winiMsg.showSnackbar('무형자산 목록 조회 중 오류가 발생했습니다.');
      return null;
    } finally {
      setIsLoading(false);
    }
  }, []);

  useInitialFetch(() => fetchList({ withinDays: 90 }), Boolean(connector));

  return { list, pageInfo, isLoading, fetchList };
};
