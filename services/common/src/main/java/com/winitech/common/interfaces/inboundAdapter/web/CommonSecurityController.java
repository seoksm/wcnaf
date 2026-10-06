package com.winitech.common.interfaces.inboundAdapter.web;

import com.winitech.common.application.common.CommonEncFacade;
import com.winitech.common.response.CommonResponse;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.security.*;


/**
 * <pre>
 * com.winitech.common.interfaces.inboundAdapter.web
 * └ CommonSecurityController.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-30 16:01
 **/
@Api(tags = "Common Security")
@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/{serviceName}/commonSecurity")
public class CommonSecurityController {
    private final CommonEncFacade commonEncFacade;

    @GetMapping("/generateKeyPair")
    public CommonResponse<CommonSecurityDto.GenerateKeyPairResponse> generateKeyPair() {
        return CommonResponse.success(CommonSecurityDto.GenerateKeyPairResponse.builder().publicKey(commonEncFacade.getPublicKey()).build());
    }
}