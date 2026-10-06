package com.winitech.common.infrastructure.common;

import com.winitech.common.domain.common.CommonPublicKey;
import com.winitech.common.domain.common.CommonPublicKeyCommand;
import com.winitech.common.domain.common.CommonPublicKeyStore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonPublicKeyStoreImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-30 18:30
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonPublicKeyStoreImpl implements CommonPublicKeyStore {
	private final CommonPublicKeyRepository commonPublicKeyRepository;

	@Override
	public CommonPublicKey store(CommonPublicKey commonPublicKey) {
		return commonPublicKeyRepository.save(commonPublicKey);
	}

	@Override
	public CommonPublicKey modify(CommonPublicKey commonPublicKey, CommonPublicKeyCommand command) {
		commonPublicKey.setPublicKey(command.getPublicKey());
		commonPublicKey.setPrivateKey(command.getPrivateKey());
		commonPublicKey.setKeyGenDate(command.getKeyGenDate());
		commonPublicKey.setEncryptKey(command.getEncryptKey());
		return commonPublicKeyRepository.save(commonPublicKey);
	}

	@Override
	public void remove(UUID id) {
		commonPublicKeyRepository.deleteById(id);
	}
}
