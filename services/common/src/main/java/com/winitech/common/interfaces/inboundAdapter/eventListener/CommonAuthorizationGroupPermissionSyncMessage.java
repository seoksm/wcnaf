package com.winitech.common.interfaces.inboundAdapter.eventListener;

import com.winitech.common.domain.common.CommonAuthorizationGroupPermissionInfo;
import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonAuthorizationGroupPermissionSyncMessage.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 14:54
 **/
@Data
@NoArgsConstructor
public class CommonAuthorizationGroupPermissionSyncMessage {
	OPERATION op;
	Integer ts_ms;
	List<UUID> authorizationGroupIdList;
	List<CommonAuthorizationGroupPermissionInfo> authorizationGroupPermissionInfoList;

	@Getter
	@RequiredArgsConstructor
	public enum OPERATION {
		s("SYNC");
		private final String description;
	}

	@Builder
	public CommonAuthorizationGroupPermissionSyncMessage(
			OPERATION op, 
			Integer ts_ms,
			List<UUID> authorizationGroupIdList, 
			List<CommonAuthorizationGroupPermissionInfo> authorizationGroupPermissionInfoList
	) {
		this.op = op;
		this.ts_ms = ts_ms;
		this.authorizationGroupIdList = authorizationGroupIdList;
		this.authorizationGroupPermissionInfoList = authorizationGroupPermissionInfoList;
	}
}
