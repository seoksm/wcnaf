package com.winitech.common.library;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.winitech.common.application.commonJob.CommonJobFacade;
import com.winitech.common.exception.EntityNotFoundException;
import com.winitech.common.exception.IllegalStatusException;
import com.winitech.common.exception.InvalidParamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 백엔드 프레임워크에서 사용하는 공통함수 모음 클래스입니다.
 * <pre>
 * com.winitech.common.library
 * └ WiniCom.java
 * </pre>
 * @author : coding (클라우드팀)
 
 * @since : 2024/11/06
 **/
@Component
public class WiniCom implements ApplicationContextAware {
	private static final SecureRandom RANDOM = new SecureRandom();
	private static final Logger log = LoggerFactory.getLogger(WiniCom.class);
	private static ObjectMapper jsonObjectMapper = null;
	private static CommonJobFacade commonJobFacade = null;
	private static ApplicationContext applicationContext = null;
	
	public WiniCom() {
		jsonObjectMapper = new ObjectMapper();
		jsonObjectMapper.registerModule(new JavaTimeModule());
		jsonObjectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}

	/**
	 * UUIDv7의 카운터
	 * thread-safe 하지 않고 atomic 하지는 않지만 보조 적인 수단이기 때문에
	 * 사용할 수 있음
	 */
	private static byte uuidV7Counter = -10;

	/**
	 * UUIDv7을 생성합니다. <br/>
	 * UUIDv7은 기존에 많이 사용하는 UUIDv4와는 다르게 시간 순으로 정렬 할 수 있는 UUID로,<br/>
	 * Database의 Primary Key로 사용하기에 적합합니다.<br/>
	 * <br/>
	 * <pre><code>
	 *     // 사용예제
	 *     // UUIDv7 생성
	 *     UUID uuid = WiniCom.getUUIDv7();
	 *     System.out.println(uuid.toString());
	 * </code></pre>
	 * <br/>
	 * 다음과 같은 구조로 작성되어있습니다.
	 * <pre>
	 *  0                   1                   2                   3
	 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |                           unix_ts_ms                          |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |          unix_ts_ms           |  ver  |       rand_a          |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |var|                        rand_b                             |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |                            rand_b                             |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * </pre>
	 * <a href="https://www.ietf.org/archive/id/draft-peabody-dispatch-new-uuid-format-04.html#name-creating-a-uuidv7-value">출처</a>
	 * @return UUIDv7
	 */
	public static UUID getUUIDv7() {
		// 현재 시간을 밀리초로 가져옴
		long timestamp = Instant.now().toEpochMilli();

		// 랜덤 비트 생성
		byte[] randomBytes = new byte[9];
		RANDOM.nextBytes(randomBytes);

		// timestamp를 상위 48비트에 배치
		long msb = (timestamp << 16);

		msb |= ((randomBytes[0] & 0xFFL) << 8);

		byte counter = uuidV7Counter++;
		msb |= counter & 0xffff; 
		
		msb &= ~(0xF000L); // version bits 클리어
		msb |= 0x7000L;    // version 7 설정

		// 하위 64비트를 랜덤값으로 설정
		long lsb = 0;
		for (int i = 1; i < 9; i++) {
			lsb = (lsb << 8) | (randomBytes[i] & 0xFFL);
		}
		lsb &= ~(0xC000000000000000L); // variant bits 클리어
		lsb |= 0x8000000000000000L;    // RFC-4122 variant 설정

		return new UUID(msb, lsb);
	}

	/**
	 * UUIDv4를 생성합니다. <br/>
	 * <br/>
	 * <pre><code>
	 *     // 사용예제
	 *     // UUIDv4 생성
	 *     UUID uuid = WiniCom.getUUIDv4();
	 *     System.out.println(uuid.toString());
	 * </code></pre>
	 * @return
	 */
	public static UUID getUUIDv4() {
		return UUID.randomUUID();
	}

