import { useCallback, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  fetchDashboardSummary, fetchDashboardWidgetConfig, saveDashboardWidgetConfig,
  WIDGET_KEYS, PROCESS_DEPENDENT_WIDGETS, defaultWidgetConfig,
} from '@/entities/dashboard';
import { fetchProcessConfig } from '@/entities/processConfig';
import { winiCom } from '@/shared/lib';
import { winiMsg, menuStore, useInitialFetch } from '@/shared/model';

/**
 * S-700/701 대시보드 - 위젯 데이터·개인화 설정·프로세스 설정을 한 번에 불러와, 설정에 따라
 * 표시 대상만 순서대로 골라준다(캐시 없이 매 진입 실시간 집계, DashboardServiceImpl과 동일한 결정).
 */
export const useDashboard = () => {
  const { connector, id: formMenuId } = winiCom.getFormInfo('Y');
  const navigate = useNavigate();

  const [summary, setSummary] = useState(null);
  const [widgetConfig, setWidgetConfig] = useState(defaultWidgetConfig());
  const [processConfig, setProcessConfig] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [settingsOpen, setSettingsOpen] = useState(false);
  const [isSaving, setIsSaving] = useState(false);

  const load = useCallback(async () => {
    if (!connector) return;
    const currentMenuId = menuStore.getCurrentMenuId();
    if (formMenuId && currentMenuId !== formMenuId) return;

    setIsLoading(true);
    try {
      const [summaryRes, configRes, processRes] = await Promise.all([
        fetchDashboardSummary(connector),
        fetchDashboardWidgetConfig(connector),
        fetchProcessConfig(connector),
      ]);

      if (summaryRes?.result === 'SUCCESS') {
        setSummary(summaryRes.data);
      } else {
        winiMsg.showSnackbar(summaryRes?.message || '대시보드 조회 중 오류가 발생했습니다.');
      }
      if (configRes?.result === 'SUCCESS' && Array.isArray(configRes.data) && configRes.data.length > 0) {
        setWidgetConfig(configRes.data);
      }
      if (processRes?.result === 'SUCCESS') {
        setProcessConfig(processRes.data);
      }
    } catch {
      winiMsg.showSnackbar('대시보드 조회 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  }, [connector, formMenuId]);

  useInitialFetch(load, Boolean(connector));

  /** 표시 대상 위젯 키를 sortOrder 순으로 - 저장된 설정이 없는 위젯은 기본값(표시함)으로 취급 */
  const visibleWidgets = useMemo(() => {
    const byKey = new Map(widgetConfig.map((c) => [c.widgetKey, c]));
    return WIDGET_KEYS
      .map((key, index) => byKey.get(key) || { widgetKey: key, visible: true, sortOrder: index })
      .filter((c) => c.visible)
      .filter((c) => {
        const processField = PROCESS_DEPENDENT_WIDGETS[c.widgetKey];
        // 프로세스 설정을 아직 못 불러왔으면 일단 보여준다(로딩 중 깜빡임보다 정보 손실을 피한다)
        return !processField || !processConfig || Boolean(processConfig[processField]);
      })
      .sort((a, b) => a.sortOrder - b.sortOrder)
      .map((c) => c.widgetKey);
  }, [widgetConfig, processConfig]);

  const openSettings = useCallback(() => setSettingsOpen(true), []);
  const closeSettings = useCallback(() => setSettingsOpen(false), []);

  const saveSettings = useCallback(async (nextConfig) => {
    setIsSaving(true);
    try {
      const res = await saveDashboardWidgetConfig(connector, nextConfig);
      if (res?.result === 'SUCCESS') {
        setWidgetConfig(nextConfig);
        setSettingsOpen(false);
        winiMsg.showSnackbar('저장되었습니다.');
      } else {
        winiMsg.showSnackbar(res?.message || '위젯 설정 저장 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('위젯 설정 저장 중 오류가 발생했습니다.');
    } finally {
      setIsSaving(false);
    }
  }, [connector]);

  const resetSettings = useCallback(() => saveSettings(defaultWidgetConfig()), [saveSettings]);

  // 드릴다운(설계문서 "막대·범례·표 행을 클릭하면 필터가 적용된 목록으로 이동") 중 필터 전달은
  // 대상 화면 쪽 쿼리 파라미터 처리가 필요해 이번 범위에서는 제외하고, 화면 이동까지만 제공한다.
  const goToTicketManagement = useCallback(() => navigate('/ticket-management'), [navigate]);
  const goToIntangibleAssetManagement = useCallback(() => navigate('/intangible-asset-management'), [navigate]);

  return {
    summary,
    isLoading,
    visibleWidgets,
    widgetConfig,
    settingsOpen,
    openSettings,
    closeSettings,
    saveSettings,
    resetSettings,
    isSaving,
    goToTicketManagement,
    goToIntangibleAssetManagement,
    refresh: load,
  };
};
