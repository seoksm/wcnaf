package com.winitech.common.interfaces.inboundAdapter.web;

import com.winitech.common.interceptor.CommonHttpRequestInterceptor;
import com.winitech.common.response.CommonResponse;
import com.winitech.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <pre>
 * com.winitech.common.interfaces.inboundAdapter.web
 * └ CommonErrorPageController.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-28 17:09
 **/
@Slf4j
@CrossOrigin
@Controller
@RequiredArgsConstructor
public class CommonErrorPageController implements ErrorController {
	@RequestMapping("/error")
	@ResponseBody
	public Object index(HttpServletRequest request, HttpServletResponse response) {
		String requestId = MDC.get(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY);
		if (requestId == null || requestId.isEmpty()) {
			MDC.put(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY, request.getHeader(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY));
		}

		return CommonResponse.fail(ErrorCode.COMMON_SYSTEM_ERROR, HttpStatus.NOT_FOUND.value());
	}
}
