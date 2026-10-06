package com.winitech.common.library;

import com.winitech.common.exception.InvalidParamException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 파일 업로드 및 다운로드 관련 유틸리티 클래스입니다.
 * <pre>
 * com.winitech.common.library
 * └ WiniFile.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-19 16:41
 **/
@Component
public class WiniFile {
	/**
	 * 파일이 저장될 기본 디렉토리
	 */
	@Getter
	private static String fileBaseDir;

	@Value("${winitech.file.base-dir}")
	private void setFileBaseDir(String fileBaseDir) {
		fileBaseDir = WiniFile.normalizeDir(fileBaseDir);
		if (! fileBaseDir.endsWith("/")) {
			fileBaseDir += "/";
		}
		
		WiniFile.fileBaseDir = fileBaseDir;
	}

	/**
	 * 파일 업로드 허용 확장자 목록
	 */
	@Getter
	private static Set<String> allowedFileExtensionSet;

	@Value("${winitech.file.upload.extensions:bmp,gif,jpg,jpeg,png,xls,xlsx,doc,docx,ppt,pptx,hwp,hwpx,pdf,mp4,zip,7z,rar,alz,egg}")
	private void setAllowedFileExtensionSet(String allowedFileExtension) {
		WiniFile.allowedFileExtensionSet = Set.of(allowedFileExtension.split(","));
	}

	/**
	 * 파일 업로드 허용 최대 크기
	 */
	@Getter
	private static Long allowedFileMaxSize;

	@Value("${winitech.file.upload.max-size:10485760}")
	private void setAllowedFileMaxSize(Long allowedFileMaxSize) {
		WiniFile.allowedFileMaxSize = allowedFileMaxSize;
	}

	/**
	 * 이미지 업로드 허용 확장자 목록
	 */
	@Getter
	private static Set<String> allowedImageExtensionSet;

	@Value("${winitech.file.upload.image.extensions:bmp,gif,jpg,jpeg,png}")
	private void setAllowedImageExtensionSet(String allowedFileExtension) {
		WiniFile.allowedImageExtensionSet = Set.of(allowedFileExtension.split(","));
	}

	/**
	 * 이미지 업로드 허용 최대 크기
	 */
	@Getter
	private static Long allowedImageMaxSize;

	@Value("${winitech.file.upload.image.max-size:10485760}")
	private void setAllowedImageMaxSize(Long allowedImageMaxSize) {
		WiniFile.allowedImageMaxSize = allowedImageMaxSize;
	}

	/**
	 * 멀티 다운로드(zip 방식) 허용 최대 파일 크기
	 */
	@Getter
	private static Long allowedMultiDownloadMaxSize;

	@Value("${winitech.file.download.multidownload.max-size:10485760}")
	private void setAllowedMultiDownloadMaxSize(Long allowedMultiDownloadMaxSize) {
		WiniFile.allowedMultiDownloadMaxSize = allowedMultiDownloadMaxSize;
	}

	/**
	 * 멀티 다운로드(zip 방식) 허용 최대 파일 개수
	 */
	@Getter
	private static Long allowedMultiDownloadMaxCount;

	@Value("${winitech.file.download.multidownload.max-count:10485760}")
	private void setAllowedMultiDownloadMaxCount(Long allowedMultiDownloadMaxCount) {
		WiniFile.allowedMultiDownloadMaxCount = allowedMultiDownloadMaxCount;
	}

	/**
	 * 파일 관련 동작을 수행할 때 허용되는 샌드박스 디렉터리 목록<br/>
	 * WiniFile.filePathBlackList(`파일경로`) 메소드에서 `파일경로`가 샌드박스에 없으면 예외 발생 
	 */
	@Getter
	private static Set<String> allowedFileSandboxSet;
	
	private static Set<String> allowedFileSandboxSetLowerCase;
	
