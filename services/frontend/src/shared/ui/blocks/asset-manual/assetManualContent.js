/**
 * 자산관리 파트 온라인 사용자 매뉴얼 콘텐츠.
 * PageHeader의 "도움말" 버튼이 여는 AssetManualDialog가 그대로 렌더링한다.
 * 화면이 추가/변경되면 이 배열과 ASSET_MANUAL_PROGRAM_MAP만 갱신하면 된다.
 */

export const ASSET_MANUAL_PARTS = {
  EMPLOYEE: '임직원 공통',
  ADMIN: '자산관리자 메뉴',
  APPENDIX: '부록',
};

export const ASSET_MANUAL_SECTIONS = [
  // ---------------- PART 1. 임직원 공통 ----------------
  {
    id: 'start',
    code: 'A-00',
    part: ASSET_MANUAL_PARTS.EMPLOYEE,
    title: '시작하기 전에',
    purpose: '전체 메뉴에서 공통으로 쓰이는 개념 세 가지만 먼저 알아두면 나머지는 훨씬 쉽습니다.',
    blocks: [
      {
        heading: '자산코드',
        body: '모든 유형자산에는 AST-2026-0001 형식의 고유 코드가 자동으로 부여됩니다. QR 라벨에 바로 이 코드가 담겨 있어서, 라벨만 스캔하면 자산코드를 직접 입력할 필요가 없습니다.',
      },
      {
        heading: '자산 상태는 두 가지 축으로 봅니다',
        body: '자산 하나의 상태는 사용상태(지금 쓸 수 있는 물건인가)와 배정상태(지금 누구 것인가)로 따로 표시됩니다. 예를 들어 "보관 + 공용"이면 창고에 있는 공용 자산이라는 뜻입니다. 전체 값 목록은 부록(상태값 한눈에 보기)을 참고하세요.',
      },
      {
        heading: '"나의 ○○" 메뉴',
        body: '메뉴 이름이 "내 ○○"으로 시작하면 전체가 아니라 본인에게 배정·등록된 건만 보이는 개인용 화면입니다. 같은 업무라도 관리자가 보는 전체 관리 화면과는 범위가 다릅니다.',
      },
    ],
    note: { type: 'info', text: '보이는 메뉴는 소속·직책별 권한에 따라 다를 수 있습니다. 필요한 메뉴가 보이지 않으면 자산관리자에게 권한 부여를 요청하세요.' },
  },
  {
    id: 'my-inventory',
    code: 'A-01',
    part: ASSET_MANUAL_PARTS.EMPLOYEE,
    title: '내 전수조사',
    purpose: '정기 전수조사(실사) 기간에 본인에게 배정된 자산을 실제로 보유하고 있는지 QR 스캔으로 확인 처리합니다.',
    blocks: [
      {
        heading: '사용 방법',
        steps: [
          '"내 전수조사" 메뉴에서 진행 중인 조사를 열면 확인 대상 자산 목록이 보입니다.',
          '"스캔 시작"을 누르고 카메라 권한을 허용한 뒤, 자산에 붙은 QR 라벨을 비춥니다.',
          '인식에 성공하면 목록에서 자동으로 체크되고, 화면을 끄지 않은 채 다음 자산을 이어서 스캔할 수 있습니다.',
          '라벨이 없거나 훼손돼 스캔이 안 되면 "사진으로 대체 확인"을 선택해 자산 사진을 촬영합니다.',
          '목록에 없는 자산을 보유 중이라면 "내 자산 아님" 이상보고로 전달합니다.',
          '대상 전체를 확인했으면 "완료"를 눌러 제출합니다. 이후 담당자 승인을 기다립니다.',
        ],
      },
    ],
    note: { type: 'warn', text: '조사 마감일을 넘기면 남은 자산은 관리자가 "미확인"으로 종결 처리할 수 있습니다. 가능한 한 마감 전에 확인을 끝내세요.' },
  },
  {
    id: 'my-loan',
    code: 'A-02',
    part: ASSET_MANUAL_PARTS.EMPLOYEE,
    title: '대여 가능 자산 · 내 대여 자산',
    purpose: '공용으로 빌려 쓸 수 있는 자산을 신청하고, 지금 빌린 자산을 반납·연장합니다.',
    blocks: [
      {
        heading: '자산 빌리기 — 대여 가능 자산',
        steps: [
          '"대여 가능 자산" 메뉴에서 지금 빌릴 수 있는 목록을 확인합니다(종류·위치로 좁혀보기 가능).',
          '원하는 자산의 QR 라벨을 스캔하면 바로 대여 신청 화면으로 이동합니다.',
          '승인 절차가 켜져 있는 부서라면 담당자 승인 후, 꺼져 있다면 신청 즉시 대여가 확정됩니다.',
        ],
      },
      {
        heading: '반납·연장 — 내 대여 자산',
        steps: [
          '"내 대여 자산"에서 현재 대여 중인 목록과 반납 예정일을 확인합니다.',
          '반납할 때는 해당 자산의 QR을 스캔하고, 상태(정상/이상)를 체크한 뒤 반납을 완료합니다.',
          '더 쓰고 싶으면 반납 예정일 전에 "연장 신청"을 누릅니다.',
        ],
      },
    ],
    note: { type: 'info', text: '반납이 늦어지면 알림이 발송되고, 관리자의 대여 현황 화면에 연체로 표시됩니다.' },
  },
  {
    id: 'my-license',
    code: 'A-03',
    part: ASSET_MANUAL_PARTS.EMPLOYEE,
    title: '내 라이선스',
    purpose: '본인에게 배정된 소프트웨어 라이선스를 확인합니다.',
    blocks: [
      {
        heading: '사용 방법',
        bullets: [
          '목록에서 라이선스명·버전·배정일을 확인할 수 있습니다.',
          '더 이상 쓰지 않는 라이선스는 "회수 요청" 표시를 눌러 관리자에게 반납 의사를 전달합니다.',
        ],
      },
    ],
  },
  {
    id: 'my-ack',
    code: 'A-04',
    part: ASSET_MANUAL_PARTS.EMPLOYEE,
    title: '확인서 승인',
    purpose: '자산을 수령했을 때, 관리자가 보낸 수령확인서 내용에 본인이 전자적으로 동의(승인)합니다.',
    blocks: [
      {
        heading: '사용 방법',
        steps: [
          '"내 확인서"에서 승인 대기 중인 확인서를 엽니다.',
          '안내 문구와 수령 자산 내역을 끝까지 확인합니다.',
          '"내용을 확인했습니다"에 체크한 뒤 "승인"을 누릅니다.',
        ],
      },
    ],
    note: { type: 'warn', text: '승인하면 승인 일시와 접속 정보가 함께 기록되어 그 자체로 증빙이 되며, 이후에는 취소할 수 없습니다. 실제 수령한 자산과 내역이 맞는지 먼저 확인하세요.' },
  },
  {
    id: 'my-ticket',
    code: 'A-05',
    part: ASSET_MANUAL_PARTS.EMPLOYEE,
    title: '내 티켓',
    purpose: '자산 신규 신청, 고장 신고 등 자산 관련 요청을 담당자에게 보내고 진행 상황을 확인합니다.',
    blocks: [
      {
        heading: '사용 방법',
        steps: [
          '"내 티켓"에서 "새 티켓"을 누릅니다.',
          '관련된 자산이 있으면 자산코드로 검색해 연결합니다(선택 사항).',
          '제목과 내용을 작성하고 등록합니다.',
          '담당자가 남긴 코멘트를 확인하고, 필요하면 추가로 답변을 남깁니다.',
          '상태(대기 → 진행중 → 완료)로 처리 흐름을 확인합니다.',
        ],
      },
    ],
  },
  {
    id: 'qr-common',
    code: 'A-06',
    part: ASSET_MANUAL_PARTS.EMPLOYEE,
    title: 'QR 스캔 공통 사용법',
    purpose: '전수조사, 대여·반납에서 똑같은 방식으로 동작하는 QR 스캔 요령입니다.',
    blocks: [
      {
        steps: [
          '스캔 화면에 들어가면 카메라 권한을 허용합니다(최초 1회).',
          '자산에 붙은 QR 라벨을 화면 중앙 네모 영역에 맞춥니다.',
          '인식되면 자동으로 다음 단계로 넘어가고, 연속 스캔 화면에서는 "완료"를 누르기 전까지 계속 이어서 스캔할 수 있습니다.',
        ],
      },
    ],
    note: { type: 'info', text: '인식이 잘 안 되면 조명을 밝게 하거나 각도를 조금 기울여 보세요. 일부 구형 브라우저에서는 사진 분석 방식으로 동작해 인식이 한 박자 느릴 수 있습니다.' },
  },

  // ---------------- PART 2. 자산관리자 메뉴 ----------------
  {
    id: 'tangible',
    code: 'B-01',
    part: ASSET_MANUAL_PARTS.ADMIN,
    title: '유형자산(비품) 관리',
    purpose: 'PC, 모니터 같은 실물 비품의 등록부터 배정, 폐기 직전까지 전체 생애주기를 다루는 가장 기본이 되는 메뉴입니다.',
    blocks: [
      { heading: '목록 조회 · 검색', body: '종류·위치·사용상태·배정상태·사용자로 필터링해 원하는 자산을 찾고, 필요하면 엑셀로 내보낼 수 있습니다.' },
      { heading: '등록 · 수정', body: '자산명·종류·위치·취득일·취득가·모델명·제조사·시리얼No 등을 입력해 한 건씩 등록·수정합니다. 저장하면 자산코드가 자동으로 채번됩니다.' },
      {
        heading: '엑셀 일괄 등록',
        steps: [
          '엑셀 양식을 다운로드합니다.',
          '양식에 자산 정보를 입력합니다.',
          '업로드하면 "미리보기" 화면에서 행마다 입력값 검증 결과를 보여줍니다.',
          '오류가 있는 행을 수정합니다.',
          '문제가 없으면 "확정"을 눌러 한 번에 반영합니다.',
        ],
        note: { type: 'info', text: '확정 전 미리보기 단계에서 오류 행이 남아 있지 않은지 꼭 확인하세요.' },
      },
      { heading: '자산 복제', body: '동일한 사양의 자산을 여러 대 등록할 때, 기존 자산 한 건을 선택해 원하는 수량만큼 복제합니다. 자산코드는 복제본마다 새로 채번됩니다.' },
      { heading: '선택 자산 일괄 변경', body: '여러 자산을 한 번에 선택해 위치·상태 같은 공통 항목을 일괄로 바꿉니다.' },
      { heading: 'QR 라벨 발행', body: '자산을 선택해 QR 라벨을 인쇄합니다. 라벨 그리드만 깔끔하게 출력되므로 라벨지에 바로 인쇄해 자산에 부착하면 됩니다.' },
      { heading: '배정 이력 · 회수', body: '자산별로 과거에 누가 언제부터 쓰고 있었는지 배정 이력을 확인하고, 현재 배정을 회수하면 자산은 "미배정" 상태가 됩니다.' },
      { heading: '변경 이력 · 전체 활동 로그', body: '자산 항목이 언제 어떻게 바뀌었는지, 그리고 전체 변경 활동을 로그로 조회할 수 있습니다.' },
      { heading: '불용 처리', body: '더는 정상적으로 쓸 수 없는 자산(고장·노후 등)을 사유와 함께 "불용" 상태로 전환합니다. 불용 처리된 자산은 불용자산 관리로 넘어갑니다.' },
    ],
  },
  {
    id: 'disposal',
    code: 'B-02',
    part: ASSET_MANUAL_PARTS.ADMIN,
    title: '불용자산 관리',
    purpose: '불용 전환된 자산을 다시 쓸 수 있게 되돌리거나, 최종적으로 처분 처리합니다.',
    blocks: [
      {
        heading: '사용 방법',
        bullets: [
          '수리가 끝나 다시 쓸 수 있게 됐다면 "사용 상태로 복귀"를 선택합니다.',
          '더 이상 쓸 수 없다면 "처분 처리"를 선택해 사유·처분금액·거래처를 입력합니다.',
        ],
      },
    ],
    note: { type: 'warn', text: '처분 처리는 완료되면 되돌릴 수 없습니다. 확정 전에 금액과 거래처 정보를 다시 한번 확인하세요.' },
  },
  {
    id: 'depreciation',
    code: 'B-03',
    part: ASSET_MANUAL_PARTS.ADMIN,
    title: '감가상각 · 회계기준',
    purpose: '자산의 장부가를 회계 기준에 맞춰 계산하고, 기간별로 결산을 확정합니다.',
    blocks: [
      { heading: '감가상각 현황', body: '자산별로 현재 장부가와 상각누계액을 한눈에 조회합니다.' },
      { heading: '상각 스케줄', body: '자산 하나를 선택하면 앞으로의 연도별 상각 계획을 확인할 수 있습니다.' },
      { heading: '결산 확정 · 해제', body: '특정 기간의 상각을 확정하면 그 기간 금액이 잠기고 확정 이력이 남습니다. 필요하면 해제도 가능하지만, 해제 역시 이력에 기록됩니다.' },
      { heading: '회계기준 설정', body: '상각률·내용연수 같은 정책값을 열람합니다. 값 변경은 보통 회계 담당과 협의가 필요한 영역입니다.' },
    ],
  },
  {
    id: 'inventory',
    code: 'B-04',
    part: ASSET_MANUAL_PARTS.ADMIN,
    title: '전수조사(실사) 관리',
    purpose: '회사 전체 또는 특정 범위의 자산을 실제로 보유하고 있는지 정기적으로 확인하는 캠페인을 만들고 운영합니다.',
    blocks: [
      {
        heading: '사용 방법',
        steps: [
          '"전수조사 생성"에서 대상 범위를 정합니다 — 관리자가 직접 부서·위치를 지정하거나, 각 임직원이 본인 배정분을 스스로 확인하는 방식 중 선택합니다.',
          '생성 후 "진행 현황"에서 참여자별 진행률(미참여/부분완료/완료 인원)을 확인합니다.',
          '제출된 결과를 검수해 승인하거나, 문제가 있으면 반려해 재확인을 요청합니다. 필요하면 관리자가 직접 대신 확인 처리할 수도 있습니다.',
          '마감 시점에 남은 미확인 건은 "미확인 자산 종결"로 일괄 또는 건별 정리한 뒤 "조사 종료 확정"을 누릅니다.',
          '"리포트" 메뉴에서 전체 결과 요약과 분실 처리 내역을 확인하고, 필요하면 엑셀로 내려받습니다.',
        ],
      },
      { heading: '반복 시행', body: '이전 조사를 템플릿 삼아 복제하면 다음 회차 조사를 빠르게 새로 만들 수 있습니다.' },
    ],
  },
  {
    id: 'loan',
    code: 'B-05',
    part: ASSET_MANUAL_PARTS.ADMIN,
    title: '대여 관리 · 프로세스 설정',
    purpose: '전체 대여 현황을 관리하고, 대여 절차에 승인 단계를 둘지 여부를 설정합니다.',
    blocks: [
      { heading: '대여 현황', body: '현재 대여 중인 전체 자산 목록을 확인합니다. 관리자가 특정 임직원을 대신해 직접 대여를 등록(대행 등록)할 수도 있습니다.' },
      { heading: '대여 승인대기', body: '임직원이 신청한 대여 건을 승인하거나 반려합니다.' },
      { heading: '프로세스 설정', body: '대여·수령/반납 절차의 승인 단계를 켜고 끌 수 있습니다. 꺼두면 신청과 동시에 바로 확정되는 방식으로 흐름이 바뀝니다.' },
    ],
  },
  {
    id: 'intangible',
    code: 'B-06',
    part: ASSET_MANUAL_PARTS.ADMIN,
    title: '무형자산 관리',
    purpose: '도메인, 인증서, 상표권처럼 실물이 없는 자산을 만료일 기준으로 관리합니다.',
    blocks: [
      {
        bullets: [
          '목록·등록·수정은 유형자산과 비슷한 흐름으로 진행합니다.',
          '상세 화면에서 과거 갱신 이력을 확인할 수 있고, 갱신할 때는 새 만료일을 입력합니다.',
        ],
      },
    ],
  },
  {
    id: 'license',
    code: 'B-07',
    part: ASSET_MANUAL_PARTS.ADMIN,
    title: '라이선스 · SW 관리',
    purpose: '소프트웨어 라이선스 구매 내역과 배정 현황을 관리합니다.',
    blocks: [
      { heading: '라이선스 관리', body: '목록·등록·수정 외에, 상세 화면에서 구매 내역·배정 현황·포함된 소프트웨어를 함께 확인할 수 있습니다.' },
      { heading: '소프트웨어 마스터 관리', body: '라이선스를 등록할 때 자동완성으로 뜨는 소프트웨어 이름의 원천 데이터를 관리합니다.' },
    ],
  },
  {
    id: 'rental',
    code: 'B-08',
    part: ASSET_MANUAL_PARTS.ADMIN,
    title: '렌탈 · 구독 관리',
    purpose: '정기적으로 결제가 발생하는 렌탈·구독 계약을 관리합니다.',
    blocks: [
      { body: '목록·등록·수정 후, 상세 화면에서 월별·연별 결제 스케줄을 확인할 수 있습니다.' },
    ],
  },
  {
    id: 'vendor',
    code: 'B-09',
    part: ASSET_MANUAL_PARTS.ADMIN,
    title: '공급사 관리',
    purpose: '렌탈·라이선스 등에서 공용으로 쓰는 거래처(공급사) 정보를 관리합니다.',
    blocks: [],
  },
  {
    id: 'ack',
    code: 'B-10',
    part: ASSET_MANUAL_PARTS.ADMIN,
    title: '확인서 관리',
    purpose: '자산 수령 확인서를 발송하고, 임직원의 승인 결과를 확인합니다.',
    blocks: [
      { heading: '확인서 관리', body: '수령 확인서를 요청(발송)하고, 담당자가 최종 수령 상태를 확인한 뒤 승인 처리합니다.' },
      { heading: '확인서 문구 관리', body: '확인서에 들어가는 안내 문구(템플릿)를 편집합니다.' },
    ],
  },
  {
    id: 'ticket',
    code: 'B-11',
    part: ASSET_MANUAL_PARTS.ADMIN,
    title: '티켓 관리',
    purpose: '임직원이 등록한 자산 관련 지원요청을 접수하고 처리합니다.',
    blocks: [
      {
        heading: '사용 방법',
        bullets: [
          'PC 화면에서는 칸반 보드로 표시되며, 카드를 드래그해 상태(대기/진행중/완료 등)를 바꿀 수 있습니다.',
          '모바일에서는 같은 내용이 목록형 화면으로 자동 전환됩니다.',
          '코멘트로 요청자와 주고받으며, 관리자가 직접 티켓을 등록할 수도 있습니다.',
        ],
      },
    ],
  },
  {
    id: 'base',
    code: 'B-12',
    part: ASSET_MANUAL_PARTS.ADMIN,
    title: '자산 기준정보',
    purpose: '자산 등록 시 선택하는 분류 체계와 위치 목록을 관리합니다.',
    blocks: [
      {
        bullets: [
          '자산 종류(분류) 관리 — 자산을 묶는 카테고리 체계를 관리합니다.',
          '자산 위치 관리 — 보관·사용 위치 목록을 관리합니다.',
        ],
      },
    ],
  },
  {
    id: 'dashboard',
    code: 'B-13',
    part: ASSET_MANUAL_PARTS.ADMIN,
    title: '대시보드',
    purpose: '자산관리 전체 현황을 한 화면에서 확인합니다.',
    blocks: [
      {
        bullets: [
          '자산 총계와 종류별·위치별·상태별 현황',
          '월별 비용 추이와 만료 예정 항목',
          '대여 현황, 라이선스 정합성, 전수조사 진행률 위젯',
          '티켓 처리 현황 KPI',
        ],
      },
      { body: '"대시보드 설정"에서 위젯 표시 여부와 순서를 원하는 대로 바꿀 수 있습니다.' },
    ],
  },

  // ---------------- 부록 ----------------
  {
    id: 'appendix',
    code: 'C',
    part: ASSET_MANUAL_PARTS.APPENDIX,
    title: '상태값 한눈에 보기',
    purpose: '유형자산 하나의 상태는 사용상태와 배정상태 두 값의 조합으로 표시됩니다. 예: "사용 + 개인배정"은 누군가 지금 정상적으로 쓰고 있는 자산, "보관 + 공용"은 창고에 있는 공용 자산입니다.',
    blocks: [
      {
        heading: '사용상태',
        table: {
          headers: ['상태', '의미'],
          rows: [
            [{ badge: 'good', label: '사용' }, '정상적으로 쓰고 있는 자산'],
            [{ badge: 'neutral', label: '보관' }, '창고 등에 보관 중이며 당장 쓰이지 않는 자산'],
            [{ badge: 'warn', label: '수리중' }, '고장 등으로 수리가 진행 중인 자산'],
            [{ badge: 'critical', label: '불용' }, '더는 정상 사용할 수 없어 처분을 앞둔 자산'],
            [{ badge: 'neutral', label: '처분완료' }, '처분 처리가 완료된 자산'],
          ],
        },
      },
      {
        heading: '배정상태',
        table: {
          headers: ['상태', '의미'],
          rows: [
            [{ badge: 'neutral', label: '미배정' }, '아직 누구에게도 배정되지 않은 자산'],
            [{ badge: 'good', label: '개인배정' }, '특정 임직원 한 명에게 배정된 자산'],
            [{ badge: 'neutral', label: '공용' }, '부서·팀이 함께 쓰는 자산'],
            [{ badge: 'good', label: '대여가능' }, '공용 대여 목록에 올라와 신청을 받을 수 있는 자산'],
            [{ badge: 'warn', label: '대여중' }, '현재 누군가 빌려서 쓰고 있는 자산'],
          ],
        },
      },
    ],
    note: { type: 'info', text: '이 가이드에 없는 화면이나 기능이 궁금하면 "내 티켓"으로 자산관리자에게 문의해 주세요.' },
  },
];

