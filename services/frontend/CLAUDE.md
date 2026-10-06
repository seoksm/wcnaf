---
description: FSD + coding + screen implementation rules for this project
alwaysApply: true
---

# 프로젝트 공통 AI 개발 규칙

이 문서는 AI가 이 프로젝트에서 코드를 작성/수정할 때 항상 따라야 하는 실행 규칙입니다.
아래 규칙은 `docs/FSD_아키텍처_가이드.md`, `docs/코딩_컨벤션_가이드.md`, `docs/화면_작성_가이드.md`를 통합한 기준입니다.

## 1) 기본 원칙

- 새 요구사항은 우선 `features/<domain>/<usecase>`에서 시작한다.
- `pages`는 조립/배치 중심으로 얇게 유지하고, 로직은 `model` 훅으로 분리한다.
- 같은/유사 코드가 2곳 이상에서 재사용되면 공통화한다.
  - 도메인 공통: `entities`
  - 도메인 비의존: `shared`
  - 반복 조합 UI 블록: `widgets`
- 슬라이스 외부 사용은 Public API(`index.js`) 경유를 기본으로 한다.
- src/pages/wini-samples를 참고하여 스타일과 속성을 참고합니다.
- src/pages/wini-samples의 데이터는 참고용이지 생성시 해당폴더에 생성 하지 않습니다.

## 2) FSD 구조/의존성 규칙

- 레이어: `app -> pages -> widgets -> features -> entities -> shared`
- 의존 방향은 상위 -> 하위만 허용한다. (하위에서 상위 import 금지)
- 같은 레이어 내 다른 슬라이스 직접 import 금지 (cross-slice 금지)
- 세그먼트 의존 규칙
  - `ui -> model, api` 허용
  - `model -> api` 허용
  - `api -> ui, model` 금지
- 화면의 큰 틀 조합(레이아웃/영역 배치)은 `pages`에서 수행한다.
- `features`는 leaf UI, model hook, api 중심으로 구성하되, feature 내부의 하단/부분 묶음 컴포넌트는 허용한다.
- 금지 패턴: 컴포넌트 이름과 무관하게, `pages`가 feature의 단일 진입 컴포넌트 1개만 호출하고 화면 전체(좌/우/상/하 주요 영역) 조립을 그 컴포넌트에 전부 위임하는 구조.

## 3) 디렉토리/네이밍 규칙

- 기본 구조: `src/<layer>/<slice>/<segment>/...`
- 공통 segment: `ui | model | api`
- 폴더명: `kebab-case`
- JS 파일명: `camelCase.js`
- React 컴포넌트 파일명: `PascalCase.jsx`
- 커스텀 훅 파일명: `useSomething.js`
- 슬라이스 루트 엔트리: `index.js`
- src/pages/wini-samples 폴더 구조는 샘플을 위한것이므로 폴더구조는 참고하지 않습니다.

## 4) Import/Export 규칙

- `@/`는 `src/`를 의미한다.
- 슬라이스 외부 import는 `@/` 절대경로 + 슬라이스 루트 `index.js` 경유를 사용한다.
- 슬라이스 내부 import는 상대경로를 사용한다.
- Deep import(`.../ui/*`, `.../model/*`, `.../api/*`)는 금지한다.
- `pages`를 제외한 레이어는 Named export만 사용한다. (`default export` 금지)
- `pages`는 라우팅 규칙상 default export를 사용한다.

## 5) 화면 개발 규칙 (중요)

- 신규 화면 개발 시:
  1. 화면을 영역(Area)으로 나눈다.
  2. 영역을 feature 단위로 분리한다.
  3. `WiniGridLayout`, `WiniGridItem`, `WiniBox` 등 공통 컴포넌트로 레이아웃 구성한다.
  4. `ui/model/api` 분리를 지킨다.
- 화면 시안에 포함된 `x/y/w/h`, 캔버스 크기(px)는 "배치 참고값"으로만 사용하고, 구현 기본은 반응형으로 한다.
- 기본 레이아웃은 `w-full`, `h-full` 또는 `min-h-*`, `flex`, `grid` 조합으로 구성하여 다양한 해상도에서 화면을 꽉 채우도록 구현한다.
- 화면 조정(필드 추가/재배치/간격/정렬)은 FE에서 공통 컴포넌트 + Tailwind 조합으로 우선 처리한다.
- 공통 컴포넌트 자체 수정(내부 마크업/상호작용/variant 변경)이 필요할 때만 컴포넌트 단위 수정 프로세스로 승격한다.

## 6) UI 컴포넌트/스타일 규칙

