package com.winitech.common.domain.common;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonPublicKeyCommand.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-30 18:01
 **/
@Getter
@Builder
@ToString
public class CommonPublicKeyCommand {
	private final UUID id;
	private final String publicKey;
	private final String privateKey;
	private final OffsetDateTime keyGenDate;
	private final String encryptKey;

	public CommonPublicKey toEntity() {
		return CommonPublicKey.builder()
				.id(id)
				.publicKey(publicKey)
				.privateKey(privateKey)
				.keyGenDate(keyGenDate)
				.encryptKey(encryptKey)
				.build();
	}
}
