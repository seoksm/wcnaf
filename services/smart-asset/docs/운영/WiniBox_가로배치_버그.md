# `WiniBox` 가로 배치 시 첫 번째와 나머지 크기가 달라지는 문제 - 전체 코드베이스 점검

## 배경

대시보드(S-700) 사용자 확인 중 "만료 예정" 위젯의 3개 버킷 타일 중 첫 번째만 나머지와
크기가 다르게 보인다는 보고를 받고 원인을 추적한 결과, 프런트엔드 공용 컴포넌트
`WiniBox`(`src/shared/ui/wini/Box/WiniBox.jsx`)에 내장된 "auto-gap" 규칙이 원인임을
확인했다.

```js
const baseSx = {
  fontSize: size.text.sm,
  ...(!disableAutoGap
    ? {
        '&:not(:first-of-type)': {
          marginTop: size.margin.xl,
          '&.winibox--btnitem': { marginTop: 0 },
        },
      }
    : {}),
  ...(uiSx ?? {}),
};
```

`WiniBox`는 "같은 부모 안에서 자신이 첫 번째 `WiniBox`가 아니면 `margin-top`을 자동으로
붙인다" - 폼 필드처럼 여러 개를 **세로로** 쌓을 때 자동으로 간격을 만들어주는 의도다.
그런데 여러 `WiniBox`를 **가로**(`flex`/`grid-cols-N`)로 나열하면 첫 번째만 이 여백이
없고 나머지는 있어, flex/grid의 기본 `align-items: stretch` 아래에서 나머지 항목이
짧게(또는 `items-center` 등에서는 아래로 밀려) 보인다.

`WiniGridItem`(`src/shared/ui/wini/GridItem/WiniGridItem.jsx`)은 이 규칙이 없어
안전하다 - 이전에 라이선스/렌탈 상세 다이얼로그를 `WiniGridLayout`+`WiniGridItem`으로
고친 것도 결과적으로 이 문제를 피해간 것이었다(그때는 원인을 "FormControl width:100%"로
짚었으나, 근본적으로는 이 auto-gap도 함께 작용했을 수 있다).

**이미 있는 회피 수단 3가지** (모두 확인·재현 테스트로 검증됨):
| 방법 | 조건 | 비고 |
|---|---|---|
| `className`에 `mt-0`(또는 `m-0`, 임의의 `mt-N`) 추가 | 이 앱은 `StyledEngineProvider enableCssLayer` + `@layer theme, base, mui, components, utilities` 순서로, Tailwind `utilities` 레이어가 MUI `mui` 레이어보다 우선한다 | 이미 여러 화면에서 이 방식으로 회피돼 있었음(`UserProfileForm.jsx`, `WiniChat.jsx`, `CanvasToolbar.jsx`) |
| `ui="noAutoGap"` | `WiniBox`가 직접 지원하는 토큰 | `wini-samples`의 `GUIDE_BOX_UI` 상수가 이 방식 |
| `ui="btnitem"` | 버튼 그룹 전용 - `WiniBox` 자체가 이 토큰에는 자동으로 `margin-top: 0`을 되돌려준다 | `features/menu/manage-actions/ui/Buttons.jsx` |
| 순수 `div` 사용 | 그 요소에 Wini `ui` 토큰 기능이 필요 없을 때 | 이번에 새로 고친 곳 대부분이 이 방식 |

## 점검 방법

AST 기반 스캐너(`@babel/parser`+`@babel/traverse`, 임시 스크립트, 점검 후 삭제)로
전체 `src/**/*.jsx`를 훑어 "가로 배치(`flex`, `grid-cols-N>1`) 컨테이너 밑에 `WiniBox`
자식이 2개 이상, 또는 `.map()`으로 여러 개 렌더링" 패턴을 찾았다. 1차는 인라인
`WiniBox` JSX만, 2차는 같은 파일 안에서 "루트가 `WiniBox`인 지역 컴포넌트"(예:
`SummaryItem`)까지 추적해 재귀적으로 탐지했다. 후보마다 `mt-0`/`ui="noAutoGap"`/
`ui="btnitem"` 같은 기존 회피 수단이 이미 있는지 직접 코드를 읽어 확인한 뒤, 실제로
버그가 있는 곳만 고쳤다.

