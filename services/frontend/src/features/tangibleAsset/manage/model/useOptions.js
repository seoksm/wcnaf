import { useCallback, useRef, useState } from 'react';
import {
  fetchAssetCategories,
  fetchAssetLocations,
  fetchCommonUsers,
} from '../api/api';
import { winiCom } from '@/shared/lib';
import { winiMsg, menuStore, useInitialFetch } from '@/shared/model';

/**
 * 등록/수정 폼의 드롭다운(종류·위치·사용자) 옵션을 로드한다.
 */
export const useTangibleAssetOptions = () => {
  const { connector, id: formMenuId } = winiCom.getFormInfo('Y');

  const [categoryList, setCategoryList] = useState([]);
  const [locationList, setLocationList] = useState([]);
  const [userList, setUserList] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isReady, setIsReady] = useState(false);
  const [error, setError] = useState(null);

  const connectorRef = useRef(connector);
  const formMenuIdRef = useRef(formMenuId);
  const loadingRef = useRef(false);

  connectorRef.current = connector;
  formMenuIdRef.current = formMenuId;

  const loadOptions = useCallback(async () => {
    const currentConnector = connectorRef.current;
    if (!currentConnector || loadingRef.current) return null;

    const currentMenuId = menuStore.getCurrentMenuId();
    if (formMenuIdRef.current && currentMenuId !== formMenuIdRef.current)
      return null;

    loadingRef.current = true;
    setIsLoading(true);
    setIsReady(false);
    setError(null);

    try {
      const results = await Promise.allSettled([
        fetchAssetCategories(currentConnector),
        fetchAssetLocations(currentConnector),
        fetchCommonUsers(currentConnector),
      ]);

      const optionTargets = [
        { label: '자산 종류', setter: setCategoryList },
        { label: '자산 위치', setter: setLocationList },
        { label: '사용자', setter: setUserList },
      ];
      const failedOptions = [];

      results.forEach((result, index) => {
        if (
          result.status === 'fulfilled' &&
          result.value?.result === 'SUCCESS'
        ) {
          optionTargets[index].setter(
            Array.isArray(result.value.data) ? result.value.data : [],
          );
          return;
        }
        failedOptions.push(optionTargets[index].label);
      });

      if (failedOptions.length > 0) {
        const message = `${failedOptions.join(', ')} 옵션을 불러오지 못했습니다.`;
        setError(message);
        winiMsg.showSnackbar(message);
        return null;
      }

      setIsReady(true);
      return results;
    } catch {
      const message = '종류·위치·사용자 옵션 조회 중 오류가 발생했습니다.';
      setError(message);
      winiMsg.showSnackbar(message);
      return null;
    } finally {
      loadingRef.current = false;
      setIsLoading(false);
    }
  }, []);

  useInitialFetch(loadOptions, Boolean(connector));

  return {
    categoryList,
    locationList,
    userList,
    isLoading,
    isReady,
    error,
    loadOptions,
  };
};
