package com.winitech.common.domain.common;

import com.sun.istack.NotNull;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonAuthorizationGroupPermissionId.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 11:20
 **/
@NoArgsConstructor
public class CommonAuthorizationGroupPermissionId implements Serializable {
	private UUID authorizationGroupId;
	private UUID menuId;

	public CommonAuthorizationGroupPermissionId(UUID authorizationGroupId, UUID menuId) {
		this.authorizationGroupId = authorizationGroupId;
		this.menuId = menuId;
	}
}