	/**
	 * Stream을 복사합니다. 4096 bytes 단위로 복사합니다.
	 * <pre><code>
	 *     // 사용예제
	 *     // InputStream과 OutputStream을 생성합니다.
	 *     InputStream is = new FileInputStream("input.txt");
	 *     OutputStream os = new FileOutputStream("output.txt");
	 *     
	 *     // Stream을 복사합니다.
	 *     WiniCom.copyStream(is, os);
	 * </code></pre>
	 * @param is InputStream 입력 스트림
	 * @param os OutputStream 출력 스트림
	 * @return 복사한 바이트 수
	 * @throws IOException
	 */
	public static long copyStream(InputStream is, OutputStream os) throws IOException {
		byte[] buffer = new byte[4096];
		
		long total = 0;
		int nRead;
		while ((nRead = is.read(buffer)) != -1) {
			os.write(buffer, 0, nRead);
			total += nRead;
		}
		return total;		
	}

	private static String[] ipHeaderList;

	/**
	 * null인 경우 defaultValue를 리턴합니다.
	 * <pre><code>
	 *     // 사용예제
	 *     // null인 경우 defaultValue를 리턴합니다.
	 *     String value = null;
	 *     String result = WiniCom.ifNull(value, "default");
	 *     System.out.println(result); // default
	 * </code></pre>
	 * @param value 입력 값
	 * @param defaultValue 입력 값이 null인 경우 리턴될  기본 값
	 * @return 입력 값이 null인 경우 defaultValue, 그렇지 않은 경우 value를 리턴
	 * @param <T>
	 */
	public static <T> T ifNull(T value, T defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		
		return value;
	}

	/**
	 * null인 경우 defaultValue를 리턴합니다. (WiniCom.ifNull과 동일합니다.)
	 * <pre><code>
	 *     // 사용예제
	 *     // null인 경우 defaultValue를 리턴합니다.
	 *     String value = null;
	 *     String result = WiniCom.nvl(value, "default");
	 *     System.out.println(result); // default
	 * </code></pre>
	 * @param value 입력 값
	 * @param defaultValue 입력 값이 null인 경우 리턴될  기본 값
	 * @return 입력 값이 null인 경우 defaultValue, 그렇지 않은 경우 value를 리턴
	 * @param <T>
	 */
	public static <T> T nvl(T value, T defaultValue) {
		return ifNull(value, defaultValue);
	}

	/**
	 * JPA에서 페이징 할 때 사용하는 PageRequest를 생성합니다.
	 * 
	 * <pre><code>
	 * // 사용예제
	 *{@literal @}Override
	 * public Page&lt;CommonJobInfo> getCommonJobPage(UUID commonJobId, Integer page, Integer pageSize) {
	 *     return commonJobQueryRepository.findAllPage(commonJobId, WiniCom.getPageRequest(page, pageSize));
	 * }
	 * </code></pre>	
	 * @param page 페이지 번호
	 * @param pageSize 페이지 사이즈 (한 페이지당 항목 수)
	 * @return PageRequest
	 */
	public static PageRequest getPageRequest(Integer page, Integer pageSize) {
		if (page == null && pageSize == null) {
			page = 0;
			pageSize = Integer.MAX_VALUE;
		}
		
		return PageRequest.of(page == null ? 0 : page, pageSize == null ? 10 : pageSize);
	}

	/**
	 * JPA에서 페이징 할 때 사용하는 PageRequest를 생성합니다. Sort 클래스를 사용하여 정렬을 설정할 수 있습니다.
	 * 
	 * <pre><code>
	 * // 사용예제
	 * // 페이징을 위한 Sort를 설정합니다.
	 * Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
	 * 
	 * // PageRequest를 생성합니다.
	 * PageRequest pageRequest = WiniCom.getPageRequest(page, pageSize, sort);
	 * 
	 * return commonJobQueryRepository.findAllPage(commonJobId, pageRequest);
	 * </code></pre>
	 * @param page 페이지 번호
	 * @param pageSize 페이지 사이즈 (한 페이지당 항목 수)
	 * @param sort 정렬을 위한 Sort 객체
	 * @return
	 */
	public static PageRequest getPageRequest(Integer page, Integer pageSize, Sort sort) {
		if (page == null && pageSize == null) {
			page = 0;
			pageSize = Integer.MAX_VALUE;
		}

		return PageRequest.of(page == null ? 0 : page, pageSize == null ? 10 : pageSize, sort);
	}