- 공통 UI(`shared/ui/wini`)를 최우선 사용한다.
- 동일 기능이 공통 컴포넌트에 있으면 새 컴포넌트를 임의 생성하지 않는다.
- 스타일링은 Tailwind 우선(`className` 기반). `sx`/`style`은 불가피한 경우만 사용한다.
- 반복되는 스타일/구조 패턴은 `shared/ui` 공통 컴포넌트로 승격한다.
- AG Grid는 단순 컬럼은 UI 근처, 재사용/복잡 설정은 분리 파일로 관리한다.
- AG Grid 높이 규칙:
  - `WiniAgGridReact`를 감싸는 부모 컨테이너에 반드시 명시 높이(`h-[px]`, `min-h-[px]` 등) 또는 계산 가능한 고정 레이아웃 높이를 먼저 부여한다.
  - AG Grid 래퍼에서 `h-full`/`height: 100%`만 단독 사용해 높이를 위임하는 방식은 지양한다. (부모 높이가 미확정이면 그리드 미노출 위험)
  - 권장 패턴: 페이지/영역 컨테이너 높이 확정 -> 그리드 래퍼 `flex-1 min-h-0` -> `WiniAgGridReact`
- src/pages/wini-samples를 참고하여 스타일과 속성을 참고합니다.
- src/pages/wini-samples의 데이터는 참고용이지 생성시 해당폴더에 생성 하지 않습니다.
- WiniFormNormal 또는 WiniFormEmpty 컴포넌트는 화면 가장 아래에 위치하며, 화면 전체를 감싸는 컴포넌트입니다. (기본: WiniFormNormal 컴포넌트를 사용합니다.)


### 6-1) Wini 컴포넌트 Prop 체크 가이드 (필수 반영)

화면 개발 시 아래 컴포넌트를 사용하면, AI는 필수 prop 누락 여부를 먼저 확인한다.

#### A. 데이터/복합 컴포넌트

| 컴포넌트 | 필수(Required) | 주요 선택(Optional) |
|---|---|---|
| `WiniAgGrid` | `rowData`, `columnDefs` | `pagination`, `rowSelection`, `size`, `headerHeight` |
| `WiniTreeView` | `winiData`, `render`, `field`, `toggleCheck` | `onChange`, `height`, `disableDrag`, `leaf` |
| `WiniList` | 구성 요소 조합(`WiniListItem`, `WiniListItemText`, `WiniListItemIcon`) | `listType`, `openDepth1/2`, `ui`, `changeView` |
| `WiniTab` | 구성 요소 조합(`WiniTabs`, `WiniTab`, `WiniTabPanel`) + `value` 연동 | `ui`, `variant`, `scrollButtons` |

#### B. 폼/입력 컴포넌트

| 컴포넌트 | 필수(Required) | 주요 선택(Optional) |
|---|---|---|
| `WiniText` | `label` | `value`, `onChange`, `multiline`, `required`, `titleFix`, `slotProps` |
| `WiniNumber` | `label` | `value`, `onChange`, `required`, `titleFix`, `slotProps` |
| `WiniSelect` | `label`, `children` | `value`, `onChange`, `defaultValue`, `MenuProps` |
| `WiniCheckbox` | `label`, `iconName` | `checked`, `onChange`, `checkedIconName`, `ui` |
| `WiniRadio` | `WiniRadioGroup` 조합 | `name`, `row`, `value`, `onChange`, `titleFix` |
| `WiniInputLabel` | `children` | `htmlFor`, `ui`, `sx`, `className` |
| `WiniDateTimePicker`  | `label`,`format` | `value`, `onChange`,`ui`,`labelClassName`,`inputClassName`, `slotProps` |


#### C. 버튼/상호작용 컴포넌트

| 컴포넌트 | 필수(Required) | 주요 선택(Optional) |
|---|---|---|
| `WiniButton` | `children` | `onClick`, `loading`, `disabled`, `ui`, `sx` |
| `WiniIconButton` | `icon` | `children`, `iconOnly`, `iconSx`, `aria-label` |
| `WiniToggleButton` | `children` | `selected`, `defaultSelected`, `selectedColor`, `ui` |
| `WiniButtonGroup` | `children` | `itemMinWidth`, `ui`, `sx`, `className` |

#### D. 레이아웃/유틸 컴포넌트

| 컴포넌트 | 필수(Required) | 주요 선택(Optional) |
|---|---|---|
| `WiniGridLayout` | 없음 | `container`, `columnSpacing`, `rowSpacing`, `rowItem` |
| `WiniGridItem` | 없음 | `ratio`, `size`, `xs/sm/md/lg/xl`, `scrollHidden` |
| `WiniBox` | 없음 | `ui`, `className`, `sx` |
| `WiniIcon` | `icon` | `fontSize`, `viewBox`, `aria-label`, `sx` |

#### E. 구현 메모

