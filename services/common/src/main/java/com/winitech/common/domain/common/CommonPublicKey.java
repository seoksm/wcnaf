package com.winitech.common.domain.common;

import com.winitech.common.domain.AbstractUuidEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonPublicKey.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-30 18:01
 **/
@Slf4j
@Getter
@Setter
@Entity
@NoArgsConstructor
public class CommonPublicKey extends AbstractUuidEntity {
	@Column(length = 1000)
	private String publicKey;

	@Column(length = 4000)
	private String privateKey;
	
	private OffsetDateTime keyGenDate;
	
	private String encryptKey;
	
	@Builder
	public CommonPublicKey(UUID id, String publicKey, String privateKey, OffsetDateTime keyGenDate, String encryptKey) {
		this.id = id;
		this.publicKey = publicKey;
		this.privateKey = privateKey;
		this.keyGenDate = keyGenDate;		
		this.encryptKey = encryptKey;
	}
}