	/**
	 * JPA에서 페이징 할 때 사용하는 PageRequest를 생성합니다. Sort.Direction과 커럼명을 사용하여 정렬을 설정할 수 있습니다.
	 *
	 * <pre><code>
	 * // 사용예제
	 * // PageRequest를 생성합니다.
	 * PageRequest pageRequest = WiniCom.getPageRequest(page, pageSize, Sort.Direction.DESC, "createdAt", "name");
	 *
	 * return commonJobQueryRepository.findAllPage(commonJobId, pageRequest);
	 * </code></pre>
	 * @param page 페이지 번호
	 * @param pageSize 페이지 사이즈 (한 페이지당 항목 수)
	 * @param direction 정렬 방향 (Sort.Direction.ASC : 오름차순, Sort.Direction.DESC : 내림차순)
	 * @param properties 정렬할 커럼명 (1개 이상 지정 가능) 
	 * @return PageRequest
	 */
	public static PageRequest getPageRequest(Integer page, Integer pageSize, Sort.Direction direction, String... properties) {
		if (page == null && pageSize == null) {
			page = 0;
			pageSize = Integer.MAX_VALUE;
		}

		return PageRequest.of(page == null ? 0 : page, pageSize == null ? 10 : pageSize, direction, properties);
	}

	/**
	 * JPA에서 최대 크기의 PageRequest를 생성합니다.

	 * <pre><code>
	 * // 사용예제
	 *{@literal @}Override
	 * public Page&lt;CommonJobInfo> getCommonJobPage(UUID commonJobId) {
	 *     return commonJobQueryRepository.findAllPage(commonJobId, WiniCom.getPageRequest());
	 * }
	 * </code></pre>	
	 * @return PageRequest
	 */
	public static PageRequest getPageRequest() {
		return PageRequest.of(0, Integer.MAX_VALUE);
	}

	/**
	 * JPA에서 정렬이 포함된 최대 크기의 PageRequest를 생성합니다. Sort 클래스를 사용하여 정렬을 설정할 수 있습니다.
	 *
	 * <pre><code>
	 * // 사용예제
	 * // 페이징을 위한 Sort를 설정합니다.
	 * Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
	 *
	 * // PageRequest를 생성합니다.
	 * PageRequest pageRequest = WiniCom.getPageRequest(sort);
	 *
	 * return commonJobQueryRepository.findAllPage(commonJobId, pageRequest);
	 * </code></pre>
	 * @param sort 정렬을 위한 Sort 객체
	 * @return PageRequest
	 */
	public static PageRequest getPageRequest(Sort sort) {
		return PageRequest.of(0, Integer.MAX_VALUE, sort);
	}

	/**
	 * JPA에서 정렬이 포함된 최대 크기의 PageRequest를 생성합니다. Sort.Direction과 커럼명을 사용하여 정렬을 설정할 수 있습니다.
	 *
	 * <pre><code>
	 * // 사용예제
	 * // PageRequest를 생성합니다.
	 * PageRequest pageRequest = WiniCom.getPageRequest(Sort.Direction.DESC, "createdAt", "name");
	 *
	 * return commonJobQueryRepository.findAllPage(commonJobId, pageRequest);
	 * </code></pre>
	 * @param direction 정렬 방향 (Sort.Direction.ASC : 오름차순, Sort.Direction.DESC : 내림차순)
	 * @param properties 정렬할 커럼명 (1개 이상 지정 가능) 
	 * @return PageRequest
	 */
	public static PageRequest getPageRequest(Sort.Direction direction, String... properties) {
		return PageRequest.of(0, Integer.MAX_VALUE, direction, properties);
	}

