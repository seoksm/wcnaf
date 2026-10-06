import { useCallback, useRef, useState } from 'react';
import { fetchAckTemplates, updateAckTemplate, findUndefinedVariables } from '@/entities/ackTemplate';
import { winiCom } from '@/shared/lib';
import { winiMsg, menuStore, useInitialFetch } from '@/shared/model';

/**
 * S-433 확인서 문구 관리 - 편집은 이후 요청에만 적용된다(K7). textareaRef로 실제 DOM의
 * selectionStart/selectionEnd를 읽어 칩 클릭 시 커서 위치에 변수를 삽입한다(설계문서 §4) -
 * WiniText는 `ref`를 주면 MUI TextField 규칙대로 바깥 FormControl에 연결되지만, `inputRef`를
 * 주면 내부 실제 textarea DOM에 그대로 연결된다(probe로 실측 확인: selectionStart 등 정상 동작).
 */
export const useAckTemplateForm = () => {
  const { connector, id: formMenuId } = winiCom.getFormInfo('Y');
  const connectorRef = useRef(connector);
  const formMenuIdRef = useRef(formMenuId);
  connectorRef.current = connector;
  formMenuIdRef.current = formMenuId;

  const [templates, setTemplates] = useState({});
  const [activeType, setActiveType] = useState('RECEIPT');
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const savingRef = useRef(false);
  const textareaRef = useRef(null);

  const load = useCallback(async () => {
    const currentConnector = connectorRef.current;
    if (!currentConnector) return;
    const currentMenuId = menuStore.getCurrentMenuId();
    if (formMenuIdRef.current && currentMenuId !== formMenuIdRef.current) return;

    setIsLoading(true);
    try {
      const data = await fetchAckTemplates(currentConnector);
      if (data?.result === 'SUCCESS') {
        const map = {};
        (data.data || []).forEach((t) => { map[t.type] = t.bodyTpl; });
        setTemplates(map);
      } else {
        winiMsg.showSnackbar(data?.message || '확인서 문구 조회 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('확인서 문구 조회 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useInitialFetch(load, Boolean(connector));

  const body = templates[activeType] || '';

  const setBody = useCallback((value) => {
    setTemplates((prev) => ({ ...prev, [activeType]: value }));
  }, [activeType]);

  const insertVariable = useCallback((name) => {
    const token = `{{${name}}}`;
    const el = textareaRef.current;
    if (!el) {
      setBody(body + token);
      return;
    }
    const start = el.selectionStart ?? body.length;
    const end = el.selectionEnd ?? body.length;
    const next = body.slice(0, start) + token + body.slice(end);
    setBody(next);
    requestAnimationFrame(() => {
      el.focus();
      const caret = start + token.length;
      el.setSelectionRange(caret, caret);
    });
  }, [body, setBody]);

  const save = useCallback(async () => {
    if (savingRef.current) return;
    const undefinedVars = findUndefinedVariables(body);
    if (undefinedVars.length > 0) {
      winiMsg.showAlert(`정의되지 않은 변수가 있습니다: ${undefinedVars.map((v) => `{{${v}}}`).join(', ')}`);
      return;
    }
    savingRef.current = true;
    setIsSaving(true);
    try {
      const data = await updateAckTemplate(connectorRef.current, activeType, body);
      if (data?.result === 'SUCCESS') {
        winiMsg.showSnackbar('저장되었습니다. 이후 요청부터 적용됩니다.');
      } else {
        winiMsg.showSnackbar(data?.message || '저장 중 오류가 발생했습니다.');
      }
    } catch {
      winiMsg.showSnackbar('저장 중 오류가 발생했습니다.');
    } finally {
      savingRef.current = false;
      setIsSaving(false);
    }
  }, [activeType, body]);

  return { activeType, setActiveType, body, setBody, textareaRef, insertVariable, isLoading, isSaving, save };
};