/**
 * 메뉴(programMapping)의 첫 segment(=src/pages 폴더명, kebab-case) → 매뉴얼 section id.
 * PageHeader가 menuUrl.split('/')[0]으로 조회해서, 해당 화면 섹션으로 바로 연 상태로 다이얼로그를 띄운다.
 * 여기 없는 페이지(자산관리 영역 밖)에서는 도움말 버튼 자체를 숨긴다.
 */
export const ASSET_MANUAL_PROGRAM_MAP = {
  'my-inventory': 'my-inventory',
  'loan-available': 'my-loan',
  'my-loan': 'my-loan',
  'my-license': 'my-license',
  'my-acknowledgement': 'my-ack',
  'my-ticket': 'my-ticket',
  'asset-scan': 'qr-common',

  'tangible-asset-management': 'tangible',
  'disposal-asset-management': 'disposal',
  'depreciation-management': 'depreciation',
  'accounting-settings': 'depreciation',
  'inventory-management': 'inventory',
  'loan-management': 'loan',
  'loan-pending-approval': 'loan',
  'process-config-settings': 'loan',
  'intangible-asset-management': 'intangible',
  'license-management': 'license',
  'software-management': 'license',
  'rental-management': 'rental',
  'vendor-management': 'vendor',
  'acknowledgement-management': 'ack',
  'ack-template-management': 'ack',
  'ticket-management': 'ticket',
  'asset-category-management': 'base',
  'asset-location-management': 'base',
  'dashboard': 'dashboard',
};
