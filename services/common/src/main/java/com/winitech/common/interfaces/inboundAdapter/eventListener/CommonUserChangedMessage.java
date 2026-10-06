package com.winitech.common.interfaces.inboundAdapter.eventListener;

import lombok.*;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.interfaces.inboundAdapter.eventListener
 * └ CommonUserChangedMessage.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-21 13:09
 **/
@Data
@NoArgsConstructor
public class CommonUserChangedMessage {
	OPERATION op;
	Integer ts_ms;
	CommonUserInfo before;
	CommonUserInfo after;
	UserOrganizationInfo userOrganizationInfo;

	@Getter
	@RequiredArgsConstructor
	public enum OPERATION {
		c("CREATE"), u("UPDATE"), d("DELETE"), r("REMOVE_FROM_ORG");
		private final String description;
	}

	@Builder
	public CommonUserChangedMessage(OPERATION op, Integer ts_ms, CommonUserInfo before, CommonUserInfo after, UserOrganizationInfo userOrganizationCdcInfo) {
		this.op = op;
		this.ts_ms = ts_ms;
		this.before = before;
		this.after = after;
		this.userOrganizationInfo = userOrganizationCdcInfo;
	}

	@Getter
	@ToString
	public class CommonUserInfo {
		private UUID id;
		private String username;
		private String firstName;
		private String lastName;
		private String fullName;

		private String email;
		private String phoneNumber;
		private String employeeNo;
		private String dutyName;
		private String departmentName;

		private String systemStatus;

		private String[] organizationIds;
		private String[] organizationCodes;
	}

	@Getter
	public class UserOrganizationInfo {
		private String id;
		private String organizationId;
		private String organizationCode;
	}
}
