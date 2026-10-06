package com.winitech.common.domain;

import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.OffsetDateTime;

/**
 * com.winitech.access.domain
 * ㄴ AbstractEntity.java
 * @author : 박준희 과장 (부설연구소)
 * @since : 2023-07-26 오후 4:06
 * @see : None
 **/
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AbstractEntity {
	@CreationTimestamp
	private OffsetDateTime createAt;
	@UpdateTimestamp
	private OffsetDateTime updateAt;
}
