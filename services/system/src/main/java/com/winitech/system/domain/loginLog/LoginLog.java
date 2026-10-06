package com.winitech.system.domain.loginLog;

import com.winitech.common.domain.AbstractUuidEntity;
import com.winitech.common.library.WiniCom;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.loginLog
 * └ LoginLog.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-20 17:39
 **/
@Slf4j
@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@NoArgsConstructor
@Table(name = "login_log")
public class LoginLog extends AbstractUuidEntity {
	private String username;
	private String loginIp;

	@Enumerated(EnumType.STRING)
	@NonNull
	private LoginLogStatus loginLogStatus;
	
	private UUID userSessionId;
	private Integer errCnt;	
	private OffsetDateTime lockUntil;

	@Getter
	@RequiredArgsConstructor
	public enum LoginLogStatus {
		LOGIN("로그인"), 
		FAILED("로그인실패"),
		UNLOCK("로그인 잠금 해제");
		private final String description;
	}

	@Builder
	public LoginLog(
			UUID id,
			String username,
			String loginIp,
			LoginLogStatus loginLogStatus,
			UUID userSessionId,
			
			Integer errCnt,
			OffsetDateTime lockUntil
	) {
		this.id = id;
		this.username = username;
		this.loginIp = loginIp;
		this.loginLogStatus = loginLogStatus;
		this.userSessionId = userSessionId;
		
		this.errCnt = WiniCom.ifNull(errCnt, 0);
		this.lockUntil = lockUntil;
	}
}
