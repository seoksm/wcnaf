import { useCallback, useRef, useState } from 'react';
import { fetchInventoryReport, fetchInventoryReportExcel } from '../api/api';
import { winiCom, downloadBlob } from '@/shared/lib';
import { winiMsg } from '@/shared/model';

/**
 * S-305 리포트 - 화면은 진행중/종료 모두 볼 수 있다(§4). PDF는 이 서비스에 PDF 생성 인프라가
 * 없어 미구현이고, "참고용" 엑셀 다운로드만 제공한다(운영 문서에 한계로 기록).
 */
export const useInventoryReport = () => {
  const { connector } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  connectorRef.current = connector;

  const [open, setOpen] = useState(false);
  const [report, setReport] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isDownloading, setIsDownloading] = useState(false);
  const reportRef = useRef(report);
  reportRef.current = report;

  const openReport = useCallback(async (inventoryId) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector || !inventoryId) return;
    setOpen(true);
    setIsLoading(true);
    setReport(null);
    try {
      const data = await fetchInventoryReport(currentConnector, inventoryId);
      if (data?.result === 'SUCCESS') {
        setReport(data.data);
      } else {
        winiMsg.showSnackbar(data?.message || '리포트 조회 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('리포트 조회 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  const closeReport = useCallback(() => {
    setOpen(false);
    setReport(null);
  }, []);

  const downloadExcel = useCallback(async (inventoryId) => {
    const currentConnector = connectorRef.current;
    if (!currentConnector || !inventoryId) return;
    setIsDownloading(true);
    try {
      const blob = await fetchInventoryReportExcel(currentConnector, inventoryId);
      downloadBlob(blob, `전수조사_리포트_${reportRef.current?.title || inventoryId}.xlsx`);
    } catch {
      winiMsg.showSnackbar('리포트 엑셀 다운로드 중 오류가 발생했습니다.');
    } finally {
      setIsDownloading(false);
    }
  }, []);

  return { open, report, isLoading, isDownloading, openReport, closeReport, downloadExcel };
};
