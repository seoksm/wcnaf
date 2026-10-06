-- S-300~306, 308 전수조사(자산실사) - 관리자 화면 (임직원 모바일 검수 S-310/311은 별도 단계에서 구현).
--
-- I1: 대상 자산은 조사 시작 시점에 스냅샷으로 고정한다 - inventory_target이 그 스냅샷이고, 조사가
-- 끝날 때까지(그리고 끝난 뒤에도, I6) 대상 집합 자체는 바뀌지 않는다. 검수 "결과"(상태·이상유형·
-- 종결처리 등)는 조사 진행 중 계속 바뀌므로 inventory_result로 분리해, "무엇을 조사하는가"(불변)와
-- "그 결과가 어떻게 됐는가"(가변)를 서로 다른 테이블로 나눈다.
CREATE TABLE inventory
(
    inventory_id                  uuid                  NOT NULL,
    create_at                     timestamp without time zone,
    update_at                     timestamp without time zone,
    title                         character varying(200) NOT NULL,
    inventory_type                character varying(20)  NOT NULL,
    status                        character varying(20)  NOT NULL DEFAULT 'IN_PROGRESS',
    approval_required             boolean                 NOT NULL DEFAULT false,
    allow_new_asset_registration  boolean                 NOT NULL DEFAULT false,
    closed_at                     timestamp without time zone,
    closed_by                     uuid,
    recurrence_rule               character varying(20),
    created_by                    uuid                    NOT NULL,
    CONSTRAINT inventory_pkey PRIMARY KEY (inventory_id)
);

COMMENT ON TABLE inventory IS 'S-300~306,308 전수조사(자산실사) 1건';
COMMENT ON COLUMN inventory.inventory_type IS 'MEMBER(임직원형, 개인배정 자산) / ADMIN(관리자형, 공용·미배정 자산) - Q-32';
COMMENT ON COLUMN inventory.status IS 'IN_PROGRESS / CLOSED - CLOSED 이후 inventory_result는 불변(I6)';
COMMENT ON COLUMN inventory.approval_required IS '검수자 승인 단계(2단: 확인→승인) 여부 - 조사별 옵션(Q-31)';
COMMENT ON COLUMN inventory.allow_new_asset_registration IS '실사 중 미등록 자산 발견 시 임직원 신규 등록 허용 여부(선택 옵션, §0)';
COMMENT ON COLUMN inventory.recurrence_rule IS 'SEMIANNUAL/ANNUAL/QUARTERLY - 반복 시행 주기 선호값(Q-33 기본 반기). 자동 실행 스케줄러는 별도 구현';

-- I1의 스냅샷 본체 - 조사 1건에 포함된 자산 1건. member_id는 임직원형(MEMBER)에서만 채워지고
-- (그 자산을 책임지는 사람), 관리자형(ADMIN)은 NULL - 특정 개인이 아니라 inventory_inspector
-- 풀 전체가 검수를 담당한다(Q-32).
CREATE TABLE inventory_target
(
    inventory_target_id    uuid                  NOT NULL,
    create_at              timestamp without time zone,
    update_at              timestamp without time zone,
    inventory_id           uuid                  NOT NULL,
    tangible_asset_id      uuid                  NOT NULL,
    member_id              uuid,
    expected_location_id   uuid,
    expected_assign_type   character varying(20),
    CONSTRAINT inventory_target_pkey PRIMARY KEY (inventory_target_id),
    CONSTRAINT inventory_target_inventory_fk FOREIGN KEY (inventory_id) REFERENCES inventory (inventory_id),
    CONSTRAINT inventory_target_tangible_asset_fk FOREIGN KEY (tangible_asset_id) REFERENCES tangible_asset (tangible_asset_id),
    CONSTRAINT inventory_target_unique UNIQUE (inventory_id, tangible_asset_id)
);

COMMENT ON TABLE inventory_target IS 'I1 스냅샷 - 조사 시작 시점에 고정한 대상 자산(+책임자) 목록. 조사 중 변경되지 않는다';
COMMENT ON COLUMN inventory_target.expected_location_id IS '조사 시작 시점 자산위치 스냅샷 - 검수 결과의 위치불일치 판정 기준';
COMMENT ON COLUMN inventory_target.expected_assign_type IS '조사 시작 시점 배정형태 스냅샷(참고용 표시)';

