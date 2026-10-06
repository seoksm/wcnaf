package com.winitech.common.infrastructure.common;

import com.winitech.common.domain.common.CommonPublicKey;
import com.winitech.common.domain.common.CommonPublicKeyReader;
import com.winitech.common.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonPublicKeyReaderImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-30 18:30
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonPublicKeyReaderImpl implements CommonPublicKeyReader {
	private final CommonPublicKeyRepository commonPublicKeyRepository;

	@Override
	public CommonPublicKey getCommonPublicKeyById(UUID id) {
		return commonPublicKeyRepository.findById(id).orElseThrow(EntityNotFoundException::new);
	}
}
