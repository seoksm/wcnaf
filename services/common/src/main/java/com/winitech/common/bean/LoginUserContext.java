package com.winitech.common.bean;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * com.winitech.common.bean
 * └ UserLoginInfo.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/07
 **/
@RequestScope
@Component
@Getter
@Setter
public class LoginUserContext {
	private UUID userId;
	private UUID organizationId;
	private String organizationCode;
	private UUID userSessionId;
	private String encryptKey;
	private OffsetDateTime accessTokenExpiredAt;
	private String userIp;
    private String authGroupCode;
	
	public void clear() {
		this.userId = null;
		this.organizationId = null;
		this.organizationCode = null;
		this.userSessionId = null;
		this.encryptKey = null;
		this.accessTokenExpiredAt = null;
		this.userIp = null;
        this.authGroupCode = null;
	}
}