-- 관리자형(ADMIN) 조사의 검수자 풀 - 특정 자산에 고정되지 않고, 이 조사에 속한 모든 대상 자산을
-- 검수할 수 있는 사람들이다.
CREATE TABLE inventory_inspector
(
    inventory_inspector_id  uuid                  NOT NULL,
    create_at               timestamp without time zone,
    inventory_id            uuid                  NOT NULL,
    member_id               uuid                  NOT NULL,
    CONSTRAINT inventory_inspector_pkey PRIMARY KEY (inventory_inspector_id),
    CONSTRAINT inventory_inspector_inventory_fk FOREIGN KEY (inventory_id) REFERENCES inventory (inventory_id),
    CONSTRAINT inventory_inspector_unique UNIQUE (inventory_id, member_id)
);

COMMENT ON TABLE inventory_inspector IS 'S-302 관리자형 조사의 검수자 풀(자산별 고정 아님)';

-- 임직원형(MEMBER) 생성 시 사전 제외한 인원 - 휴직·장기출장자 등(Q-37). 이번 구현은 관리자가
-- 수동으로 선택하는 방식만 지원한다 - member 상태값에 "휴직"이 아직 없어(원 설계는 상태 자동
-- 판정을 전제했다) 자동 판정은 다음 단계로 미룬다.
CREATE TABLE inventory_excluded_member
(
    inventory_excluded_member_id  uuid  NOT NULL,
    create_at                     timestamp without time zone,
    inventory_id                  uuid  NOT NULL,
    member_id                     uuid  NOT NULL,
    CONSTRAINT inventory_excluded_member_pkey PRIMARY KEY (inventory_excluded_member_id),
    CONSTRAINT inventory_excluded_member_inventory_fk FOREIGN KEY (inventory_id) REFERENCES inventory (inventory_id),
    CONSTRAINT inventory_excluded_member_unique UNIQUE (inventory_id, member_id)
);

COMMENT ON TABLE inventory_excluded_member IS 'S-301 생성 시 수동으로 제외한 임직원(Q-37) - 자동(휴직상태 기준) 판정은 미구현';

-- I4/I5/S-304/S-308의 가변 검수 결과 - inventory_target과 1:1. 조사 진행 중 상태가 계속
-- 바뀌고(미확인→확인/승인대기→...), 반려되면 I5에 따라 다시 UNCONFIRMED로 돌아간다(별도 상태값
-- 없이 rejection_reason만 남기고 status를 되돌린다).
CREATE TABLE inventory_result
(
    inventory_result_id   uuid                   NOT NULL,
    create_at              timestamp without time zone,
    update_at              timestamp without time zone,
    inventory_target_id    uuid                   NOT NULL,
    status                 character varying(20)  NOT NULL DEFAULT 'UNCONFIRMED',
    anomaly_type           character varying(20),
    note                   character varying(1000),
    reviewed_by            uuid,
    reviewed_at            timestamp without time zone,
    rejection_reason       character varying(500),
    closure_action         character varying(20),
    closure_reason_code    character varying(20),
    closure_note           character varying(500),
    CONSTRAINT inventory_result_pkey PRIMARY KEY (inventory_result_id),
    CONSTRAINT inventory_result_target_fk FOREIGN KEY (inventory_target_id) REFERENCES inventory_target (inventory_target_id),
    CONSTRAINT inventory_result_target_uk UNIQUE (inventory_target_id)
);

COMMENT ON TABLE inventory_result IS 'I4/I5/S-304/S-308 검수 결과 - inventory_target과 1:1, 조사 중 계속 갱신됨';
COMMENT ON COLUMN inventory_result.status IS 'UNCONFIRMED/PENDING_APPROVAL/CONFIRMED/ANOMALY - I4: ANOMALY는 CONFIRMED와 별도 상태';
COMMENT ON COLUMN inventory_result.anomaly_type IS 'DAMAGE(파손)/LOCATION_MISMATCH(위치불일치)/WRONG_HOLDER(타인보유)/OTHER - status=ANOMALY일 때만';
COMMENT ON COLUMN inventory_result.rejection_reason IS 'I5: 반려 사유 - 반려 시 status는 UNCONFIRMED로 되돌아가고 이 사유만 남는다';
COMMENT ON COLUMN inventory_result.closure_action IS 'S-308: CARRY_OVER(차기이월)/MANUAL_VERIFY(소재확인)/LOST(분실) - 조사 종료 시 미확인 건에만 설정, LOST는 일괄 처리 불가(R6)';
COMMENT ON COLUMN inventory_result.closure_reason_code IS 'S-308: NOT_PARTICIPATED(미참여)/ON_LEAVE(휴직)/LABEL_DAMAGED(라벨훼손)/LOCATION_UNKNOWN(소재불명)';
