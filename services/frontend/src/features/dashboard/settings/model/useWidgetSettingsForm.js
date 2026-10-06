import { useEffect, useState } from 'react';
import { WIDGET_LABEL } from '@/entities/dashboard';

/** S-701 대시보드 설정 - 열릴 때마다 현재 설정을 임시 편집본으로 복제해, 취소하면 원본에 영향 없다. */
export const useWidgetSettingsForm = ({ open, widgetConfig }) => {
  const [draft, setDraft] = useState([]);

  useEffect(() => {
    if (open) {
      setDraft([...widgetConfig].sort((a, b) => a.sortOrder - b.sortOrder));
    }
  }, [open, widgetConfig]);

  const toggleVisible = (widgetKey) => {
    setDraft((prev) => prev.map((c) => (c.widgetKey === widgetKey ? { ...c, visible: !c.visible } : c)));
  };

  const move = (index, direction) => {
    setDraft((prev) => {
      const targetIndex = index + direction;
      if (targetIndex < 0 || targetIndex >= prev.length) return prev;
      const next = [...prev];
      [next[index], next[targetIndex]] = [next[targetIndex], next[index]];
      return next.map((c, i) => ({ ...c, sortOrder: i }));
    });
  };

  const rows = draft.map((config) => ({ ...config, label: WIDGET_LABEL[config.widgetKey] || config.widgetKey }));
  const buildSavePayload = () => draft.map((config, index) => ({ ...config, sortOrder: index }));

  return { rows, toggleVisible, move, buildSavePayload };
};
