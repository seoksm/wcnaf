package com.winitech.common.domain.common;

import org.springframework.http.HttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

public interface CommonEncService {
    byte[] getBody(String pubkey, String iv, byte[] body);
    String getPublicKey();
}
