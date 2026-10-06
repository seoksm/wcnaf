import { useCallback, useRef, useState } from 'react';
import { getActivityLog } from '../api/api';
import { winiCom, winiDate } from '@/shared/lib';
import { winiMsg } from '@/shared/model';

/** 서버 페이지 크기와 동일 - 응답 건수가 이보다 적으면 다음 페이지가 없다는 뜻 */
const PAGE_SIZE = 200;

const EMPTY_FILTER = { historyType: '', assetCode: '', assetName: '', fromDate: null, toDate: null };

/** dayjs 객체 · Date · 문자열을 모두 받아 서버가 이해하는 ISO date-time 문자열로 변환 */
const toIsoDateTime = (value) => {
  if (!value) return undefined;
  if (typeof value.toISOString === 'function') return value.toISOString();
  const parsed = winiDate(value);
  return parsed?.isValid?.() ? parsed.toISOString() : undefined;
};

/**
 * 전체 활동 로그 조회 (S-221) - 이력유형·자산코드·자산명·로그발생시각(기간) 필터,
 * 스크롤다운 시 다음 200건을 이어서 불러온다(무한스크롤)
 */
export const useActivityLog = () => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;
  const requestInFlightRef = useRef(false);

  const [open, setOpen] = useState(false);
  const [filterDraft, setFilterDraft] = useState(EMPTY_FILTER);
  const [filterApplied, setFilterApplied] = useState(EMPTY_FILTER);
  const [log, setLog] = useState([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [isLoading, setIsLoading] = useState(false);

  const fetchPage = useCallback(
    async (filter, pageToLoad, append) => {
      const currentConnector = connectorRef.current;
      if (!currentConnector || requestInFlightRef.current) return;

      requestInFlightRef.current = true;
      setIsLoading(true);
      try {
        const data = await getActivityLog(
          currentConnector,
          {
            historyType: filter.historyType || undefined,
            assetCode: filter.assetCode || undefined,
            assetName: filter.assetName || undefined,
            fromDate: toIsoDateTime(filter.fromDate),
            toDate: toIsoDateTime(filter.toDate),
          },
          pageToLoad,
        );
        if (data?.result === 'SUCCESS') {
          const rows = Array.isArray(data?.data) ? data.data : [];
          setLog((prev) => (append ? [...prev, ...rows] : rows));
          setHasMore(rows.length === PAGE_SIZE);
          setPage(pageToLoad);
        }
      } catch {
        winiMsg.showSnackbar('전체 활동 로그 조회 중 오류가 발생했습니다.');
      } finally {
        requestInFlightRef.current = false;
        setIsLoading(false);
      }
    },
    [],
  );

  const openDialog = useCallback(async () => {
    setOpen(true);
    setFilterDraft(EMPTY_FILTER);
    setFilterApplied(EMPTY_FILTER);
    await fetchPage(EMPTY_FILTER, 0, false);
  }, [fetchPage]);

  const closeDialog = useCallback(() => setOpen(false), []);

  const onChangeFilter = useCallback((e) => {
    const { name, value } = e.target;
    setFilterDraft((prev) => ({ ...prev, [name]: value }));
  }, []);

  const onChangeDateFilter = useCallback(
    (field) => (e) => {
      const value = e?.target ? e.target.value : e;
      setFilterDraft((prev) => ({ ...prev, [field]: value }));
    },
    [],
  );

  const onSearch = useCallback(async () => {
    setFilterApplied(filterDraft);
    await fetchPage(filterDraft, 0, false);
  }, [filterDraft, fetchPage]);

  const loadMore = useCallback(async () => {
    if (isLoading || !hasMore) return;
    await fetchPage(filterApplied, page + 1, true);
  }, [isLoading, hasMore, filterApplied, page, fetchPage]);

  return {
    open,
    openDialog,
    closeDialog,
    filterDraft,
    onChangeFilter,
    onChangeDateFilter,
    onSearch,
    log,
    isLoading,
    hasMore,
    loadMore,
  };
};
