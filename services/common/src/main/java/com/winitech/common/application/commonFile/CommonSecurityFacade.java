//package com.winitech.common.application.commonFile;
//
//import com.winitech.common.domain.common.CommonPublicKeyCommand;
//import com.winitech.common.domain.common.CommonPublicKeyInfo;
//import com.winitech.common.domain.common.CommonPublicKeyService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.nio.charset.StandardCharsets;
//import java.util.UUID;
//
///**
// * <pre>
// * com.winitech.common.application.commonFile
// * └ CommonSecurityFacade.java
// * </pre>
// * @author : coding (클라우드팀)
// * @since : 2024-12-31 09:11
// **/
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class CommonSecurityFacade {
//	private final CommonPublicKeyService commonPublicKeyService;
//
//	public CommonPublicKeyInfo registerPublicKey(CommonPublicKeyCommand command) {
//		return commonPublicKeyService.registerCommonPublicKey(command);
//	}
//
//	public String publicKeyDecryptionTest(UUID publicKeyId, String clientPublicKey, String encryptedData) {
//		CommonPublicKeyInfo info = commonPublicKeyService.searchCommonPublicKeyById(publicKeyId);
//
//		return commonPublicKeyService.decrypt(info.getPrivateKey(), clientPublicKey, encryptedData);
//	}
//
//	public String encrypt(String privateKey, String publicKey, String plainData) {
//		if (plainData == null) {
//			plainData = "";
//		}
//
//		return commonPublicKeyService.encrypt(privateKey, publicKey, plainData.getBytes(StandardCharsets.UTF_8));
//	}
//
//	public String encryptNaive(String privateKey, String publicKey, String plainData) {
//		if (plainData == null) {
//			plainData = "";
//		}
//
//		return commonPublicKeyService.encryptNaive(privateKey, publicKey, plainData.getBytes(StandardCharsets.UTF_8));
//	}
//}