	@Value("${winitech.file.sandboxs}")
	private void setAllowedFileSandboxSet(String allowedFileSandbox) {
		WiniFile.allowedFileSandboxSet = Arrays.stream(allowedFileSandbox.split(","))
				.map(WiniFile::normalizeDir)
				.collect(Collectors.toSet());
		
		// 샌드박스로 시작하는지 체크하기위해 미리 소문자화
		WiniFile.allowedFileSandboxSetLowerCase = allowedFileSandboxSet
				.stream()
				.map(WiniString::lower)
				.collect(Collectors.toSet());
	}

	@Getter
	private static String signedFileIdSecret;
	
	@Value("${winitech.security.file.signed-file-id-secret:Wini-wieru9834y9whf}")
	private void setSignedFileIdSecret(String signedFileIdSecret) {
		this.signedFileIdSecret = signedFileIdSecret;
	}

	@Getter
	private static Long signedFileIdDuration;

	@Value("${winitech.security.file.signed-file-id-duration:86400}")
	private void setSignedFileIdDuration(Long signedFileIdDuration) {
		this.signedFileIdDuration = signedFileIdDuration;
	}	

	/**
	 * 파일의 확장자를 구하기. 확장자가 없을 시 "" 리턴
	 * <pre><code>
	 * // 사용예제
	 * String fileName = "example.txt";
	 * String fileExtension = WiniFile.getFileExtension(fileName);
	 * System.out.println(fileExtension); 
	 * // => txt
	 * </code></pre>
	 * @param fileNameWithExtension 확장자를 포함한 파일 이름
	 * @return .을 제외한 파일의 확장자
	 */
	@NonNull
	public static String getFileExtension(String fileNameWithExtension) {
		if (fileNameWithExtension == null || fileNameWithExtension.isEmpty()) {
			return "";
		}

		String fileExt = StringUtils.getFilenameExtension(fileNameWithExtension);
		
		if (fileExt == null) {
			return "";
		}
		
		return fileExt;
	}

	/**
	 * 파일 경로에서 파일 이름만 구하기
	 * <pre><code>
	 * // 사용예제
	 * String path = "/path/to/example.txt";
	 * String fileName = WiniFile.getFileName(path);
	 * System.out.println(fileName);
	 * // => example.txt
	 * </code></pre>
	 * @param path 파일 이름이 포함된 파일 경로
	 * @return 디렉터리를 제외한 파일 이름 (확장자 포함)
	 */
	public static String getFileName(String path) {
		if (path == null || path.isEmpty()) {
			return "";
		}
		
		return StringUtils.getFilename(path);		
	}

	/**
	 * 파일 이름에서 확장자를 제거한 파일 이름을 구하기. 확장자가 없을 시 "" 리턴
	 * <pre><code>
	 * // 사용예제
	 * String fileName = "example.txt";
	 * String fileNameWithoutExtension = WiniFile.getFileNameWithoutExtension(fileName);
	 * System.out.println(fileNameWithoutExtension);
	 * // => example
	 * </code></pre>
	 * @param fileNameWithExtension 확장자를 포함한 파일 이름
	 * @return 확장자를 제거한 파일 이름
	 */
	@NonNull
	public static String getFileNameWithoutExtension(String fileNameWithExtension) {
		if (fileNameWithExtension == null || fileNameWithExtension.isEmpty()) {
			return "";
		}

		String fileNameWithoutExtension = StringUtils.stripFilenameExtension(StringUtils.getFilename(fileNameWithExtension));
		
		if (fileNameWithoutExtension == null) {
			return "";
		}
		
		return fileNameWithoutExtension;
	}

