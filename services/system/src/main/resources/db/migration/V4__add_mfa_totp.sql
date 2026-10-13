-- MFA(TOTP, Google/Microsoft Authenticator 호환) 도입. 개인 선택(opt-in) 방식이며, 전사/관리자
-- 의무화 여부는 추후 정책으로만 결정한다(스키마상 강제하지 않음). 백업코드 분실 재발급은 본인
-- 셀프서비스가 아니라 관리자 승인을 거치도록 설계되어 있다(system/application/userMfa 참고).

ALTER TABLE users
    ADD COLUMN totp_enabled boolean NOT NULL DEFAULT false,
    ADD COLUMN totp_secret_encrypted text;

COMMENT ON COLUMN users.totp_enabled IS 'OTP 앱(TOTP) 2차 인증 사용 여부 - 개인 선택(opt-in)';
COMMENT ON COLUMN users.totp_secret_encrypted IS 'TOTP 비밀키. AES-256-GCM 암호화 저장(winitech.mfa.secret-key). 평문으로 절대 저장하지 않는다.';

CREATE TABLE user_mfa_backup_code (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    user_id uuid NOT NULL,
    code_hash character varying(255) NOT NULL,
    used_at timestamp without time zone,
    CONSTRAINT user_mfa_backup_code_pkey PRIMARY KEY (id),
    CONSTRAINT user_mfa_backup_code_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_user_mfa_backup_code_user_id ON user_mfa_backup_code(user_id);

COMMENT ON TABLE user_mfa_backup_code IS 'TOTP 기기 분실 대비 1회용 백업코드(해시로만 저장). 재발급은 관리자 승인 하에만 가능.';
COMMENT ON COLUMN user_mfa_backup_code.user_id IS '소유 사용자 ID';
COMMENT ON COLUMN user_mfa_backup_code.code_hash IS '백업코드 BCrypt 해시 (평문은 발급 시 1회만 노출)';
COMMENT ON COLUMN user_mfa_backup_code.used_at IS '사용된 일시 (null이면 미사용)';

-- 1차 인증(비밀번호) 통과 ~ 2차 인증(TOTP) 완료 사이의 임시 상태. id는 애플리케이션이
-- UUID.randomUUID()로 직접 발급한다(시간순 UUIDv7이 아님 - 추측 불가능한 베어러 토큰이어야 하므로).
CREATE TABLE user_mfa_challenge (
    id uuid NOT NULL,
    create_at timestamp without time zone,
    update_at timestamp without time zone,
    user_id uuid NOT NULL,
    organization_id uuid NOT NULL,
    login_ip character varying(255) NOT NULL,
    ip_security_status character varying(255) NOT NULL,
    session_encrypt_key character varying(255),
    expires_at timestamp without time zone NOT NULL,
    consumed_at timestamp without time zone,
    CONSTRAINT user_mfa_challenge_pkey PRIMARY KEY (id),
    CONSTRAINT user_mfa_challenge_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id)
);

COMMENT ON TABLE user_mfa_challenge IS '로그인 1단계(비밀번호) 통과 후 2단계(TOTP) 대기 상태. 발급 후 수 분 내 1회용으로 소비된다.';
COMMENT ON COLUMN user_mfa_challenge.expires_at IS '만료 일시(기본 3분) - 지나면 secondAuth에서 거부';
COMMENT ON COLUMN user_mfa_challenge.consumed_at IS '2차 인증 완료로 소비된 일시 (null이면 아직 대기중, 재사용 방지용)';
