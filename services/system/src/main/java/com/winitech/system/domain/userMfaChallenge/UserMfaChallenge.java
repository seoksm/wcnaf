package com.winitech.system.domain.userMfaChallenge;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.system.domain.userSession.UserSession;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.userMfaChallenge
 * └ UserMfaChallenge.java
 * </pre>
 * 1차 인증(아이디/비밀번호) 통과 ~ 2차 인증(TOTP) 완료 사이의 상태를 서버에 보관한다.
 * JWT로 만들지 않고 DB에 저장하는 이유: 이 짧은 구간에 필요한 로그인 컨텍스트(조직, IP 보안
 * 설정 등)를 클라이언트가 들고 있다가 그대로 돌려주게 하면 위변조 방지를 위해 별도 서명 체계를
 * 또 만들어야 하는데, 이미 트랜잭션 DB가 있으므로 여기 저장하고 일회용으로 소비(consume)하는
 * 편이 더 단순하고 안전하다(만료/1회용 처리를 DB 상태로 바로 강제할 수 있음).
 * <p>
 * id는 {@link com.winitech.common.library.core.JpaUUIDv7Generator}(시간순 UUIDv7)를 쓰지 않고
 * 매번 완전 무작위({@link UUID#randomUUID()})로 발급한다 - 이 값 자체가 "비밀번호 검증을 통과했다"는
 * 사실을 증명하는 추측 불가능한 베어러 토큰 역할을 하기 때문이다.
 **/
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@Table(name = "user_mfa_challenge")
public class UserMfaChallenge extends AbstractEntity {
    @Id
    private UUID id;
    @NonNull
    private UUID userId;
    @NonNull
    private UUID organizationId;
    @NonNull
    private String loginIp;
    @Enumerated(EnumType.STRING)
    @NonNull
    private UserSession.IpSecurityStatus ipSecurityStatus;
    private String sessionEncryptKey;
    @NonNull
    private OffsetDateTime expiresAt;
    private OffsetDateTime consumedAt;

    @Builder
    public UserMfaChallenge(
            UUID userId,
            UUID organizationId,
            String loginIp,
            UserSession.IpSecurityStatus ipSecurityStatus,
            String sessionEncryptKey,
            OffsetDateTime expiresAt
    ) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.organizationId = organizationId;
        this.loginIp = loginIp;
        this.ipSecurityStatus = ipSecurityStatus;
        this.sessionEncryptKey = sessionEncryptKey;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return OffsetDateTime.now().isAfter(expiresAt);
    }

    public boolean isConsumed() {
        return consumedAt != null;
    }

    public void consume() {
        this.consumedAt = OffsetDateTime.now();
    }
}
