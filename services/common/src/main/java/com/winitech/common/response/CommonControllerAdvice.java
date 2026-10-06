package com.winitech.common.response;

import com.winitech.common.exception.BaseException;
import com.winitech.common.exception.UnauthenticatedException;
import com.winitech.common.exception.UnauthorizedException;
import com.winitech.common.interceptor.CommonHttpRequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Slf4j
@ControllerAdvice
public class CommonControllerAdvice {
    @Value("${winitech.log.show-stacktrace:true}")
    private boolean shouldShowStackTrace;

    @Value("${winitech.log.show-full-stacktrace:false}")
    private boolean shouldShowFullStackTrace;

    private static final List<ErrorCode> SPECIFIC_ALERT_TARGET_ERROR_CODE_LIST = new ArrayList<>();

    /**
     * http status: 404 AND result: FAILURE
     * 시스템 예외 상황. 집중 모니터링 대상
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)   // 401, 404, 5XX 오류코드를 404로 통합 (HttpStatus.INTERNAL_SERVER_ERROR대신 HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = Exception.class)
    public CommonResponse onException(Exception e) {
        String eventId = MDC.get(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY);
        log.error("eventId = {} ", eventId, e);
        
        if (shouldShowStackTrace) {
            printStackTrace("[Exception]", e);
        }

        if (e instanceof HttpMediaTypeNotSupportedException) {
            return CommonResponse.fail(ErrorCode.COMMON_RESTAPI_ENC_DISABLED, HttpStatus.NOT_FOUND.value());
        }
        
        return CommonResponse.fail(ErrorCode.COMMON_SYSTEM_ERROR, HttpStatus.NOT_FOUND.value());
    }

    /**
     * http status: 404 AND result: FAILURE
     * 시스템은 이슈 없고, 비즈니스 로직 처리에서 에러가 발생함
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = BaseException.class)
    public CommonResponse onBaseException(BaseException e) {
        String eventId = MDC.get(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY);
        if (SPECIFIC_ALERT_TARGET_ERROR_CODE_LIST.contains(e.getErrorCode())) {
            log.error("[BaseException] eventId = {}, cause = {}, errorMsg = {}", eventId, NestedExceptionUtils.getMostSpecificCause(e), NestedExceptionUtils.getMostSpecificCause(e).getMessage());
        } else {
            log.warn("[BaseException] eventId = {}, cause = {}, errorMsg = {}", eventId, NestedExceptionUtils.getMostSpecificCause(e), NestedExceptionUtils.getMostSpecificCause(e).getMessage());
        }

        if (shouldShowStackTrace) {
            printStackTrace("[BaseException]", e);
        }

        return CommonResponse.fail(e.getMessage(), e.getErrorCode().name(),  HttpStatus.OK.value());
    }

    /**
     * http status: 404 AND result: FAILURE
     * 인증실패
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = UnauthenticatedException.class)
    public CommonResponse onUnauthenticatedException(UnauthenticatedException e) {
        String eventId = MDC.get(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY);
        log.warn("[UnauthenticatedException] eventId = {}, cause = {}, errorMsg = {}", eventId, NestedExceptionUtils.getMostSpecificCause(e), NestedExceptionUtils.getMostSpecificCause(e).getMessage());

        if (shouldShowStackTrace) {
            printStackTrace("[UnauthenticatedException]", e);
        }

        return CommonResponse.fail(e.getMessage(), e.getErrorCode().name(),  HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * http status: 404 AND result: FAILURE
     * 권한 없음
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = UnauthorizedException.class)
    public CommonResponse onUnauthorizedException(UnauthorizedException e) {
        String eventId = MDC.get(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY);
        log.warn("[UnauthorizedException] eventId = {}, cause = {}, errorMsg = {}", eventId, NestedExceptionUtils.getMostSpecificCause(e), NestedExceptionUtils.getMostSpecificCause(e).getMessage());

        if (shouldShowStackTrace) {
            printStackTrace("[UnauthorizedException]", e);
        }

        return CommonResponse.fail(e.getMessage(), e.getErrorCode().name(),  HttpStatus.FORBIDDEN.value());
    }

    /**
     * 예상치 않은 Exception 중에서 모니터링 skip 이 가능한 Exception 을 처리할 때
     * ex) ClientAbortException
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)  // 에러이지만 무시할 수 있는 에러로 200 OK로 처리
    @ExceptionHandler(value = {ClientAbortException.class})
    public CommonResponse skipException(Exception e) {
        String eventId = MDC.get(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY);
        log.warn("[skipException] eventId = {}, cause = {}, errorMsg = {}", eventId, NestedExceptionUtils.getMostSpecificCause(e), NestedExceptionUtils.getMostSpecificCause(e).getMessage());
        return CommonResponse.fail(ErrorCode.COMMON_SYSTEM_ERROR,  HttpStatus.OK.value());
    }

    /**
     * http status: 404 AND result: FAILURE
     * request parameter 에러
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND) // 401, 404, 5XX 오류코드를 404로 통합 (HttpStatus.INTERNAL_SERVER_ERROR대신 HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public CommonResponse methodArgumentNotValidException(MethodArgumentNotValidException e) {
        String eventId = MDC.get(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY);
        log.warn("[BaseException] eventId = {}, errorMsg = {}", eventId, NestedExceptionUtils.getMostSpecificCause(e).getMessage());

        if (shouldShowStackTrace) {
            printStackTrace("[BaseException]", e);
        }

        List<Error> errorList = new ArrayList<>();
        BindingResult bindingResult = e.getBindingResult();
        bindingResult.getAllErrors().forEach(error -> {
            FieldError field = (FieldError) error;
            String fieldName = field.getField();
            String message = field.getDefaultMessage();
            String value = field.getRejectedValue() == null ? "" : field.getRejectedValue().toString();

            Error errorMessage = new Error();
            errorMessage.setField(fieldName);
            errorMessage.setMessage(message);
            errorMessage.setInvalidValue(value);
            errorList.add(errorMessage);
        });
        FieldError fe = bindingResult.getFieldError();
        if (fe != null) {
            return CommonResponse.fail(errorList, ErrorCode.COMMON_INVALID_PARAMETER.name(), HttpStatus.BAD_REQUEST.value());
        } else {
            return CommonResponse.fail(ErrorCode.COMMON_INVALID_PARAMETER.getErrorMsg(), ErrorCode.COMMON_INVALID_PARAMETER.name(), HttpStatus.BAD_REQUEST.value());
        }
    }
    
    /**
     * http status: 404 AND result: FAILURE
     * controller request parameter 에러
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND) // 401, 404, 5XX 오류코드를 404로 통합 (HttpStatus.INTERNAL_SERVER_ERROR대신 HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = {ConstraintViolationException.class})
    public CommonResponse constraintViolationException(ConstraintViolationException e) {
        String eventId = MDC.get(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY);
        log.warn("[BaseException] eventId = {}, errorMsg = {}", eventId, NestedExceptionUtils.getMostSpecificCause(e).getMessage());

        if (shouldShowStackTrace) {
            printStackTrace("[BaseException]", e);
        }

        List<Error> errorList = new ArrayList<>();

        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            Path propertyPath = violation.getPropertyPath();
            String fieldName = "";
            for (Path.Node node : propertyPath) {
                if (node.getName() != null && !node.getName().isEmpty()) {
                    fieldName = node.getName();
                }
            }
            String message = violation.getMessage();
            String value = violation.getInvalidValue() == null ? "(null)" : violation.getInvalidValue().toString();

            Error errorMessage = new Error();
            errorMessage.setField(fieldName);
            errorMessage.setMessage(message);
            errorMessage.setInvalidValue(value);
            errorList.add(errorMessage);
        }

        errorList.sort(Comparator.comparing(Error::getField).thenComparing(Error::getMessage));
        
        return CommonResponse.fail(errorList, ErrorCode.COMMON_INVALID_PARAMETER.name(), HttpStatus.BAD_REQUEST.value());
    }
    
    protected void printStackTrace(String prefix, Exception e) {
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