## 수정한 곳 (실제 버그 확인 후 수정)

| 파일 | 내용 |
|---|---|
| `features/ticket/kanban/ui/KanbanBoard.jsx` | S-600 칸반 4개 상태 컬럼 - 첫 컬럼(접수대기)만 다른 높이로 보이던 문제 |
| `features/inventory/detail/ui/ProgressSummary.jsx` | S-303 진행현황 KPI 4칸 |
| `features/inventory/report/ui/ReportView.jsx` | S-305 리포트 KPI 4칸 |
| `features/depreciation/schedule/ui/ScheduleDialog.jsx` | S-231 스케줄 각 행의 텍스트/칩 그룹 세로 밀림 |
| `features/depreciation/status/ui/Summary.jsx` | S-230 요약 바 - `SummaryItem` 6개 나열 + 확정 칩 그룹 |
| `features/ui-builder/component-settings/ui/TreeNodeEditor.jsx` | UI 빌더 트리 노드 행의 버튼 그룹 세로 밀림 |
| `pages/wini-samples/component/CompWiniIcon.jsx` | 컴포넌트 샘플 페이지의 아이콘 데모 타일 2곳 |
| 대시보드(S-700/701) 관련 6개 파일 | `docs/운영/대시보드.md` "확인된 동작·발견한 문제" 참조 |

전부 `WiniBox` → 순수 `div`로 교체(해당 요소가 Wini `ui` 토큰 기능을 쓰지 않는 경우에만).
수정 후 Playwright로 각 그룹의 `getBoundingClientRect()`를 측정해 같은 그룹 안 모든
요소의 `top`/`height`가 완전히 일치함을 확인했다.

## 확인했지만 이미 문제없던 곳 (오탐, 수정 안 함)

| 파일 | 이유 |
|---|---|
| `entities/user/ui/UserProfileForm.jsx` | 두 버튼그룹(`ui="btnbox"`)에 이미 `mt-0`이 명시돼 있음 |
| `features/menu/manage-actions/ui/Buttons.jsx` | 두 그룹 모두 `ui="btnitem"` - 이 토큰은 애초에 예외 처리됨 |
| `features/ui-builder/canvas-editor/ui/CanvasToolbar.jsx` | 두 번째 그룹에 이미 `m-0`이 명시돼 있음 |
| `pages/wini-samples/component/CompGuideCommon.jsx` (2곳) | 전부 `ui={GUIDE_BOX_UI}`이고 `GUIDE_BOX_UI = 'noAutoGap'` |
| `shared/ui/blocks/chat/WiniChat.jsx` (3곳) | 헤더 우측 그룹에 `mt-0`, 메시지 말풍선에 `mt-2`가 이미 명시돼 있음(레이어 우선순위로 정상 작동 확인) |

## 남은 리스크

- 이번 점검은 **같은 파일 안에서** 반복되는 `WiniBox`(또는 그 파일 내부 지역 래퍼
  컴포넌트)만 정적 분석으로 잡았다. **다른 파일에서 import한 공용 컴포넌트가 호출하는
  쪽 화면에서 가로로 나열**되는 경우(크로스 파일)는 이번 스캐너로 감지되지 않는다.
  대시보드의 `StatTile`/`WidgetCard`는 원래 이 세션에서 새로 만든 컴포넌트라 직접
  확인했지만, 기존 코드베이스 전체의 크로스 파일 사례까지는 전면 점검하지 못했다.
- 근본 해결은 `WiniBox`의 auto-gap 규칙 자체를 "부모의 `display: flex`+`flex-direction:
  row` 여부"를 감지해 조건부로만 적용하도록 고치는 것이지만, 이는 공용 컴포넌트 변경이라
  전체 화면에 영향을 미쳐 이번 범위에서는 다루지 않았다.