- `AgGrid`, `Tabs`, `TreeView`는 prop 나열보다 상태/핸들러 연결 설계를 우선한다.
- 공통 테마 적용은 `ui` prop을 우선 검토하고, 세부 조정은 `className` 중심으로 처리한다.
- 레이아웃은 `WiniBox` + `WiniGridLayout` 조합을 기본으로 사용한다.
- `WiniText`에 type은 컴포넌트가 존재하지않을때 만사용한다 (이미존재하는 컴포넌트`WiniDateTimePicker`, `WiniNumber` 사용)
- `WiniButton`의 라벨길이는 가급적으로 한줄로 표현한다.

### 6-2) `WiniBox` 레이아웃 사용 요약

- `WiniBox`는 화면에서 영역(컨테이너) 역할로 사용하고, 내부에 `WiniGridLayout`, 폼, 버튼 영역을 배치한다.
- 먼저 `ui` 프리셋을 선택하고(`search`, `form`, `info`, `line`, `btnbox`, `btnitem`, `inputButton`), 필요한 경우 `className`으로 미세 조정한다.
- 검색 영역은 `WiniBox ui="search"` + 내부 `WiniGridLayout container ui="form"` 조합을 기본 패턴으로 사용한다.
- 입력 영역은 `WiniBox ui="form"`으로 감싸고, 필드는 `WiniGridItem` 단위로 배치한다.
- 버튼 영역은 `WiniBox ui="btnbox"` 안에 좌/우 정렬용 `WiniBox ui="btnitem"`을 배치하는 패턴을 우선 적용한다.
- 안내/강조 박스는 `WiniBox ui="info"` 또는 `WiniBox ui="line"`을 사용한다.
- 레이아웃 보정은 Tailwind(`flex`, `gap-*`, `h-full`, `w-full`)를 우선 적용하고, `sx`/`style`은 불가피한 경우만 사용한다.
- `WiniAgGridReact`를 포함하는 경우, `WiniBox` 부모 컨테이너에 명시 높이(`h-[px]`, `min-h-[px]`) 또는 계산 가능한 고정 높이를 먼저 부여한다.

```jsx
<WiniBox ui="search">
  <WiniGridLayout container ui="form" columnSpacing={1} rowSpacing={1}>
    {/* 검색 필드 */}
  </WiniGridLayout>
  <WiniBox ui="btnbox">
    <WiniBox ui="btnitem"></WiniBox>
    <WiniBox ui="btnitem">
      <WiniButton ui="default">조회</WiniButton>
    </WiniBox>
  </WiniBox>
</WiniBox>
```

## 7) React/Hook 작성 규칙

- React 함수형 컴포넌트만 사용한다.
- 컴포넌트는 UI 렌더링 중심으로 유지한다.
- 비즈니스 로직/비동기/데이터 가공은 훅(`model`)으로 분리한다.
- 훅 네이밍은 `useSomething` 형식을 따른다.
- 훅은 단일 책임을 지키고, UI에서 필요한 값/핸들러를 명확한 이름으로 반환한다.
- 훅 내부에서 DOM 직접 조작은 금지한다.
- 화면 전환 안정성을 위해 `useCallback`/`useMemo`/`useEffect` 의존성 배열에 `connector` 객체를 직접 넣는 패턴은 지양한다. (라우트 전환 시 참조 변경으로 불필요 재실행/에러 유발 가능) 필요한 값만 primitive로 분해해 의존성에 명시하거나, 안정화된 참조(`ref`/상위 memoized 값)로 관리한다.

## 8) 상태/비동기/에러 처리 규칙

- 기본 상태관리는 `useState`를 우선한다.
- 전역 공유가 필요한 상태만 Zustand를 사용한다.
- 비동기 요청은 `isLoading`, `error`, `empty` 패턴을 일관되게 유지한다.
- `try/catch/finally`로 로딩 상태를 닫는다.
- 사용자 액션(저장/삭제/제출)은 성공/실패 피드백을 반드시 제공한다.

## 9) 메시지/권한/유틸 규칙

- `window.alert`, `window.confirm` 사용 금지.
- 메시지 표시는 `winiMsg` 사용:
  - 차단성/확인 필요: `showAlert`, `showConfirm`
  - 안내성/비차단: `showSnackbar`
- 권한 값은 `"ALLOW"` / `"NONE"` 기준.
- 화면 권한은 `winiCom.getFormInfo()` 또는 `winiCom.getFormAut()`로 조회한다.
- 버튼 노출/활성/동작은 권한 기반으로 제어한다. 필요 시 `winiCom.checkMenuAut()` 사용한다.
- `winiCom.get/reset/isValidCheck`는 항상 `ref.current`를 넘긴다.
- `winiCom.isValidCheck`는 동기 함수로 사용한다.

## 10) HTTP/날짜 규칙

