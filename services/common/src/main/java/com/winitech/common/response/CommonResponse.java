package com.winitech.common.response;


import com.winitech.common.interceptor.CommonHttpRequestInterceptor;
import com.winitech.common.library.commonType.WiniPageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;

import java.util.List;

/**
 * 공통응답 클래스
 * <pre>
 * com.winitech.common.response
 * └ CommonResponse.java
 * </pre>
 * 
 * <pre><code>
 * // 성공 응답
 * List&lt;MenuInfo> menuInfoList = menuFacade.getAllMenu();
 * List&lt;MenuDto.MenuResponse> response = menuInfoList.stream().map(MenuDto.MenuResponse::new).collect(Collectors.toList());
 * return CommonResponse.success(response);
 * 
 * // 성공 응답 + 페이징
 * return CommonRepsonse.success(response, CommonResponseMetadata.ofPaging(page, totalRecords, pageSize));
 * return CommonRepsonse.success(response).addPagingMetadata(page, totalRecords, pageSize);
 * 
 * // 성공 응답 + 기타 메타데이터
 * return CommonRepsonse.success(response)
 *      .addMetaData("key", value)
 *      .addMetaData("key2", value2);
 * 
 * // 실패 응답
 * return CommonResponse.fail(ErrorCode.COMMON_SYSTEM_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value());
 * </code></pre>
 * @author : coding (클라우드팀)
 * @since : 2024/11/12
 **/
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {
    private Result result;
    private T data;
    private CommonResponseMetadata metadata;
    private Object message;
    private Number errorCode;
    private String errorCodeName;
    
    private static final CommonResponseMetadata DEFAULT_METADATA = CommonResponseMetadata.create();

    /**
     * 성공 응답
     * 
     * <pre><code>
     *     return CommonResponse.success(response);
     *     return CommonResponse.success(response, "성공");
     *     return CommonResponse.success(response, "성공", CommonResponseMetadata.ofPaging(page, totalRecords, pageSize));
     * </code></pre>
     * @param data 응답 데이터
     * @param message 응답 메시지
     * @param metadata 응답 메타데이터
     * @return CommonResponse
     */
    public static <T> CommonResponse<T> success(T data, String message, CommonResponseMetadata metadata) {
        if (metadata == null) {
            metadata = DEFAULT_METADATA;
        }
        
        return (CommonResponse<T>) CommonResponse.builder()
                .result(Result.SUCCESS)
                .data(data)
                .metadata(metadata)
                .message(message)
                .build();
    }

    /**
     * 성공 응답
     *
     * <pre><code>
     *     return CommonResponse.success(response, CommonResponseMetadata.ofPaging(page, totalRecords, pageSize));
     * </code></pre>
     * @param data 응답 데이터
     * @param metadata 응답 메타데이터
     * @return CommonResponse
     */
    public static <T> CommonResponse<T> success(T data, CommonResponseMetadata metadata) {
        return success(data, null, metadata);
    }

    /**
     * 성공 응답
     *
     * <pre><code>
     *     return CommonResponse.success(response, "성공");
     * </code></pre>
     * @param data 응답 데이터
     * @param message 응답 메시지
     * @return CommonResponse
     */
    public static <T> CommonResponse<T> success(T data, String message) {
        return success(data, message, null);
    }

    /**
     * 성공 응답
     *
     * <pre><code>
     *     return CommonResponse.success(response);
     * </code></pre>
     * @param data 응답 데이터
     * @return CommonResponse
     */
    public static <T> CommonResponse<T> success(T data) {
        return success(data, null, null);
    }

    /**
     * 성공 응답
     *
     * <pre><code>
     *     return CommonResponse.success(response);
     * </code></pre>
     * @param data 응답 데이터
     * @return CommonResponse
     */
    public static <T> CommonResponse<T> success(T data, WiniPageInfo pageInfo) {
        return success(data, null, CommonResponseMetadata.ofPaging(pageInfo.getPage(), pageInfo.getTotalRecords(), pageInfo.getPageSize()));
    }

    /**
     * 실패 응답
     * 
     * <pre><code>
     *     return CommonResponse.fail("실패", "COMMON_SYSTEM_ERROR", HttpStatus.INTERNAL_SERVER_ERROR.value());
     * </code></pre>
     * @param message 응답 메시지
     * @param errorCodeName 에러코드명
     * @param errorCode 에러코드 (현재 HTTP 에러코드를 리턴하나 변경 예정)
     * @return
     */
    public static CommonResponse fail(Object message, String errorCodeName, Number errorCode) {
        CommonResponseMetadata metadata;

        String requestId = MDC.get(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY);
        if (requestId == null || requestId.isEmpty()) {
            metadata = DEFAULT_METADATA;
        } else {
            metadata = CommonResponseMetadata.create();
            metadata.put("requestId", requestId);
        }
        
        return CommonResponse.builder()
                .result(Result.FAILURE)
                .message(message)
                .metadata(metadata)
                .errorCodeName(errorCodeName)
                .errorCode(errorCode)
                .build();
    }

    /**
     * 실패 응답
     * 
     * <pre><code>
     *     return CommonResponse.fail(ErrorCode.COMMON_SYSTEM_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value());
     * </code></pre>
     * @param errorCodeName
     * @param errorCode
     * @return
     */
    public static CommonResponse fail(ErrorCode errorCodeName, Number errorCode) {
        return fail(errorCodeName.getErrorMsg(), errorCodeName.name(), errorCode);
    }

    /**
     * 응답 메타데이터 추가
     * 
     * <pre><code>
     *     return CommonResponse.success(response)
     *          .addMetaData("key", value)
     *          .addMetaData("key2", value2);
     * </code></pre>
     * @param key 메타데이터 키
     * @param value 메타데이터 값
     * @return
     */
    public CommonResponse<T> addMetaData(String key, Object value) {
        if (metadata == null || metadata == DEFAULT_METADATA) {
            metadata = CommonResponseMetadata.create();
        }
        
        metadata.put(key, value);
        
        return this;
    }

    /**
     * 페이징 메타데이터 추가
     * @param page 현재 페이지
     * @param totalRecords 전체 레코드 수
     * @param pageSize 페이지당 레코드 수
     * @return
     */
    public CommonResponse<T> addPagingMetadata(int page, int totalRecords, int pageSize) {
        addMetaData("page", page);
        addMetaData("totalRecords", totalRecords);
        addMetaData("pageSize", pageSize);

        return this;
    }

    public enum Result {
        SUCCESS, FAILURE
    }
}