	@Value("${winitech.security.forward-ip-headers:X-Forwarded-For,HTTP_X_FORWARDED_FOR}")
	private void setIpHeaderList(String[] ipHeaderList) {
		WiniCom.ipHeaderList = ipHeaderList;		
	}

	/**
	 * HTTP 요청에서 클라이언트 IP를 가져옵니다.
	 * request.getRemoteAddr()을 사용하는 경우 Kubernetes 환경 또는 LoadBalancer나 웹서버와 WAS가 분리된 환경에서<br/> 
	 * 실제 클라이언트 IP를 가져올 수 없는데, 이를 해결하기 위해 X-Forwarded-For 등의 헤더를 사용합니다.<br/>
	 * <br/>
	 * 기본적으로 X-Forwarded-For와 HTTP_X_FORWARDED_FOR를 지원하며<br/> 
	 * application.properties의 winitech.security.forward-ip-headers를 통해 헤더 목록을 설정할 수 있습니다.<br/>
	 * 
	 * <pre><code>
	 *     // 사용예제
	 *     public void downloadFile(HttpServletRequest req, HttpServletResponse res) throws IOException {
	 *         // 클라이언트 IP를 가져옵니다.
	 *         String clientIp = WiniCom.getClientIp(req);
	 *         System.out.println("Client IP: " + clientIp);
	 *     }
	 * </code></pre>
	 * 
	 * @param req HttpServletRequest 요청 객체
	 * @return 클라이언트 IP 주소
	 */
	public static String getClientIp(HttpServletRequest req) {

		String result = "";

		for (String header : ipHeaderList) {
			String ipAddr = req.getHeader(header);

			log.info("===== getClientIp Method in Common Package =====");
			log.info("header: {}, ipAddr: {}", header, ipAddr);
			log.info("================================================");

			if (ipAddr != null && !ipAddr.isEmpty() && !"unknown".equalsIgnoreCase(ipAddr)) {
				// X-Forwarded-For 헤더는 여러 IP를 쉼표로 구분하여 포함할 수 있음
				if (ipAddr.contains(",")) {
					ipAddr = ipAddr.split(",")[0].trim();
					result =  ipAddr;
				}
//				return ipAddr;
			}
		}

		if (!result.isBlank()) {
			return result;
		}

		return req.getRemoteAddr();
	}
	
	/**
	 * 랜덤한 double 값을 생성합니다. SecureRandom에서 생성된 값을 사용합니다.
	 * 
	 * <pre><code>
	 *     // 사용예제
	 *     // 랜덤한 double 값을 생성합니다.
	 *     Double randomValue = WiniCom.random();
	 *     System.out.println(randomValue);
	 * </code></pre>
	 * @return 랜덤한 double 값
	 */
	public static Double random() {
		return RANDOM.nextDouble();
	}

	/**
	 * 랜덤한 double 값을 생성합니다. lower~upper 사이의 값을 생성합니다.
	 * 
	 * <pre><code>
	 *     // 사용예제
	 *     // lower ~ upper 사이의 랜덤한 double 값을 생성합니다.
	 *     Double randomValue = WiniCom.random(1.0, 10.0);
	 *     System.out.println(randomValue);
	 * </code></pre>
	 * @param lower 하한값
	 * @param upper 상한값
	 * @return lower ~ upper 사이의 랜덤한 double 값
	 */
	public static double random(double lower, double upper) {
		//return RANDOM.nextDouble(lower, upper);
		return lower + (RANDOM.nextDouble() * (upper - lower));
	}

