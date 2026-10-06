package com.winitech.common.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * <pre>
 * com.winitech.common.domain
 * └ AbstractPagingRequestCommand.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-07-03 10:14
 **/
@Getter
public abstract class AbstractPagingRequestCommand {
	/**
	 * 페이지 번호 (0부터 시작)
	 */
	protected int page = 0;
	/**
	 * 페이지 크기 (기본 10)
	 */
	protected int pageSize = 10;
	
	public int getPageStartOffset() {
		return page * pageSize;
	}
	
	public int getPageEndOffset() {
		return getPageStartOffset() + pageSize;
	}

	public Pageable getPageable() {
		return PageRequest.of(page, pageSize);
	}
}
