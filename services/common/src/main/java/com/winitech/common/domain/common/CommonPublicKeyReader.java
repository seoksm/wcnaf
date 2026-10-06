package com.winitech.common.domain.common;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonPublicKeyReader.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-30 18:02
 **/
public interface CommonPublicKeyReader {
	CommonPublicKey getCommonPublicKeyById(UUID id);
}