	/**
	 * 랜덤한 float 값을 생성합니다. lower~upper 사이의 값을 생성합니다.
	 * 
	 * <pre><code>
	 *     // 사용예제
	 *     // lower ~ upper 사이의 랜덤한 float 값을 생성합니다.
	 *     Float randomValue = WiniCom.random(1.0f, 10.0f);
	 *     System.out.println(randomValue);
	 * </code></pre>
	 * @param lower 하한값
	 * @param upper 상한값
	 * @return lower ~ upper 사이의 랜덤한 float 값
	 */
	public static float random(float lower, float upper) {
		// return RANDOM.nextFloat(lower, upper);	// Java 17 이상
		return lower + (float) (RANDOM.nextDouble() * (upper - lower));
	}

	/**
	 * 랜덤한 long 값을 생성합니다. lower~upper 사이의 값을 생성합니다.
	 * 
	 * <pre><code>
	 *     // 사용예제
	 *     // lower ~ upper 사이의 랜덤한 long 값을 생성합니다.
	 *     Long randomValue = WiniCom.random(1L, 10L);
	 *     System.out.println(randomValue);
	 * </code></pre>
	 * @param lower 하한값
	 * @param upper 상한값 
	 * @return lower ~ upper 사이의 랜덤한 long 값
	 */
	public static long random(long lower, long upper) {
		// return RANDOM.nextLong(lower, upper);	// Java 17 이상
		return lower + (long) (RANDOM.nextDouble() * (upper - lower));
	}

	/**
	 * 랜덤한 int 값을 생성합니다. lower~upper 사이의 값을 생성합니다.
	 * 
	 * <pre><code>
	 *     // 사용예제
	 *     // lower ~ upper 사이의 랜덤한 int 값을 생성합니다.
	 *     Int randomValue = WiniCom.random(1, 10);
	 *     System.out.println(randomValue);
	 * </code></pre>
	 * @param lower 하한값
	 * @param upper 상한값
	 * @return lower ~ upper 사이의 랜덤한 int 값
	 */
	public static int random(int lower, int upper) {
		// return RANDOM.nextInt(lower, upper); 	// Java 17 이상
		return lower + (int) (RANDOM.nextDouble() * (upper - lower));
	}

	/**
	 * 랜덤한 byte 배열을 생성합니다. 
	 * 
	 * <pre><code>
	 *     // 사용예제
	 *     // 랜덤한 byte 배열을 생성합니다.
	 *     byte[] randomBytes = WiniCom.randomByte(16);
	 *     System.out.println(WiniCom.encodeBase64(randomBytes));
	 * </code></pre>
	 * @param byteLength byte 배열의 길이
	 * @return 랜덤한 byte 배열
	 */
	public static byte[] randomByte(int byteLength) {
		byte[] buffer = new byte[byteLength];
		return randomByte(buffer);
	}

	/**
	 * 랜덤한 byte 배열을 생성하여 주어진 buffer에 저장합니다.
	 * 
	 * <pre><code>
	 *     // 사용예제
	 *     // 랜덤한 byte 배열을 생성하여 주어진 buffer에 저장합니다.
	 *     byte[] buffer = new byte[16];
	 *     WiniCom.randomByte(buffer);
	 *     System.out.println(WiniCom.encodeBase64(buffer));
	 * </code></pre>
	 * @param buffer byte 버퍼
	 * @return 랜덤한 byte 배열 (buffer와 동일합니다.)
	 */
	public static byte[] randomByte(byte[] buffer) {
		RANDOM.nextBytes(buffer);
		return buffer;
	}

	/**
	 * 객체를 JSON 문자열로 변환합니다.
	 * 
	 * <pre><code>
	 *     // 사용예제
	 *     // 객체를 JSON 문자열로 변환합니다.
	 *     CommonJobInfo result = commonJobInfoService.getCommonJobInfoById(commonJobId);
	 *     
	 *     String json = WiniCom.toJson(result);
	 *     System.out.println(json);
	 *     // {"id":"a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6","name":"testJob","status":"ACTIVE"}
	 * </code></pre>
	 * @param obj 변환할 임의의 객체
	 * @return JSON 문자열
	 */
	public static String toJson(Object obj)  {
		try {
			return jsonObjectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			log.error("Failed to convert object to JSON", e);
			throw new InvalidParamException("Failed to convert object to JSON");
		}
	}
	