- 신규 코드에서 직접 `fetch`/`axios` 호출 금지.
- 프로젝트 표준 communicator/http 클라이언트를 사용한다.
- API 호출 로직은 기본적으로 각 feature의 `api/api.js`(또는 `api` 세그먼트)에 작성한다.
- API 함수는 `features/*/api`에 두고, 재사용 2회 이상이면 `entities/*/api`로 승격한다.
- `api.js`에서 URL 작성 시 템플릿 리터럴 문자열 보간(`\`${'{'}id{'}'}\``)을 기본으로 사용한다.
- URL 구문 내부에서 함수 호출을 넣는 패턴은 지양한다. (예: `` `/api/v1/system/user/${'{'}user.getId(){'}'}` `` 금지)
- 날짜/시간 처리는 `winiDate` 우선 사용한다. (`new Date()` 남발 금지)
- UTC 데이터는 표시/검색/비교 전에 변환을 고려한다.
- 날짜 포맷은 `winiDate.dateFormat(...)`을 사용한다.

## 11) 주석/로그 규칙

- 주석은 "왜(why)"가 필요할 때만 작성한다.
- 코드 설명 반복/임시/디버깅 주석 금지.
- 업무 코드(`features`, `pages` 등)에 `console.log` 금지. (샘플/dev 페이지 예외)

## 12) AI 자동 실행 체크리스트

AI는 화면/기능 개발 요청을 받으면 아래를 자동 적용한다.

1. 먼저 FSD 기준으로 `features` 또는 `pages`의 적절한 위치를 판단한다.
2. `ui/model/api` 분리를 먼저 설계하고 파일을 생성/수정한다.
3. 공통 UI(`shared/ui/wini`) 재사용 가능성을 우선 검토한다.
4. 외부 import는 Public API(`index.js`) 경유로 맞춘다.
5. 권한/피드백/에러/로딩 상태를 누락 없이 반영한다.
6. Tailwind 우선으로 스타일을 정리한다.
7. deep import, cross-slice import, 레이어 역참조가 없는지 최종 점검한다.

## 13) 금지 목록 요약

- `pages` 외 레이어에서 `default export`
- 슬라이스 외부 deep import
- 같은 레이어 다른 슬라이스 직접 import
- 하위 -> 상위 레이어 import
- `api`에서 `ui/model` import
- `window.alert/confirm` 사용
- 신규 코드에서 직접 `fetch/axios`
- `api.js` URL 템플릿 보간 위치에서 함수 호출 사용
- 업무 코드에 임시 `console.log`

## 14) 빠른 예제 스니펫

아래 예제는 화면 개발 시 AI가 기본 템플릿처럼 참고한다.

### 예제 A) 페이지는 조립만 담당

```jsx
import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';
import { UserListSearch, UserListGrid, useUserList } from '@/features/user/list';
import { UserEditor, useUserEditor } from '@/features/user/editor';

export const UserManagementPage = () => {
  const { searchParam, userList, handleSearchChange, handleSearch, handleRowClick } = useUserList();
  const { selectedUser, handleChange, handleReset, handleCreate, handleUpdate, handleDelete } = useUserEditor();

  return (
    <WiniFormNormal>
      <UserListSearch searchParam={searchParam} onSearchChange={handleSearchChange} onSearch={handleSearch} />
      <UserListGrid userList={userList} onCellClick={handleRowClick} />
      <UserEditor
        selectedUser={selectedUser}
        onChange={handleChange}
        onReset={handleReset}
        onCreate={handleCreate}
        onUpdate={handleUpdate}
        onDelete={handleDelete}
      />
    </WiniFormNormal>
  );
};
```

### 예제 B) Feature Public API (Named export)

```js
// src/features/user/create/index.js
export { Form as UserCreateForm } from './ui/Form.jsx';
export { useSubmit as useUserCreate } from './model/useSubmit.js';
```

### 예제 C) 권한 기반 버튼 노출

```jsx
import { winiCom } from '@/shared/lib';
import { WiniButton } from '@/shared/ui/wini';

export const ActionButtons = ({ onSave }) => {
  return (
    <>
      {winiCom.checkMenuAut(
        'update',
        <WiniButton ui="line" className="w-20" onClick={onSave}>
          수정
        </WiniButton>,
      )}
    </>
  );
};
```

### 예제 D) 에러 처리 + 사용자 피드백

```js
import { winiCom } from '@/shared/lib';
import { winiMsg } from '@/shared/model';

const handleSelect = async (connector) => {
  try {
    // const data = await fetchUsers(connector);
  } catch (error) {
    winiMsg.showSnackbar(winiCom.getErrorMessage(error.response?.data?.message));
  }
};
```


가능하면 프로젝트에 이미 존재하는 코드 패턴을 우선적으로 참고합니다.
