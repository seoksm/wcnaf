package com.winitech.common.domain.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonEncServiceImpl implements CommonEncService{

    private final ObjectMapper objectMapper;

    @Value("${winitech.enc.private.key:}")
    private String privateKeyPath;

    @javax.annotation.PostConstruct
    private void validatePrivateKey() {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            keyFactory.generatePrivate(new PKCS8EncodedKeySpec(getPrivateKey()));
        } catch (Exception e) {
            throw new IllegalStateException(
                    "winitech.enc.private.key(환경변수 WINITECH_ENC_PRIVATE_KEY)가 설정되지 않았거나 "
                            + "유효한 RSA PKCS8 개인키가 아닙니다.", e);
        }
    }

    private byte[] getPrivateKey() throws IOException {
        return Base64.getDecoder().decode(privateKeyPath);
    }

    @Override
    public byte[] getBody(String pubkey, String iv, byte[] encBodyByte) {
        try {

            Map<String, String> json = objectMapper.readValue(encBodyByte, new TypeReference<>() {});
            String encBody = json.get("Enc");

            byte[] privKeyBytes = getPrivateKey();

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privKeyBytes));

            // RSA로 AES 키 복호화 (바이트로 받기)
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
            OAEPParameterSpec oaepSpec = new OAEPParameterSpec(
                    "SHA-256",
                    "MGF1",
                    MGF1ParameterSpec.SHA256,  // MGF1도 SHA-256으로
                    PSource.PSpecified.DEFAULT
            );

            rsaCipher.init(Cipher.UNWRAP_MODE, privateKey, oaepSpec);
            SecretKey aesKey = (SecretKey) rsaCipher.unwrap(Base64.getDecoder().decode(pubkey), "AES", Cipher.SECRET_KEY);

            // AES로 데이터 복호화
            byte[] ivBytes = Base64.getDecoder().decode(iv);
            byte[] encryptedBytes = Base64.getDecoder().decode(encBody);

            Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
            aesCipher.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(128, ivBytes));
            return aesCipher.doFinal(encryptedBytes);

        } catch (InvalidAlgorithmParameterException e) {
            log.error("Failed to InvalidAlgorithmParameterException: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Failed to enc payload: {}", e.getMessage());
        }

        return null;
    }

    @Override
    public String getPublicKey() {
        try {
            byte[] privKeyBytes = getPrivateKey();

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privKeyBytes);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            RSAPrivateCrtKey rsaKey = (RSAPrivateCrtKey) privateKey;
            RSAPublicKeySpec pubSpec = new RSAPublicKeySpec(rsaKey.getModulus(), rsaKey.getPublicExponent());
            PublicKey publicKey = keyFactory.generatePublic(pubSpec);
            return Base64.getEncoder().encodeToString(publicKey.getEncoded());

        } catch (IOException e) {
            log.error("Failed to create key by IOException: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Failed to create key: {}", e.getMessage());
        }

        return null;
    }
}