	/**
	 * 객체를 JSON으로 변환하여 OutputStream에 씁니다.
	 * 
	 * <pre><code>
	 *     // 사용예제
	 *     // 객체를 JSON으로 변환하여 OutputStream에 씁니다.
	 *     CommonJobInfo result = commonJobInfoService.getCommonJobInfoById(commonJobId);
	 *     
	 *     WiniCom.toJson(result, outputStream);
	 * </code></pre>
	 * @param obj 변환할 임의의 객체
	 * @param outputStream JSON을 쓸 OutputStream
	 */
	public static void toJson(Object obj, OutputStream outputStream) {
		try {
			jsonObjectMapper.writeValue(outputStream, obj);
		} catch (IOException e) {
			log.error("Failed to convert object to JSON", e);
			throw new InvalidParamException("Failed to convert object to JSON");
		}
	}

	/**
	 * 임의의 객체를 문자열로 변환합니다. null 인 경우 ""을 리턴하여 null 체크를 하지 않아도 됩니다.
	 * 
	 * <pre><code>
	 *     // 사용예제
	 *     // 임의의 객체를 문자열로 변환합니다.
	 *     String str = WiniCom.toString(1234.56);
	 *     System.out.println(str); // "1234.56"
	 *     
	 *     // null인 경우 ""을 리턴합니다.
	 *     String str2 = null;
	 *     System.out.println(WiniCom.toString(str2)); // ""
	 * </code></pre>
	 * @param obj 문자열로 변환할 임의의 객체
	 * @return 문자열
	 */
	public static String toString(Object obj) {
		if (obj == null) {
			return null;
		}
		
		if (obj instanceof String) {
			return (String) obj;
		}
		
		return obj.toString();
	}

	/**
	 * 임의의 객체를 문자열로 변환합니다. null 인 경우 ""을 리턴합니다. (toString과 동일)
	 * 
	 * <pre><code>
	 *     // 사용예제
	 *     // 임의의 객체를 문자열로 변환합니다.
	 *     String str = WiniCom.toEmpty(1234.56);
	 *     System.out.println(str); // "1234.56"
	 * 
	 *     // null인 경우 ""을 리턴합니다.
	 *     String str2 = null;
	 *     System.out.println(WiniCom.toEmpty(str2)); // ""
	 * </code></pre>
	 * @param obj 문자열로 변환할 임의의 객체
	 * @return 문자열
	 */
	public static String toEmpty(Object obj) {
		return toString(obj);
	}

	/**
	 * bytes 배열을 Base64로 인코딩합니다.
	 * 
	 * <pre><code>
	 *     // 사용예제
	 *     // bytes 배열을 Base64로 인코딩합니다.
	 *     byte[] data = "Hello, World!".getBytes(StandardCharsets.UTF_8);
	 *     String base64 = WiniCom.encodeBase64(data);
	 *     System.out.println(base64); // "SGVsbG8sIFdvcmxkIQ=="
	 * </code></pre>
	 * @param data bytes 배열
	 * @return Base64 인코딩된 문자열
	 */
	public static String encodeBase64(byte[] data) {
		return java.util.Base64.getEncoder().encodeToString(data);
	}

