package com.winitech.common.library.commonType;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * <pre>
 * com.winitech.common.library.commonType
 * └ WiniPageInfo.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-23 15:27
 **/
@Getter 
@Setter
@NoArgsConstructor
@Builder
public class WiniPageInfo<T> {
	private int page;

	private long totalRecords;

	private int pageSize;
	
	private List<T> content;

	public WiniPageInfo(int page, long totalRecords, int pageSize) {
		this.page = page;
		this.totalRecords = totalRecords;
		this.pageSize = pageSize;
		this.content = new ArrayList<>();
	}
	
	public WiniPageInfo(int page, long totalRecords, int pageSize, Collection<T> content) {
		this.page = page;
		this.totalRecords = totalRecords;
		this.pageSize = pageSize;
		this.content = new ArrayList<>(content);
	}
	
	public WiniPageInfo(Page<T> page) {
		this.page = page.getNumber();
		this.totalRecords = page.getTotalElements();
		this.pageSize = page.getSize();
		this.content = new ArrayList<>(page.getContent());
	}

	public Stream<T> stream() {
		return this.getContent().stream();
	}
}
