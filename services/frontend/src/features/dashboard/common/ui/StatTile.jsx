import { WiniTypography } from '@/shared/ui/wini';

/**
 * KPI 스탯 타일 - ProgressSummary(S-303)의 규약을 그대로 확장한다. 숫자를 크고 굵게,
 * 라벨은 작고 흐리게 둬서 "얼마나 있는가"가 즉시 읽히게 한다(V6: 잉크색은 라벨에만).
 *
 * 루트를 일반 div로 두는 이유 - WiniBox는 "같은 부모 안에서 자신이 첫 번째가 아니면
 * margin-top을 자동으로 붙이는"(세로 스택 전용) 규칙이 기본 내장돼 있어(WiniBox.jsx의
 * `&:not(:first-of-type)`), 이 타일처럼 여러 개를 가로로 나열하면 첫 번째만 그 여백이
 * 없어 나머지가 짧아 보이는 버그가 생긴다. WiniGridItem은 이 규칙이 없어 안전하지만,
 * 이 타일은 반응형 컬럼 분할이 필요 없는 단순 래퍼라 굳이 Grid를 쓰지 않고 순수 div로
 * 피해간다.
 */
export const StatTile = ({ label, value, color = 'text-text-main', size = 'text-2xl' }) => (
  <div className="flex-1 rounded border border-solid border-gray-200 bg-white px-3 py-3 text-center">
    <WiniTypography variant="span" className={`block ${size} font-bold ${color}`}>{value}</WiniTypography>
    <WiniTypography variant="span" className="mt-1 block text-xs text-text-sub">{label}</WiniTypography>
  </div>
);
