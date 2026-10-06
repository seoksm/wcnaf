-- S-240~242 불용자산 처리(불용/복귀/처분).
--
-- D4: "상각 대상은 life_status 기준. 불용 처리한 달까지 상각 후 다음 달부터 중단" - 이 규칙을
-- 계산하려면 "언제 불용/처분완료가 됐는지"를 알아야 한다. life_status가 바뀔 때마다 갱신되는
-- 이 시각을 감가상각 동결 시점으로 사용한다(DepreciationCalculator 참고).
ALTER TABLE tangible_asset ADD COLUMN life_status_changed_at timestamp without time zone;
COMMENT ON COLUMN tangible_asset.life_status_changed_at IS
    '생애상태(life_status)가 마지막으로 바뀐 시각 - 불용/처분완료 자산의 감가상각 동결 시점(D4)으로 사용';

-- 기존 자산은 생애상태가 언제 지금 값이 됐는지 이력을 소급 복원할 수 없으므로, 자산 생성 시각을
-- 최선의 근사값으로 채운다 - 대부분 USE 상태로 등록되었을 것이므로 불용/처분 동결 계산에는 영향이
-- 없고, 이미 불용/처분 상태로 등록된 극히 일부만 근사치가 된다(운영에서 필요시 수동 보정 가능).
UPDATE tangible_asset SET life_status_changed_at = create_at WHERE life_status_changed_at IS NULL;

ALTER TABLE tangible_asset ALTER COLUMN life_status_changed_at SET NOT NULL;

-- 처분 처리(S-242) 결과 - 자산 1건당 최대 1건(재처분 없음, Q-28: 처분완료는 되돌릴 수 없음).
CREATE TABLE disposal_asset
(
    disposal_asset_id      uuid                   NOT NULL,
    create_at              timestamp without time zone,
    update_at              timestamp without time zone,
    tangible_asset_id      uuid                   NOT NULL,
    book_value_at_disposal numeric(15,2)          NOT NULL,
    disposal_reason_code   character varying(20)  NOT NULL,
    disposal_amount        numeric(15,2),
    counterparty           character varying(200),
    memo                   character varying(1000),
    disposed_by            uuid                   NOT NULL,
    CONSTRAINT disposal_asset_pkey PRIMARY KEY (disposal_asset_id),
    CONSTRAINT disposal_asset_tangible_asset_fk FOREIGN KEY (tangible_asset_id) REFERENCES tangible_asset (tangible_asset_id),
    CONSTRAINT disposal_asset_tangible_asset_uk UNIQUE (tangible_asset_id)
);

COMMENT ON TABLE disposal_asset IS 'S-242 처분 처리 결과 - 자산당 최대 1건(재처분 없음)';
COMMENT ON COLUMN disposal_asset.book_value_at_disposal IS '처분 시점(=불용 동결 시점)의 장부가 - 이후 다시 계산하지 않고 고정';
COMMENT ON COLUMN disposal_asset.disposal_reason_code IS 'SALE/SCRAP/DONATION/LOSS/THEFT/OTHER';
COMMENT ON COLUMN disposal_asset.disposal_amount IS '매각·처분 금액 - 폐기 등 금액이 없으면 NULL';
COMMENT ON COLUMN disposal_asset.counterparty IS '매각 상대방 - 해당 없으면 NULL';
