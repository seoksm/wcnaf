package com.winitech.system.domain.department;

import java.util.UUID;

/**
* com.winitech.user.domain
* ㄴ UserStore.java
* @author : 박준희 과장 (부설연구소)
* @since : 2021-12-15 오후 4:56
* @see : None
 **/
public interface DepartmentStore {
	Department store(Department department);
	Department modify(UUID departmentId, DepartmentCommand departmentCommand);
	void remove(UUID departmentId);
	void updateParentAndSortSeq(UUID departmentId, UUID parentDepartmentId, int sortSeq);
	void flush();
}
