package com.winitech.apiGateway.common.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.winitech.common.exception.BaseException;
import com.winitech.common.exception.UnauthenticatedException;
import com.winitech.common.exception.UnauthorizedException;
import com.winitech.common.interceptor.CommonHttpRequestInterceptor;
import com.winitech.common.library.WiniCom;
import com.winitech.common.response.CommonResponse;
import com.winitech.common.response.Error;
import com.winitech.common.response.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * com.winitech.apiGateway.common.config
 * └ CommonErrorWebExceptionHandler.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-15 09:33
 **/
@Slf4j
public class CommonErrorWebExceptionHandler  implements ErrorWebExceptionHandler {
	private final ApplicationContext applicationContext;
	private final ServerCodecConfigurer serverCodecConfigurer;

	@Getter @Setter
	private boolean shouldShowStackTrace = false;
	
	@Getter @Setter
	private boolean shouldShowFullStackTrace = false;

	private static final List<ErrorCode> SPECIFIC_ALERT_TARGET_ERROR_CODE_LIST = new ArrayList<>();

	private final ObjectMapper objectMapper;

	public CommonErrorWebExceptionHandler(ApplicationContext applicationContext, ServerCodecConfigurer serverCodecConfigurer) {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);

		this.applicationContext = applicationContext;
		this.serverCodecConfigurer = serverCodecConfigurer;
	}
	
	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable e) {
		if (e instanceof MethodArgumentNotValidException) {
			return onMethodArgumentNotValidException(exchange, (MethodArgumentNotValidException) e);
		} else if (e instanceof UnauthorizedException) {
			return onUnauthorizedException(exchange, (UnauthorizedException) e);
		} else if (e instanceof UnauthenticatedException) {
			return onUnauthenticatedException(exchange, (UnauthenticatedException) e);
		} else if (e instanceof BaseException) {
			return onBaseException(exchange, (BaseException) e);
		} else {
			return onException(exchange, e);
		}
	}

	/**
	 * http status: 404 AND result: FAILURE
	 * request parameter 에러
	 *
	 * @param e
	 * @return
	 */
	private Mono<Void> onMethodArgumentNotValidException(ServerWebExchange exchange, MethodArgumentNotValidException e) {
		String eventId = MDC.get(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY);
		log.warn("[BaseException] eventId = {}, errorMsg = {}", eventId, NestedExceptionUtils.getMostSpecificCause(e).getMessage());

		if (shouldShowStackTrace) {
			printStackTrace("[BaseException]", e);
		}

		List<com.winitech.common.response.Error> errorList = new ArrayList<>();
		BindingResult bindingResult = e.getBindingResult();
		bindingResult.getAllErrors().forEach(error -> {
			FieldError field = (FieldError) error;
			String fieldName = field.getField();
			String message = field.getDefaultMessage();
			String value = field.getRejectedValue() == null ? "" : field.getRejectedValue().toString();

			com.winitech.common.response.Error errorMessage = new Error();
			errorMessage.setField(fieldName);
			errorMessage.setMessage(message);
			errorMessage.setInvalidValue(value);
			errorList.add(errorMessage);
		});
		FieldError fe = bindingResult.getFieldError();
		if (fe != null) {
			CommonResponse response = CommonResponse.fail(errorList, ErrorCode.COMMON_INVALID_PARAMETER.name(), HttpStatus.BAD_REQUEST.value());
			return sendErrorResponse(exchange, e, HttpStatus.NOT_FOUND, response);
		} else {
			CommonResponse response = CommonResponse.fail(ErrorCode.COMMON_INVALID_PARAMETER.getErrorMsg(), ErrorCode.COMMON_INVALID_PARAMETER.name(), HttpStatus.BAD_REQUEST.value());
			return sendErrorResponse(exchange, e, HttpStatus.NOT_FOUND, response);
		}
	}

	/**
	 * http status: 404 AND result: FAILURE
	 * 권한 없음
	 *
	 * @param e
	 * @return
	 */
	private Mono<Void> onUnauthorizedException(ServerWebExchange exchange, UnauthorizedException e) {
		String eventId = MDC.get(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY);
		log.warn("[UnauthorizedException] eventId = {}, cause = {}, errorMsg = {}", eventId, NestedExceptionUtils.getMostSpecificCause(e), NestedExceptionUtils.getMostSpecificCause(e).getMessage());

		if (shouldShowStackTrace) {
			printStackTrace("[UnauthorizedException]", e);
		}

		CommonResponse response = CommonResponse.fail(e.getMessage(), e.getErrorCode().name(), HttpStatus.FORBIDDEN.value());
		return sendErrorResponse(exchange, e, HttpStatus.NOT_FOUND, response);
	}

	/**
	 * http status: 404 AND result: FAILURE
	 * 인증실패
	 *
	 * @param e
	 * @return
	 */
	private Mono<Void> onUnauthenticatedException(ServerWebExchange exchange, UnauthenticatedException e) {
		String eventId = MDC.get(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY);
		log.warn("[UnauthenticatedException] eventId = {}, cause = {}, errorMsg = {}", eventId, NestedExceptionUtils.getMostSpecificCause(e), NestedExceptionUtils.getMostSpecificCause(e).getMessage());

		if (shouldShowStackTrace) {
			printStackTrace("[UnauthenticatedException]", e);
		}

		CommonResponse response = CommonResponse.fail(e.getMessage(), e.getErrorCode().name(), HttpStatus.UNAUTHORIZED.value());
		return sendErrorResponse(exchange, e, HttpStatus.NOT_FOUND, response);
	}

	/**
	 * http status: 404 AND result: FAILURE
	 * 시스템은 이슈 없고, 비즈니스 로직 처리에서 에러가 발생함
	 *
	 * @param e
	 * @return
	 */
	private Mono<Void> onBaseException(ServerWebExchange exchange, BaseException e) {
		String eventId = MDC.get(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY);
		if (SPECIFIC_ALERT_TARGET_ERROR_CODE_LIST.contains(e.getErrorCode())) {
			log.error("[BaseException] eventId = {}, cause = {}, errorMsg = {}", eventId, NestedExceptionUtils.getMostSpecificCause(e), NestedExceptionUtils.getMostSpecificCause(e).getMessage());
		} else {
			log.warn("[BaseException] eventId = {}, cause = {}, errorMsg = {}", eventId, NestedExceptionUtils.getMostSpecificCause(e), NestedExceptionUtils.getMostSpecificCause(e).getMessage());
		}

		if (shouldShowStackTrace) {
			printStackTrace("[BaseException]", e);
		}

		CommonResponse response = CommonResponse.fail(e.getMessage(), e.getErrorCode().name(), HttpStatus.OK.value());
		return sendErrorResponse(exchange, e, HttpStatus.NOT_FOUND, response);
	}

	/**
	 * http status: 404 AND result: FAILURE
	 * 시스템 예외 상황. 집중 모니터링 대상
	 *
	 * @param exchange
	 * @param e
	 * @return
	 */
	private Mono<Void> onException(ServerWebExchange exchange, Throwable e) {
		String eventId = MDC.get(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY);
		log.error("eventId = {} ", eventId, e);

		if (shouldShowStackTrace) {
			printStackTrace("[Exception]", e);
		}

//		if (e instanceof HttpMediaTypeNotSupportedException) {
//			CommonResponse response = CommonResponse.fail(ErrorCode.COMMON_RESTAPI_ENC_DISABLED, HttpStatus.NOT_FOUND.value());
//			return sendErrorResponse(exchange, e, HttpStatus.NOT_FOUND, response);
//		}

		CommonResponse response = CommonResponse.fail(ErrorCode.COMMON_SYSTEM_ERROR, HttpStatus.NOT_FOUND.value());
		return sendErrorResponse(exchange, e, HttpStatus.NOT_FOUND, response);
	}

	private Mono<Void> sendErrorResponse(ServerWebExchange exchange, Throwable e, HttpStatus httpStatus, CommonResponse commonResponse) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(httpStatus);
		response.getHeaders().add("Content-Type", "application/json");

		try {
			byte[] bytes = objectMapper.writeValueAsBytes(commonResponse);
			DataBuffer buffer = response.bufferFactory().wrap(bytes);
			return response.writeWith(Mono.just(buffer));
		} catch (JsonProcessingException ex) {
			return Mono.error(ex);
		}
	}

	protected void printStackTrace(String prefix, Throwable e) {
		StackTraceElement[] stackTraces = e.getStackTrace();

		if (stackTraces == null || stackTraces.length == 0) {
			log.error("No stack trace available");
			return;
		}

		log.error("{} Stacktraces:", prefix);

		int skippedLine = 0;

		if (shouldShowFullStackTrace) {
			for (int i = 0; i < stackTraces.length; i++) {
				log.error("{} [{}]\tat {}", prefix, i, stackTraces[i].toString());
			}
		} else {
			for (int i = 0; i < stackTraces.length; i++) {
				boolean isOurPackages = stackTraces[i].getClassName().startsWith("com.winitech.");
				String line = stackTraces[i].toString();

				if (isOurPackages) {
					if (stackTraces[i].getClassName().contains("CGLIB$$")) {
						skippedLine++;
						continue;
					}
				} else {
					skippedLine++;
					continue;
				}

				if (skippedLine > 0) {
					log.error("{} [{}]\t   ... {} items skipped ...", prefix, i - 1, skippedLine);
				}

				skippedLine = 0;
				log.error("{} [{}]\tat {}", prefix, i, line);
			}
		}
	}
}
