import { WiniTypography } from '@/shared/ui/wini';

/**
 * S-700 위젯 카드 공용 셸 - 기존 화면들의 카드 규약(rounded border border-solid border-gray-200
 * bg-white p-3, 예: ProgressSummary/MyStatusList)을 그대로 따른다. 제목 옆 아이콘은 위젯의
 * 성격(요약/분포/추세/조치필요)을 한눈에 구분시켜 "주요내용이 잘 드러나도록" 돕는다.
 *
 * 루트가 WiniBox가 아니라 일반 div인 이유 - 이 카드는 대시보드에서 한 행에 여러 개가
 * 나란히 놓인다(예: 분포 3개, 조치필요 2개). WiniBox는 "부모 안에서 자기가 첫 번째가
 * 아니면 margin-top을 자동으로 붙이는" 규칙이 기본 내장돼 있어(세로 스택 전용 설계),
 * 카드를 가로로 나열하면 첫 번째 카드만 그 여백이 없어 나머지가 짧게/아래로 밀려
 * 보이는 버그가 생긴다(StatTile과 동일한 원인). 헤더 안쪽 요소들도 같은 이유로 전부
 * 일반 div/span으로 둔다.
 *
 * @container를 카드 루트에도 여는 이유 - 이 카드는 대시보드 페이지 레벨 컨테이너 안에서
 * 2~3열 그리드의 한 칸으로 들어간다. 카드 내부(예: TicketStatusWidget의 KPI 4칸)가
 * 컨테이너 쿼리(@xl: 등)를 쓸 때, 카드 자신이 컨테이너가 아니면 그 쿼리는 더 바깥의
 * 페이지 컨테이너 폭을 보게 돼 실제 카드 폭(그리드 한 칸 몫)보다 넓게 오판한다 - 카드
 * 마다 중첩 컨테이너를 열어야 내부 반응형이 "이 카드에게 실제로 주어진 폭"을 기준으로
 * 정확히 판단한다.
 */
export const WidgetCard = ({ icon: Icon, title, action, children, className = '', bodyClassName = '' }) => (
  <div className={`@container flex h-full flex-col rounded border border-solid border-gray-200 bg-white p-3 ${className}`}>
    <div className="mb-3 flex items-center justify-between gap-2">
      <div className="flex items-center gap-1.5">
        {Icon && <Icon sx={{ fontSize: 18 }} className="text-text-sub" />}
        <WiniTypography variant="span" className="text-sm font-semibold text-text-main">{title}</WiniTypography>
      </div>
      {action}
    </div>
    <div className={`flex-1 ${bodyClassName}`}>{children}</div>
  </div>
);
