package com.winitech.common.domain.common;

import java.util.List;

/**
 * 공통 파일 스토어
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonFileStore.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-13 17:34
 **/
public interface CommonFileStore {
	CommonFile store(CommonFile commonFile);
	List<CommonFile> storeAll(List<CommonFile> commonFileList);
	CommonFile modify(CommonFile commonFile, CommonFileCommand command);
	void remove(CommonFile commonFile);
	void remove(List<CommonFile> commonFileList);
}