	/**
	 * 파일 확장자가 허용된 확장자 목록에 포함되어 있는지 체크합니다.<br/>
	 * application.properties의 winitech.file.upload.extensions 를 통해 확장자 목록을 설정할 수 있습니다.
	 * <pre><code>
	 * // 사용예제
	 * String fileName = "example.txt";
	 * String fileExt = WiniFile.getFileExtension(fileName);
	 * boolean isAllowed = WiniFile.isFileExtensionAllowed(fileExt);
	 * System.out.println(isAllowed); 
	 * // => true
	 * </code></pre>
	 * @param fileExt 확장자
	 * @return 허용된 확장자 목록에 포함되어 있으면 true, 아니면 false
	 */
	public static boolean isFileExtensionAllowed(String fileExt) {
		if (fileExt == null) {
			return false;
		}
		
		return allowedFileExtensionSet.contains(fileExt);
	}

	/**
	 * 파일 크기가 허용된 최대 크기보다 작은지 체크합니다.<br/>
	 * application.properties의 winitech.file.upload.max-size 를 통해 최대 크기를 설정할 수 있습니다.
	 * <pre><code>
	 * // 사용예제
	 * long fileSize = 1024 * 1024 * 5; // 5MB
	 * boolean isAllowed = WiniFile.isFileSizeAllowed(fileSize);
	 * System.out.println(isAllowed);
	 * // => true
	 * </code></pre>
	 * @param fileSize 파일 크기 (바이트)
	 * @return 허용된 최대 크기보다 작으면 true, 아니면 false
	 */
	public static boolean isFileSizeAllowed(long fileSize) {
		return allowedFileMaxSize > fileSize;
	}

	/**
	 * 이미지 확장자가 허용된 확장자 목록에 포함되어 있는지 체크합니다.<br/>
	 * application.properties의 winitech.file.upload.image.extensions 를 통해 확장자 목록을 설정할 수 있습니다.
	 * <pre><code>
	 * // 사용예제
	 * String imageFileExt = "jpg";
	 * boolean isAllowed = WiniFile.isImageExtensionAllowed(imageFileExt);
	 * System.out.println(isAllowed);
	 * // => true
	 * </code></pre>
	 * @param imageFileExt .을 제외한 이미지 파일 확장자 (예) jpg) 
	 * @return
	 */
	public static boolean isImageExtensionAllowed(String imageFileExt) {
		if (imageFileExt == null) {
			return false;
		}

		return allowedImageExtensionSet.contains(imageFileExt);
	}

	/**
	 * 이미지 크기가 허용된 최대 크기보다 작은지 체크합니다.<br/>
	 * application.properties의 winitech.file.upload.image.max-size 를 통해 최대 크기를 설정할 수 있습니다.
	 * @param imageSize 파일 크기 (바이트)
	 * @return 허용된 최대 크기보다 작으면 true, 아니면 false
	 */
	public static boolean isImageSizeAllowed(long imageSize) {
		return allowedImageMaxSize > imageSize;
	}

	/**
	 * 파일 업로드 시 확장자와 크기를 체크합니다.<br/>
	 * 파일 확장자나 크기가 허용된 범위를 초과하면 예외(InvalidParamException)를 발생시킵니다.
	 * @param fileName 파일 이름
	 * @param fileSize 파일 크기 (바이트)
	 */
	public static void checkFileUpload(String fileName, long fileSize) {
		String fileExt = getFileExtension(fileName);
		
		if (fileExt != null) {
			fileExt = fileExt.toLowerCase();
		}
		
		if (! isFileExtensionAllowed(fileExt)) {
			throw new InvalidParamException("The file extension is not allowed.");	
		} 
		
		if (! isFileSizeAllowed(fileSize)) {
			throw new InvalidParamException("The file size is too large.");
		}
	}

	/**
	 * 이미지 업로드 시 확장자와 크기를 체크합니다.
	 * 파일 확장자나 크기가 허용된 범위를 초과하면 예외(InvalidParamException)를 발생시킵니다.
	 * @param imageFileName 파일 이름
	 * @param imageFileSize 파일 크기 (바이트)
	 */
	public static void checkImageUpload(String imageFileName, long imageFileSize) {
		String imageFileExt = getFileExtension(imageFileName);
		
		if (imageFileExt != null) {
			imageFileExt = imageFileExt.toLowerCase();
		}
		
		if (! isImageExtensionAllowed(imageFileExt)) {
			throw new InvalidParamException("The image extension is not allowed.");	
		}
		
		if (! isImageSizeAllowed(imageFileSize)) {
			throw new InvalidParamException("The image size is too large.");
		}
	}

