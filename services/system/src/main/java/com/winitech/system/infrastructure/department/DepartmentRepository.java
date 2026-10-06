package com.winitech.system.infrastructure.department;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.winitech.system.domain.department.Department;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * com.winitech.system.infrastructure.department
 * └ UserOrganizationRepository.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/05
 **/
public interface DepartmentRepository extends JpaRepository<Department, UUID> {

	Optional<Department> findByIdAndSystemStatus(UUID departmentId, Department.SystemStatus systemStatus);

	List<Department> findAllBySystemStatusOrderByDepartmentNameAscIdAsc(Department.SystemStatus status);

	List<Department> findAllBySystemStatusOrderBySortSeqAscDepartmentNameAscIdAsc(Department.SystemStatus systemStatus);

	Optional<Department> findByDepartmentCodeAndSystemStatus(String departmentCode, Department.SystemStatus status);

	List<Department> findAllByParentDepartmentId(UUID parentDepartmentId);

	List<Department> findByDepartmentNameAndParentDepartmentId(String departmentName, UUID parentDepartmentId);

	boolean existsByDepartmentCodeAndIdNotAndSystemStatus(String departmentCode, UUID departmentId, Department.SystemStatus systemStatus);

	@Modifying(clearAutomatically = true)
	@Query("" +
			"UPDATE Department m " +
			"   SET m.parentDepartment = :parentDepartment, " +
			"       m.sortSeq = :sortSeq " +
			" WHERE m.id = :id" +
			"   AND m.systemStatus = 'ENABLE'")
	void updateParentAndSortSeq(@Param("id") UUID departmentId, @Param("parentDepartment") Department parentDepartment, @Param("sortSeq") int sortSeq);
}
