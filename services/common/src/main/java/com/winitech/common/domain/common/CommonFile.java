package com.winitech.common.domain.common;

import com.amazonaws.services.ec2.model.Storage;
import com.winitech.common.domain.AbstractEntity;
import com.winitech.common.domain.AbstractUuidEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 공통 파일 엔티티
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonFile.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 24-12-13
 **/
@Slf4j
@Getter
@Setter
@Entity
@NoArgsConstructor
public class CommonFile extends AbstractEntity {
	@Id
	private UUID id;

	/**
	 * 파일명
	 */
	private String fileName;

	/**
	 * 파일 확장자 (. 제외)
	 */
	@Column(length = 10)
	private String fileExt;

	/**
	 * 파일 크기
	 */
	private Long fileSize;

	/**
	 * MIME 타입
	 */
	private String mimeType;

	/**
	 * 파일 저장소 유형
	 */
	@Enumerated(EnumType.STRING)
	@Column(length = 10)
	private StorageType storageType;

	/**
	 * 파일 기원
	 */
	@Enumerated(EnumType.STRING)
	@NonNull
	@Column(length = 20)
	private FileOrigin fileOrigin;

	/**
	 * 파일 경로 URL
	 */
	private String url;

	/**
	 * 사용자 고유번호
	 */
	private UUID userId;

	/**
	 * 엔티티명 (테이블명)
	 */
	@Column(length = 30)
	private String entityName;

	/**
	 * 엔티티 고유번호 (PK)
	 */
	private UUID entityId;
	
	/**
	 * 서브 키
	 */
	@Column(length = 50)
	private String subKey;

	/**
	 * 추가 정보
	 */
	private String extraInfo;
	
	/**
	 * 비활성화 일시
	 */
	private OffsetDateTime disabledAt;
	
	@Enumerated(EnumType.STRING)
	@NonNull
	@Column(length = 10)
	private Status status;
	
	@Getter
	@RequiredArgsConstructor
	public enum Status {
		ENABLE("활성화"), DISABLE("비활성화");
		private final String description;
	}
	
	private Integer sortSeq;

	/**
	 * 가로 픽셀
	 */
	private Integer width;

	/**
	 * 세로 픽셀
	 */
	private Integer height;

	@Getter
	@RequiredArgsConstructor
	public enum FileOrigin {
		WINI_EDITOR("WiniEditor Image"), 
		FILE_UPLOAD("FileUpload"), 
		ETC("기타");
		private final String description;
		
		public static FileOrigin from(String value) {
			if (value == null) {
				return ETC;
			}
			
			try {
				return FileOrigin.valueOf(value);
			} catch (IllegalArgumentException _ignored) {
				// 무시 처리  
				
				return ETC;
			}
		}
	}

	@Getter
	@RequiredArgsConstructor
	public enum StorageType {
		S3("S3 호환 API");
		private final String description;
	}

	@Builder
	public CommonFile(
			UUID id,
			String fileName,
			String fileExt,
			Long fileSize,
			String mimeType,
			
			StorageType storageType,
			FileOrigin fileOrigin,
			String url,
			UUID userId,
			String entityName,
			UUID entityId,
			
			String subKey,
			String extraInfo,
			
			Integer sortSeq,
			Integer width, 
			Integer height
	) {
		this.id = id;
		this.fileName = fileName;
		this.fileExt = fileExt;
		this.fileSize = fileSize;
		this.mimeType = mimeType;
		
		this.storageType = storageType;
		this.fileOrigin = fileOrigin;
		this.url = url;
		this.userId = userId;
		this.entityName = entityName;
		
		this.entityId = entityId;		
		this.subKey = subKey;
		this.extraInfo = extraInfo;
		this.sortSeq = sortSeq;
		this.status = Status.ENABLE;		
		this.disabledAt = null;
		
		this.width = width;
		this.height = height;
	}
	
	public void enable() {
		this.status = Status.ENABLE;
		this.disabledAt = null;
	}
	
	public void disable() {
		this.status = Status.DISABLE;
		this.disabledAt = OffsetDateTime.now();
	}
}