	/**
	 * 파일 이름을 통해 MIME Type을 구합니다.
	 * <pre><code>
	 * // 사용예제
	 * String fileName = "example.jpg";
	 * String mimeType = WiniFile.getMimeType(fileName);
	 * System.out.println(mimeType);
	 * // => image/jpeg
	 * </code></pre>
	 * @param fileName 파일 이름
	 * @return MIME Type (예: image/jpeg, application/pdf 등)
	 */
	@NonNull
	public static String getMimeType(String fileName) {
		// TODO : Tika등의 라이브러리를 사용하여 MIME Type을 추출하는 로직 추가. 하지만 굳이 필요할까 싶기도 함.
		return MediaTypeFactory.getMediaType(fileName).orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
	}

	private static final Pattern INVALID_CHARACTERS = Pattern.compile("[<>:\"/\\|?*\\\\]");

	/**
	 * 파일 이름의 유효하지 않은 문자를 밑줄(_)로 대체합니다.
	 * <pre><code>
	 * // 사용예제
	 * String fileName = "example&lt;>:\"/\\|?*.hwp";
	 * String safeFileName = WiniFile.makeSafeFileName(fileName);
	 * System.out.println(safeFileName); 
	 * // => example________.hwp
	 * </code></pre>
	 * @param fileName 원본 파일 이름
	 * @return 유효하지 않은 문자가 밑줄로 대체된 안전한 파일 이름
	 */
	@NonNull
	public static String makeSafeFileName(String fileName) {
		if (fileName == null || fileName.isEmpty()) {
			return "";
		}
		
		return INVALID_CHARACTERS.matcher(fileName).replaceAll("_");
	}

	/**
	 * 파일 이름을 URL 인코딩하여 다운로드 시 한글이 깨지지 않게 사용할 수 있는 형식으로 변환합니다.
	 * <pre><code>
	 * // 사용예제
	 * String fileName = "한글파일이름.txt";
	 * String encodedFileName = WiniFile.encodeDownloadFileName(fileName);
	 * System.out.println(encodedFileName); // => %ED%95%9C%EA%B8%80%ED%8C%8C%EC%9D%BC%EC%9D%B4%EB%A6%84.txt
	 * </code></pre>
	 * @param fileName 파일 이름
	 * @return URL 인코딩된 파일 이름
	 */
	@NonNull
	public static String encodeDownloadFileName(String fileName) {
		return encodeDownloadFileName(fileName, null);
	}

