import { useCallback, useRef, useState } from 'react';
import { getLoanList } from '../api/api';
import { winiCom } from '@/shared/lib';
import { winiMsg, menuStore, useInitialFetch } from '@/shared/model';

const EMPTY_PAGE_INFO = { currentPage: 0, pageSize: 20, totalElements: 0, totalPages: 0 };

/**
 * S-410 대여 현황 - 서버 페이지네이션(page/size/status) 응답을 그대로 반영한다.
 * 응답 형태: { content: [...], currentPage, pageSize, totalElements, totalPages }
 */
export const useLoanList = () => {
  const [loanList, setLoanList] = useState([]);
  const [pageInfo, setPageInfo] = useState(EMPTY_PAGE_INFO);
  const [isLoading, setIsLoading] = useState(false);

  const { connector, id: formMenuId } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  const formMenuIdRef = useRef(formMenuId);
  connectorRef.current = connector;
  formMenuIdRef.current = formMenuId;

  const fetchLoans = useCallback(async (params) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return null;

    const currentMenuId = menuStore.getCurrentMenuId();
    if (formMenuIdRef.current && currentMenuId !== formMenuIdRef.current) return null;

    setIsLoading(true);
    try {
      const data = await getLoanList(currentConnector, params);
      if (data?.result === 'SUCCESS') {
        const pageData = data?.data || {};
        setLoanList(Array.isArray(pageData.content) ? pageData.content : []);
        setPageInfo({
          currentPage: pageData.currentPage ?? 0,
          pageSize: pageData.pageSize ?? 20,
          totalElements: pageData.totalElements ?? 0,
          totalPages: pageData.totalPages ?? 0,
        });
      } else {
        winiMsg.showSnackbar(data?.message || '대여 현황 조회 중 오류가 발생했습니다.');
      }
      return data;
    } catch {
      winiMsg.showSnackbar('대여 현황 조회 중 오류가 발생했습니다.');
      return null;
    } finally {
      setIsLoading(false);
    }
  }, []);

  useInitialFetch(fetchLoans, Boolean(connector));

  return { loanList, pageInfo, isLoading, fetchLoans };
};
