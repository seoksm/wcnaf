-- MFA 2차 인증 챌린지에 코드 검증 실패 횟수를 기록한다. 실패 횟수가 한도(기본 5회,
-- winitech.mfa.challenge-max-attempts)를 넘으면 챌린지를 즉시 폐기(consumed_at 기록)하여
-- 같은 챌린지 토큰으로 TOTP/백업코드를 무제한 대입하는 것을 막는다.

ALTER TABLE user_mfa_challenge
    ADD COLUMN fail_count integer NOT NULL DEFAULT 0;

COMMENT ON COLUMN user_mfa_challenge.fail_count IS '코드 검증 실패 누적 횟수 - 한도 초과 시 즉시 소비(폐기) 처리';
