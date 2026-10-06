package com.winitech.system.interfaces.inboundAdapter.web.programAction;

import com.winitech.common.domain.common.CommonMenuActionInfo;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.interfaces.inboundAdapter.web.programAction
 * └ ProgramActionProducer.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 15:09
 **/
public interface ProgramActionProducer {
	/**
	 * 프로그램 액션 CDC 메시지 발생
	 * menuActionIdList에서 지정한 메뉴 액션을 menuActionInfoList로 대체 
	 * @param menuIdList 교체할 메뉴 액션 ID 목록           
	 * @param menuActionInfoList 교체할 메뉴 액션 정보 목록
	 */
	void menuActionCdc(
			List<UUID> menuIdList,
			List<CommonMenuActionInfo> menuActionInfoList
	);
}