	/**
	 * 문자열을 Base64로 인코딩합니다.
	 * 
	 * <pre><code>
	 *     // 사용예제
	 *     // 문자열을 Base64로 인코딩합니다.
	 *     String data = "Hello, World!";
	 *     String base64 = WiniCom.encodeBase64(data);
	 *     System.out.println(base64); // "SGVsbG8sIFdvcmxkIQ=="
	 * </code></pre>
	 * @param data 문자열
	 * @return Base64 인코딩된 문자열
	 */
	public static String encodeBase64(String data) {
		return encodeBase64(data.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Base64로 인코딩된 문자열을 디코딩하여 문자열로 변환합니다.
	 * 결과물이 문자열이 아닌 경우 예외가 발생할 수 있습니다.
	 * 
	 * <pre><code>
	 *     // 사용예제
	 *     // Base64로 인코딩된 문자열을 디코딩하여 문자열로 변환합니다.
	 *     String base64 = "SGVsbG8sIFdvcmxkIQ==";
	 *     String decoded = WiniCom.decodeBase64(base64);
	 *     System.out.println(decoded); // "Hello, World!"
	 * </code></pre>
	 * @param base64 Base64로 인코딩된 문자열
	 * @return 문자열
	 */
	public static String decodeBase64(String base64) {
		return new String(java.util.Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);		
	}

	/**
	 * Base64로 인코딩된 문자열을 디코딩하여 byte 배열로 변환합니다.
	 * 
	 * <pre><code>
	 *     // 사용예제
	 *     // Base64로 인코딩된 문자열을 디코딩하여 byte 배열로 변환합니다.
	 *     String base64 = "SGVsbG8sIFdvcmxkIQ==";
	 *     byte[] decoded = WiniCom.decodeBase64Bytes(base64);
	 *     System.out.println(new String(decoded, StandardCharsets.UTF_8)); // "Hello, World!"
	 * </code></pre>
	 * @param base64 Base64로 인코딩된 문자열
	 * @return byte 배열
	 */
	public static byte[] decodeBase64Bytes(String base64) {
		return java.util.Base64.getDecoder().decode(base64);		
	}

	/**
	 * Stream을 재귀적으로 flatten 합니다.
	 * @param root 루트 노드
	 * @param children 자식 노드
	 * @return Stream&lt;T> 
	 * @param <T> 
	 */
	public static <T> Stream<T> flatMapRecursive(T root, Function<T, Stream<T>> children) {
		return Stream.concat(Stream.of(root), children.apply(root).flatMap(child -> flatMapRecursive(child, children)));
	}

	/**
	 * 예외의 근본 원인을 찾습니다.
	 * @param ex 예외
	 * @return 근본 원인인 예외
	 */
	public static Exception getRootCause(Exception ex) {
		if (ex == null) {
			return null;
		}
		
		Throwable cause = ex.getCause();

		if (cause == null) {
			return ex;
		}

		while (cause != null && cause.getCause() != null) {
			cause = cause.getCause();
		}

		return (Exception) cause;
	}

	/**
	 * 백그라운드 작업을 수동으로 트리거합니다.
	 * @param jobGroupName 작업 그룹 이름
	 * @param jobName 작업 이름
	 */
	public static void triggerJob(String jobGroupName, String jobName) {
		triggerJob(jobGroupName, jobName, null);
	}

	/**
	 * 백그라운드 작업을 수동으로 트리거합니다.
	 * @param jobGroupName 작업 그룹 이름
	 * @param jobName 작업 이름
	 * @param params 작업에 전달할 파라미터
	 */
	public static void triggerJob(String jobGroupName, String jobName, Map<String, Object> params) {
		if (commonJobFacade == null) {
			throw new IllegalStatusException("CommonJobFacade is not initialized.");
		}
		
		try {
			commonJobFacade.triggerCommonJob(jobGroupName, jobName, params);
		} catch (EntityNotFoundException e) {
			log.error("Job not found: {} - {}", jobGroupName, jobName);
			throw new InvalidParamException("Job not found: " + jobGroupName + " - " + jobName);
		}
	}

	/**
	 * 추적에 사용할 RequestId를 생성합니다.
	 * @return
	 */
	public static String generateRequestId() {
		return UUID.randomUUID().toString();
	}
	
//	public static void main(String[] args) {
//		for (int i = 0; i < 400; i++) {
//			System.out.println(WiniCom.getUUIDv7());
//		}
//	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		WiniCom.applicationContext = applicationContext;
		
		if (applicationContext != null) {
			WiniCom.commonJobFacade = applicationContext.getBean(CommonJobFacade.class);
		}
	}
}
