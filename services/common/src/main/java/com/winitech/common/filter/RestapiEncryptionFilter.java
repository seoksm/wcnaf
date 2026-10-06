package com.winitech.common.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.winitech.common.application.common.CommonEncFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.RequestFacade;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

/**
 * <pre>
 * com.winitech.common.filter
 * └ RestapiEncryptionFilter.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-31 17:34
 **/
@Slf4j
@Component
@RequiredArgsConstructor
//@ConditionalOnProperty(name = "winitech.security.use-restapi-encryption", havingValue = "true")
public class RestapiEncryptionFilter implements Filter {
    private final CommonEncFacade commonEncFacade;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String pubkey = ((RequestFacade) request).getHeader("X-Enc-Key");
        String iv = ((RequestFacade) request).getHeader("X-Enc-Iv");

        if(pubkey != null && iv != null){
            byte[] body = request.getInputStream().readAllBytes();
            byte[] decrypted = commonEncFacade.getBody(pubkey, iv, body);
            ModifiedBodyRequestWrapper newRequest = new ModifiedBodyRequestWrapper((HttpServletRequest) request, decrypted);
            newRequest.removeHeader("X-Enc-Key");
            newRequest.removeHeader("X-Enc-Iv");
            chain.doFilter(newRequest, response);
        }
        else {
            chain.doFilter(request, response);
        }
    }
}


class ModifiedBodyRequestWrapper extends HttpServletRequestWrapper {
    private final byte[] body;
    private final Set<String> removedHeaders = new HashSet<>();

    public ModifiedBodyRequestWrapper(HttpServletRequest request, byte[] body) {
        super(request);
        this.body = body;
    }

    //헤더오버라이드
    public void removeHeader(String name) {
        removedHeaders.add(name.toLowerCase());
    }
    @Override
    public String getHeader(String name) {
        if (removedHeaders.contains(name.toLowerCase())) return null;
        return super.getHeader(name);
    }
    @Override
    public Enumeration<String> getHeaders(String name) {
        if (removedHeaders.contains(name.toLowerCase())) {
            return Collections.emptyEnumeration();
        }
        return super.getHeaders(name);
    }
    @Override
    public Enumeration<String> getHeaderNames() {
        List<String> names = new ArrayList<>();
        Enumeration<String> original = super.getHeaderNames();
        while (original.hasMoreElements()) {
            String name = original.nextElement();
            if (!removedHeaders.contains(name.toLowerCase())) {
                names.add(name);
            }
        }
        return Collections.enumeration(names);
    }
    @Override
    public int getIntHeader(String name) {
        if (removedHeaders.contains(name.toLowerCase())) return -1;
        return super.getIntHeader(name);
    }

    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override public int read() { return bais.read(); }
            @Override public boolean isFinished() { return bais.available() == 0; }
            @Override public boolean isReady() { return true; }
            @Override public void setReadListener(ReadListener listener) { }
        };
    }
    @Override
    public BufferedReader getReader() {
        var gis = getInputStream();
        InputStreamReader isr = new InputStreamReader(gis, StandardCharsets.UTF_8);
        return new BufferedReader(isr);
    }
    @Override
    public int getContentLength() { return body.length; }
    @Override
    public long getContentLengthLong() { return body.length; }
}
