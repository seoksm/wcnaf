package com.winitech.common.interfaces.inboundAdapter.eventListener;

import com.winitech.common.domain.common.CommonAuthorizationGroupUserInfo;
import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.infrastructure.authorizationGroupUser
 * └ CommonAuthorizationGroupUserSyncMessage.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 14:39
 **/
@Data
@NoArgsConstructor
public class CommonAuthorizationGroupUserSyncMessage {
	OPERATION op;
	Integer ts_ms;
	List<UUID> authorizationGroupIdList;
	List<CommonAuthorizationGroupUserInfo> authorizationGroupUserInfoList;

	@Getter
	@RequiredArgsConstructor
	public enum OPERATION {
		s("SYNC");
		private final String description;
	}

	@Builder
	public CommonAuthorizationGroupUserSyncMessage(OPERATION op, Integer ts_ms, List<UUID> authorizationGroupIdList, List<CommonAuthorizationGroupUserInfo> authorizationGroupUserInfoList) {
		this.op = op;
		this.ts_ms = ts_ms;
		this.authorizationGroupIdList = authorizationGroupIdList;
		this.authorizationGroupUserInfoList = authorizationGroupUserInfoList;
	}
}
