package com.winitech.common.domain.common;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonUserReader.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-21 12:44
 **/
public interface CommonUserReader {
	CommonUser getCommonUserById(UUID id);
	
	CommonUser getCommonUserByIdIfExists(UUID id);

	List<CommonUser> getCommonUserByName(String name);
	
	List<CommonUser> getAllCommonUser();
	
	boolean isExistCommonUserById(UUID id);

	Page<CommonUserInfo> getCommonUserPage(Integer page, Integer pageSize, String searchType, String searchKeyword, CommonUser.Status userStatus);
}