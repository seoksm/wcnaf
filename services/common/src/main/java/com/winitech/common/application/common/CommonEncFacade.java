package com.winitech.common.application.common;

import com.winitech.common.domain.common.CommonEncService;
import com.winitech.common.domain.common.CommonMenuActionCommand;
import com.winitech.common.domain.common.CommonMenuActionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequestWrapper;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonEncFacade {
    private final CommonEncService commonEncService;

    public byte[] getBody(String pubkey, String iv, byte[] body) {
        return commonEncService.getBody(pubkey, iv, body);
    }

    public String getPublicKey(){
        return commonEncService.getPublicKey();
    }
}