	/**
	 * 파일 이름을 URL 인코딩하여 다운로드 시 한글이 깨지지 않게 사용할 수 있는 형식으로 변환합니다.<br/>
	 * 이때 브라우저의 user agent에 따라 인코딩 방식을 다르게 적용합니다.
	 * <pre><code>
	 * // 사용예제
	 * String fileName = "한글파일이름.txt";
	 * String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3";
	 * String encodedFileName = WiniFile.encodeDownloadFileName(fileName, userAgent);
	 * System.out.println(encodedFileName); // => %ED%95%9C%EA%B8%80%ED%8C%8C%EC%9D%BC%EC%9D%B4%EB%A6%84.txt
	 * </code></pre>
	 * @param fileName 파일 이름
	 * @param userAgent 브라우저 user agent
	 * @return URL 인코딩된 파일 이름
	 */
	@NonNull
	public static String encodeDownloadFileName(String fileName, String userAgent) {
		String safeFileName = makeSafeFileName(fileName);
		
		switch (getBrowser(userAgent)) {
			case "Firefox": /* FALLTHROUGH */
			case "Opera": {
				return "\"" + new String(safeFileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + "\"";
			}
			case "MSIE": /* FALLTHROUGH */
			case "Trident": /* FALLTHROUGH */
			default: {
				return URLEncoder.encode(safeFileName, StandardCharsets.UTF_8).replace("+", "%20");
			}
		}
	}

	/**
	 * 파일 경로를 정규화합니다. (\\를 /로 통일)
	 * <pre><code>
	 * // 사용예제
	 * String filePath = "C:\\Users\\User\\Documents\\file.txt";
	 * String normalizedPath = WiniFile.normalizeDir(filePath);
	 * System.out.println(normalizedPath); 
	 * // => C:/Users/User/Documents/file.txt
	 * </code></pre>
	 * @param path 파일 경로
	 * @return 정규화된 파일 경로
	 */
	@NonNull
	private static String normalizeDir(String path) {
		if (path == null) {
			return "";
		}
		
		return path.replace('\\', '/');
	}

	/**
	 * 브라우저의 user agent를 통해 브라우저 종류를 구분합니다.<br/>
	 * MSIE (IE11 제외한 IE), Trident (IE11), Firefox, Opera, Chrome (기본) 중 하나를 반환합니다.
	 * <pre><code>
	 * // 사용예제
	 * String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3";
	 * String browser = WiniFile.getBrowser(userAgent);
	 * System.out.println(browser); // => Chrome
	 * </code></pre>
	 * @param userAgent 브라우저 user agent
	 * @return 브라우저 종류 (MSIE, Trident, Firefox, Opera, Chrome)
	 */
	@NonNull
	private static String getBrowser(String userAgent) {
		if (userAgent == null) {
			return "Chrome";
		}
		if (userAgent.contains("MSIE")) {
			return "MSIE";
		} else if (userAgent.contains("Trident")) { // IE11 문자열 깨짐 방지
			return "Trident";
		} else if (userAgent.contains("Firefox")) {
			return "Firefox";
		} else if (userAgent.contains("Opera")) {
			return "Opera";
		}
		return "Chrome";
	}

	/**
	 * 파일 다운로드 시 Content-Disposition 헤더를 설정합니다.<br/>
	 * 한글 파일 이름을 지원하기 위해 URL 인코딩을 적용합니다.
	 * <pre><code>
	 * // 사용예제
	 * String fileName = "한글파일이름.txt";
	 * WiniFile.setDisposition(fileName, req, res);
	 * // => Content-Disposition: attachment; filename="%ED%95%9C%EA%B8%80%ED%8C%8C%EC%9D%BC%EC%9D%B4%EB%A6%84.txt"
	 * </code></pre>
	 * @param fileName 파일 이름
	 * @param req HttpServletRequest
	 * @param res HttpServletResponse
	 */
	public static void setDisposition(String fileName, HttpServletRequest req, HttpServletResponse res) {
		res.setHeader("Content-Disposition", "attachment; filename=" + encodeDownloadFileName(fileName, req.getHeader("User-Agent")));
	}

	/**
	 * 파일 보기 시 Content-Disposition 헤더를 설정합니다.<br/>
	 * 한글 파일 이름을 지원하기 위해 URL 인코딩을 적용합니다.<br/>
	 * <br/>
	 * WiniFile.setDisposition와의 차이는 WiniFile.setDisposition는 attachment로 설정되어 다운로드가 강제되지만, 
	 * 이 메쏘드는 inline으로 설정되어 브라우저에서 다운로드 없이 파일을 열 수 있도록 합니다.
	 * <pre><code>
	 * // 사용예제
	 * String fileName = "한글파일이름.txt";
	 * WiniFile.setDispositionInline(fileName, req, res);
	 * // => Content-Disposition: attachment; filename="%ED%95%9C%EA%B8%80%ED%8C%8C%EC%9D%BC%EC%9D%B4%EB%A6%84.txt"
	 * </code></pre>
	 * @param fileName 파일 이름
	 * @param req HttpServletRequest
	 * @param res HttpServletResponse
	 */
	public static void setDispositionInline(String fileName, HttpServletRequest req, HttpServletResponse res) {
		res.setHeader("Content-Disposition", "inline; filename=" + encodeDownloadFileName(fileName, req.getHeader("User-Agent")));
	}

	/**
	 * 파일이 저장되는 기본 디렉토리를 읽어옵니다.<br/>
	 * application.properties의 winitech.file.base-dir를 통해 설정할 수 있습니다.
	 * <pre><code>
	 * // 사용예제
	 * String baseDir = WiniFile.getBaseDir();
	 * System.out.println(baseDir); 
	 * // => /data/uploadDir/
	 * </code></pre>
	 * @return
	 */
	@NonNull
	public static String getBaseDir() {
		if (fileBaseDir == null) {
			throw new InvalidParamException("The base directory is not set.");
		}
		
		return fileBaseDir;
	}

	/**
	 * 파일 경로가 절대 경로인지 체크하고, 
	 * 절대 경로가 아니면 기본 파일 디렉토리(winitech.file.base-dir)를 기준으로 절대 경로를 구합니다.
	 * @param path 파일 경로 (상대경로 또는 절대경로)
	 * @return 절대 경로
	 */
	public static String getAbsoluteFilePath(String path) {
		if (path == null || path.isEmpty()) {
			return getBaseDir();
		}
		
		path = path.replace("\\", "/");
		
		if (isAbsolutePath(path)) {
			// 절대 경로인 경우
			return path;
		}

		try {
			return Paths.get(getBaseDir(), path).toFile().getCanonicalPath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 파일 경로가 절대 경로인지 체크합니다.
	 * @param path 파일 경로
	 * @return 절대 경로이면 true, 아니면 false
	 */
	private static boolean isAbsolutePath(String path) {
		return path.startsWith("/") || Pattern.compile("^[a-zA-Z]:/").matcher(path).find();
	}

	/**
	 * 파일 경로가 기본 디렉토리로 시작하는지 체크합니다.<br/>
	 * 파일 경로가 기본 디렉토리로 시작하지 않으면 예외(InvalidParamException)를 발생시킵니다.
	 * <pre><code>
	 * // 사용예제
	 * String filePath = "/data/uploadDir/example.txt";
	 * String checkedPath = WiniFile.checkBaseDir(filePath);
	 * System.out.println(checkedPath);
	 * // => /data/uploadDir/example.txt
	 *
	 * String filePath2 = "/var/log/message.log";
	 * String checkedPath2 = WiniFile.checkBaseDir(filePath);
	 * // => InvalidParamException 예외 발생
	 * </code></pre>
	 * @param filePath 파일 절대 경로
	 * @return
	 */
	@NonNull
	public static String checkBaseDir(String filePath) {
		if (! WiniString.lower(filePath).startsWith(WiniString.lower(getBaseDir()))) {
			throw new InvalidParamException("Invalid file path. The file path must start with \"base directory\".");
		}
		
		return filePath;
	}

	/**
	 * 파일 경로가 샌드박스 디렉토리로 시작하는지 체크하고<br/>
	 * 파일 경로가 샌드박스 디렉토리로 시작하지 않으면 예외(InvalidParamException)를 발생시킵니다.<br/>
	 * <br/>
	 * 샌드박스 디렉토리는 백엔드에서 파일 업로드 및 다운로드 뿐만 아니라 모든 파일 관련 작업에 대해<br/>
	 * 허용된 디렉토리 목록을 설정합니다.<br/>
	 * 샌드박스 디렉토리 목록은 application.properties의 winitech.file.sandboxs를 통해 설정할 수 있습니다.<br/>
	 * <br/>
	 * <strong>자동으로 체크되는것이 아니니 명시적으로 필요한 곳에서 호출해야 합니다.</strong>
	 * ""도 예외로 처리됩니다.
	 * <pre><code>
	 * // 사용예제
	 * String filePath = "/data/uploadDir/example.txt";
	 * String checkedPath = WiniFile.checkSandbox(filePath);
	 * System.out.println(checkedPath);
	 * // => /data/uploadDir/example.txt
	 * 
	 * String filePath2 = "/var/log/message.log";
	 * String checkedPath2 = WiniFile.checkSandbox(filePath);
	 * // => InvalidParamException 예외 발생
	 * </code></pre>
	 * @param filePath 파일 절대 경로
	 * @return filePath를 그대로 전달
	 */
	@NonNull
	public static String checkSandbox(String filePath) {
		if (filePath == null) {
			throw new InvalidParamException("Invalid file path. The file path must not be null.");
		}
		String lowerCaseFilePath = WiniFile.normalizeDir(WiniString.lower(filePath));

		for (String sandbox : allowedFileSandboxSetLowerCase) {
			if (lowerCaseFilePath.startsWith(sandbox)) {
				return filePath;
			}
		}

		if (filePath.contains("../")) {
			throw new InvalidParamException("Invalid file path. The file path must not contain \"../\".");
		} else if (filePath.contains("..\\")) {
			throw new InvalidParamException("Invalid file path. The file path must not contain \"..\\\".");
		}
		
		throw new InvalidParamException("Invalid file path. The file path is not allowed in the sandbox.");
	}

	/**
	 * 파일 경로에 ../이나 ..\\가 포함되어 있는지 체크하고<br/>
	 * 있는 경우 예외(InvalidParamException)를 발생시킵니다.<br/>
	 * 또한 checkSandbox 메소드를 호출하여 샌드박스 디렉토리로 시작하는지 체크합니다.<br/>
	 * <br/>
	 * checkSandbox와의 차이점은 filePathBlackList는 ""을 허용합니다.
	 * @param filePath 파일 절대 경로
	 * @return filePath를 그대로 전달
	 * @see #checkSandbox(String filePath)
	 */
	@NonNull
	public static String filePathBlackList(String filePath) {
		if (filePath == null || filePath.isEmpty()) {
			return "";
		}
		
		if (filePath.contains("../")) {
			throw new InvalidParamException("Invalid file path. The file path must not contain \"../\".");
		} else if (filePath.contains("..\\")) {
			throw new InvalidParamException("Invalid file path. The file path must not contain \"..\\\".");
		}
		
		return checkSandbox(filePath);
	}

	public static void checkSignedFileId(String signedFileId) {
		String[] signedFileIdArr = signedFileId.split("_");
		if (signedFileIdArr.length != 3) {
			throw new InvalidParamException("Invalid signedFileId Format");
		}

		long expiredAt = Long.parseLong(signedFileIdArr[1]);
		if (expiredAt < Instant.now().getEpochSecond()) {
			throw new InvalidParamException("Expired signedFileId");
		}

		String fileId = signedFileIdArr[0];
		if (! signedFileId.equals(getSignedFileId(UUID.fromString(fileId), Instant.ofEpochSecond(expiredAt)))) {
			throw new InvalidParamException("Invalid signedFileId");
		}
	}

	public static UUID getFileIdFromSignedFileId(String signedFileId) {
		return UUID.fromString(signedFileId.substring(0, signedFileId.indexOf("_")));
	}

	public static String getSignedFileId(UUID fileId) {
		Instant expiredAt = Instant.now().plusSeconds(signedFileIdDuration);

		return getSignedFileId(fileId, expiredAt);
	}

	public static String getSignedFileId(UUID fileId, Instant expiredAt) {
		long epochSecond = expiredAt.getEpochSecond();
		String hashedMd5 = WiniSecurity.hashMd5(signedFileIdSecret + "@" + epochSecond + "$" + fileId + "#" + signedFileIdSecret);
		return fileId + "_" + epochSecond + "_" + hashedMd5.substring(0, 5) + hashedMd5.substring(hashedMd5.length() - 5);
	}
}
