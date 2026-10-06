import { useEffect, useMemo, useState } from 'react';
import { Communicator } from '@/shared/api';
import { menuAdapter } from '@/shared/adapters';
import { convertProgramMappingToPath } from '@/shared/lib';
import { fetchMenuPermission, fetchMenuTree } from '../api/api';

const EMPTY_AUTH = {
  select: 'NONE',
  insert: 'NONE',
  update: 'NONE',
  delete: 'NONE',
  print: 'NONE',
  down: 'NONE',
  manage: 'NONE',
  valueView: 'NONE',
};

/**
 * custom1Status는 공용 메뉴권한 프레임워크가 제공하는 범용 예비 슬롯("기타1")이라
 * 메뉴마다 의미가 다를 수 있다 - 유형자산 메뉴에서는 이 슬롯을 "가치조회"(임직원
 * 추가권한, 05_화면설계_진행현황_인덱스.md Step1 S-040) 여부로 해석하는 것으로 정한다.
 * QR 스캔 상세(S-217)의 취득가액 노출이 이 값을 따른다 - 다른 화면에서 같은 슬롯을
 * 다른 뜻으로 재사용하면 안 된다.
 */
const toWiniAuth = (permission) => ({
  select: permission?.selectStatus || 'NONE',
  insert: permission?.insertStatus || 'NONE',
  update: permission?.updateStatus || 'NONE',
  delete: permission?.deleteStatus || 'NONE',
  print: permission?.printStatus || 'NONE',
  down: permission?.downStatus || 'NONE',
  manage: permission?.manageStatus || 'NONE',
  valueView: permission?.custom1Status || 'NONE',
});

/** 사이드바 밖의 단독 라우트가 특정 메뉴의 실제 ID와 권한을 사용할 수 있게 한다. */
export const useStandaloneMenuContext = (userId, menuPath) => {
  const connector = useMemo(() => new Communicator(), []);
  const [contextValue, setContextValue] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let active = true;

    const load = async () => {
      if (!userId || !menuPath) {
        setContextValue(null);
        setError('로그인 정보 또는 메뉴 경로를 확인할 수 없습니다.');
        setIsLoading(false);
        return;
      }

      setIsLoading(true);
      setError(null);
      try {
        const menuResult = menuAdapter.toMenuTreeResult(await fetchMenuTree(connector, userId), false);
        const menuEntry = Object.entries(menuResult.data?.all || {}).find(
          ([, programMapping]) => convertProgramMappingToPath(programMapping) === menuPath,
        );
        const menuId = menuEntry?.[0];

        if (!menuId) throw new Error('MENU_NOT_FOUND');

        const permission = menuAdapter.toMenuPermissionResult(
          await fetchMenuPermission(connector, userId, menuId),
          menuId,
        );
        const winiAut = toWiniAuth(permission);
        connector.client.defaults.headers.common['X-Menu-Id'] = menuId;

        if (!active) return;
        setContextValue({ id: menuId, winiEvent: {}, info: {}, connector, winiAut: winiAut || EMPTY_AUTH });
        if (winiAut.select !== 'ALLOW') setError('유형자산 정보를 조회할 권한이 없습니다.');
      } catch (requestError) {
        if (!active) return;
        setContextValue(null);
        setError(
          requestError?.message === 'MENU_NOT_FOUND'
            ? '유형자산 관리 메뉴를 찾을 수 없습니다.'
            : '메뉴 권한 정보를 불러오지 못했습니다.',
        );
      } finally {
        if (active) setIsLoading(false);
      }
    };

    load();
    return () => {
      active = false;
    };
  }, [connector, menuPath, userId]);

  return { contextValue, isLoading, error };
};
