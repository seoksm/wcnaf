package com.winitech.common.domain.common;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonPublicKeyInfo.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-30 18:01
 **/
@Getter
public class CommonPublicKeyInfo {
	private final UUID id;
	private final String publicKey;
	private final String privateKey;
	private final OffsetDateTime keyGenDate;
	private final String encryptKey;

	public CommonPublicKeyInfo(CommonPublicKey commonPublicKey) {
		this.id = commonPublicKey.getId();
		this.publicKey = commonPublicKey.getPublicKey();
		this.privateKey = commonPublicKey.getPrivateKey();
		this.keyGenDate = commonPublicKey.getKeyGenDate();
		this.encryptKey = commonPublicKey.getEncryptKey();
	}
}
