package com.winitech.common.domain.common;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.common.domain.AbstractUuidEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonMenuAction.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 11:03
 **/
@Slf4j
@Getter
@Setter
@Entity
@NoArgsConstructor
public class CommonMenuAction extends AbstractUuidEntity {
	protected UUID menuId;
	protected UUID programId;
	protected String programCode;
	protected String actionType;
	protected String authType;
	protected String uri;

	@Builder
	public CommonMenuAction(UUID id, UUID menuId, UUID programId, String programCode, String actionType, String authType, String uri) {
		this.id = id;
		this.programId = programId;
		this.programCode = programCode;
		this.menuId = menuId;
		this.actionType = actionType;
		this.authType = authType;
		this.uri = uri;
	}
}
