import { useEffect, useRef } from 'react';

/**
 * ready(예: Boolean(connector))가 처음 true가 되는 순간 fetchFn을 딱 한 번만 호출한다.
 * 이후 ready/fetchFn이 다시 바뀌어도(예: 다른 메뉴로 전환되며 공유 connector가 갈아치워지는 경우)
 * 이 훅을 호출한 컴포넌트 인스턴스에서는 다시 호출하지 않는다 - ref 가드로 "최초 1회"를
 * 물리적으로 고정하지, 의존성 배열의 값 변화 여부에 맡기지 않는다.
 *
 * 메뉴 목록/드롭다운처럼 "마운트 시 한 번만 로드"가 필요한 곳에서 쓴다.
 * 메뉴를 전환하면 앱 전체가 새 메뉴로 넘어가기 전까지 이전 페이지가 잠깐 더 마운트돼
 * 있는 상태에서 공유 connector가 새 메뉴 것으로 갈아치워지는데, 이때 이전 페이지가 그걸
 * 받아서 자기 데이터를 다시 요청해버리면 잘못된 메뉴 권한으로 호출돼 "권한없음"이 뜨거나
 * 이전 메뉴의 목록이 다시 불려온다. `useEffect(fn, [])`만 쓰면 connector가 늦게 준비되는
 * 경우를 놓칠 수 있고, `useEffect(fn, [fetchFn])`처럼 fetchFn 자체를 의존성에 넣으면 위
 * cross-menu 오염 문제가 재발한다 - 이 훅은 두 문제를 동시에 피한다.
 *
 * @param {function} fetchFn - 준비되면 호출할 함수 (매 렌더 새로 생성돼도 무방)
 * @param {boolean} ready - 실행 가능 조건 (예: Boolean(connector))
 */
export function useInitialFetch(fetchFn, ready) {
  const hasFetchedRef = useRef(false);

  useEffect(() => {
    if (hasFetchedRef.current || !ready) return;
    hasFetchedRef.current = true;
    fetchFn();
  }, [ready, fetchFn]);
}
