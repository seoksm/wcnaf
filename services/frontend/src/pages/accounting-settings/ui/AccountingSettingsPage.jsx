import { WiniBox, WiniTypography, WiniGridItem, WiniGridLayout } from '@/shared/ui/wini';
import { WiniFormNormal } from '@/shared/ui/blocks/form-layout';

/**
 * S-233 회계기준 설정 - 읽기전용 정책 요약.
 * Q-22(회계기준 병행 범위)의 권장안(K-GAAP 단일)을 그대로 채택했고, 관련된 나머지 회계 정책
 * (Q-21·23~28)도 전부 코드 레벨 고정값이나 자산 종류별 컬럼(AssetCategory)으로 이미 구현되어
 * 있어, 워크스페이스 단위로 편집 가능한 설정은 존재하지 않는다. 이 화면은 그 확정된 정책을
 * 한곳에서 확인할 수 있도록 보여주기만 한다 - 값을 바꾸려면 코드/카테고리 설정을 바꿔야 한다.
 */
const POLICIES = [
  {
    title: '계산 방식',
    ref: 'Q-21',
    description:
      '배치 스케줄러 없이, 자산 이벤트(등록·취득가액 변경·불용·처분)가 발생할 때와 결산 자료를 ' +
      '조회하는 시점에 계산합니다. 산출 기간은 회계연도 누적 분기(1~3월·1~6월·1~9월·1~12월)이며, ' +
      '취득가액·감가상각누계액·장부가를 모두 보여주는 총액법으로 표시합니다.',
  },
  {
    title: '회계기준',
    ref: 'Q-22',
    description: 'K-GAAP 단일 기준을 적용합니다. IFRS 등 다른 기준과의 병행은 지원하지 않습니다.',
  },
  {
    title: '이력 저장 형식',
    ref: 'Q-23',
    description:
      '변경된 필드만 저장합니다. 다만 불용·처분·배정 3종 이력은 그 시점의 전체 상태 스냅샷을 함께 남깁니다.',
  },
  {
    title: '상각 기산 시점',
    ref: 'Q-24',
    description: '취득한 달을 포함해 상각을 시작합니다(월할 계산).',
  },
  {
    title: '비망가액',
    ref: 'Q-25',
    description:
      '완전히 상각된 자산도 장부가 0원이 되어 대장에서 존재감을 잃지 않도록, 자산 종류별로 설정한 ' +
      '비망가액(기본 1,000원)까지만 상각하고 그 아래로는 내려가지 않습니다.',
  },
  {
    title: '처분손익',
    ref: 'Q-26',
    description:
      '처분 금액에서 처분 시점 장부가를 뺀 값을 처분손익으로 계산해 보여줍니다. 처분 금액이 없으면 표시하지 않습니다.',
  },
  {
    title: '취득가액 정정 시 과거 처리',
    ref: 'Q-27',
    description:
      '이미 확정(저장)된 분기는 그대로 유지됩니다. 아직 확정되지 않은 분기만 정정된 취득가액을 ' +
      '반영해 다시 계산됩니다.',
  },
  {
    title: '불용 → 사용 복귀',
    ref: 'Q-28',
    description:
      '불용(처분대기) 상태의 자산만 사용 상태로 복귀할 수 있습니다. 처분완료된 자산은 되돌릴 수 없습니다.',
  },
];

export const AccountingSettingsPage = () => {
  return (
    <WiniFormNormal>
      <WiniGridLayout container>
        <WiniGridItem xs={12}>
          <WiniTypography variant="span" className="mb-3 block text-sm text-text-sub">
            현재 시스템에 적용된 감가상각·회계 처리 정책입니다. 회사 회계정책 사항이라 이 화면에서
            직접 바꿀 수 없으며, 변경이 필요하면 개발팀에 문의해주세요.
          </WiniTypography>
        </WiniGridItem>
      </WiniGridLayout>

      <WiniGridLayout container columnSpacing={2} rowSpacing={2}>
        {POLICIES.map((policy) => (
          <WiniGridItem key={policy.ref} size={{ md: 6, xs: 12 }}>
            <WiniBox ui="info" className="h-full p-4">
              <WiniBox className="mb-2 flex items-center justify-between">
                <WiniTypography variant="span" className="text-sm font-bold text-text-main">
                  {policy.title}
                </WiniTypography>
                <WiniTypography variant="span" className="text-xs text-gray-400">
                  {policy.ref}
                </WiniTypography>
              </WiniBox>
              <WiniTypography variant="span" className="block text-sm text-text-sub">
                {policy.description}
              </WiniTypography>
            </WiniBox>
          </WiniGridItem>
        ))}
      </WiniGridLayout>
    </WiniFormNormal>
  );
};

export default AccountingSettingsPage;
