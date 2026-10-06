package com.winitech.common.interfaces.inboundAdapter.eventListener;

import com.winitech.common.domain.common.CommonMenuActionInfo;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.interfaces.inboundAdapter.eventListener
 * └ CommonUserSessionBlockMessage.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-21 15:34
 **/
@Data
@NoArgsConstructor
public class CommonUserSessionBlockMessage {
	CommonUserSessionBlockMessage.OPERATION op;
	Integer ts_ms;
	UUID userSessionId;
	OffsetDateTime accessTokenExpiresAt;
	UUID userId;

	@Getter
	@RequiredArgsConstructor
	public enum OPERATION {
		s("SYNC");
		private final String description;
	}

	@Builder
	public CommonUserSessionBlockMessage(CommonUserSessionBlockMessage.OPERATION op, Integer ts_ms, UUID userSessionId, OffsetDateTime accessTokenExpiresAt, UUID userId) {
		this.op = op;
		this.ts_ms = ts_ms;
		this.userSessionId = userSessionId;
		this.accessTokenExpiresAt = accessTokenExpiresAt;
		this.userId = userId;
	}
}
