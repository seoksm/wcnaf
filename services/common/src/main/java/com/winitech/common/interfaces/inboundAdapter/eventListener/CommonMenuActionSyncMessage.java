package com.winitech.common.interfaces.inboundAdapter.eventListener;

import com.winitech.common.domain.common.CommonMenuActionInfo;
import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonMenuActionSyncMessage.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 14:58
 **/
@Data
@NoArgsConstructor
public class CommonMenuActionSyncMessage {
	OPERATION op;
	Integer ts_ms;
	List<UUID> programIdList;
	List<CommonMenuActionInfo> menuActionInfoList;

	@Getter
	@RequiredArgsConstructor
	public enum OPERATION {
		s("SYNC");
		private final String description;
	}

	@Builder
	public CommonMenuActionSyncMessage(OPERATION op, Integer ts_ms, List<UUID> programIdList, List<CommonMenuActionInfo> menuActionInfoList) {
		this.op = op;
		this.ts_ms = ts_ms;
		this.programIdList = programIdList;
		this.menuActionInfoList = menuActionInfoList;
	}
}
