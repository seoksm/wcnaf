package com.winitech.system.domain.userPasswordLog;

import com.winitech.common.domain.AbstractUuidEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.userPasswordLog
 * └ UserPasswordLog.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-23 13:30
 **/
@Slf4j
@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@NoArgsConstructor
@Table(name = "user_password_log")
public class UserPasswordLog extends AbstractUuidEntity {
	@NonNull
	private UUID userId;	// 로그성 데이터이기 때문에 User 객체를 참조하지 않고 User의 id만 참조한다.
	@NonNull
	private String hashedPassword;
	private OffsetDateTime passwordChangedAt;
	private String passwordChangeIp;
	
	@Builder
	public UserPasswordLog(
			UUID id,
			UUID userId,
			String hashedPassword,
			OffsetDateTime passwordChangedAt,
			String passwordChangeIp
	) {
		this.id = id;
		this.userId = userId;
		this.hashedPassword = hashedPassword;
		this.passwordChangedAt = passwordChangedAt;
		this.passwordChangeIp = passwordChangeIp;
	}
}
