package com.winitech.system.domain.userSession;

import com.winitech.common.domain.AbstractEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * com.winitech.system.domain.userSession
 * └ UserSession.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/02
 **/
@Slf4j
@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@NoArgsConstructor
@Table(name = "user_session")
public class UserSession extends AbstractEntity {
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
	private UUID id;
	@NotNull
	private UUID userId;
	@NotNull
	private OffsetDateTime loginAt;
	private OffsetDateTime logoutAt;
	@NotNull
	private String loginIp;
	private String refreshToken;
	private OffsetDateTime refreshTokenExpiresAt;
	private Integer totalRefreshCnt;
	private Integer refreshCnt;
	private OffsetDateTime refreshTokenUpdatedAt;

	@Enumerated	(EnumType.STRING)
	@NonNull
	private UserSession.LoginStatus loginStatus;

	@Enumerated	(EnumType.STRING)
	@NonNull
	private UserSession.IpSecurityStatus ipSecurityStatus;

	@Getter
	@RequiredArgsConstructor
	public enum LoginStatus {
		LOGIN("로그인"), LOGOUT("로그아웃");
		private final String description;
	}
	
	@Getter
	@RequiredArgsConstructor
	public enum IpSecurityStatus {
		ENABLE("IP 보안 사용"), DISABLE("IP 보안 사용 안함");
		private final String description;
	}
	
	@Builder
	public UserSession(
			UUID id,
			UUID userId, 
			OffsetDateTime loginAt,
			OffsetDateTime logoutAt,
			String loginIp,
			
			String refreshToken,
			OffsetDateTime refreshTokenExpiresAt,
			Integer totalRefreshCnt,
			Integer refreshCnt,
			OffsetDateTime refreshTokenUpdatedAt,
			
			UserSession.LoginStatus loginStatus,
			UserSession.IpSecurityStatus ipSecurityStatus
	) {
		this.id = id;
		this.userId = userId;
		this.loginAt = loginAt;
		this.logoutAt = logoutAt;
		this.loginIp = loginIp;
		
		this.refreshToken = refreshToken;
		this.refreshTokenExpiresAt = refreshTokenExpiresAt;
		this.totalRefreshCnt = totalRefreshCnt;
		this.refreshCnt = refreshCnt;
		this.refreshTokenUpdatedAt = refreshTokenUpdatedAt;
		
		this.loginStatus = loginStatus;
		this.ipSecurityStatus = ipSecurityStatus;
	}
}
