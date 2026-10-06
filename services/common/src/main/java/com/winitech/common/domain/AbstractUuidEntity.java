package com.winitech.common.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

/**
 * com.winitech.common.domain
 * └ AbstractUuidEntity.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/12/13
 **/
@Getter
@Setter
@MappedSuperclass
public abstract class AbstractUuidEntity extends AbstractEntity {
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
	protected UUID id;
}
