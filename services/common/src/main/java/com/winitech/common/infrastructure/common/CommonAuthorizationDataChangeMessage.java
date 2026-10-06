package com.winitech.common.infrastructure.common;

import lombok.*;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonAuthorizationDataChangeMessage.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-09 09:36
 **/
@Data
@NoArgsConstructor
public class CommonAuthorizationDataChangeMessage {
	private OPERATION op;
	private Integer ts_ms;
	private String message;

	@Getter
	@RequiredArgsConstructor
	public enum OPERATION {
		s("sync");
		private final String description;
	}
	@Builder
	public CommonAuthorizationDataChangeMessage(OPERATION op, Integer ts_ms, String message) {
		this.op = op;
		this.ts_ms = ts_ms;
		this.message = message;
	}
}
