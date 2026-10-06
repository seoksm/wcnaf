package com.winitech.system.infrastructure.department;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.winitech.common.exception.EntityNotFoundException;
import com.winitech.system.domain.department.Department;
import com.winitech.system.domain.department.DepartmentReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * com.winitech.system.infrastructure.userOrganization
 * └ UserOrganizationReaderImpl.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/05
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class DepartmentReaderImpl implements DepartmentReader {

	private final DepartmentRepository departmentRepository;
	
	@Override
	public Department getDepartment(UUID departmentId) {
		return departmentRepository.findByIdAndSystemStatus(departmentId, Department.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public List<Department> getAllDepartment() {
		return departmentRepository.findAllBySystemStatusOrderByDepartmentNameAscIdAsc(Department.SystemStatus.ENABLE);
	}
	
	@Override
	public List<Department> getAllDepartmentTree() {
		return departmentRepository.findAllBySystemStatusOrderBySortSeqAscDepartmentNameAscIdAsc(Department.SystemStatus.ENABLE);
	}
	
	@Override
	public Department getDepartmentByCode(String departmentCode) {
		return departmentRepository.findByDepartmentCodeAndSystemStatus(departmentCode, Department.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public List<Department> getUpperDepartment(UUID parentDepartmentId) {
		return departmentRepository.findAllByParentDepartmentId(parentDepartmentId);
	}
	
	@Override
	public List<Department> existsByDepartmentNameAndUpperDepartment(String departmentName, UUID parentDepartmentId) {
		return departmentRepository.findByDepartmentNameAndParentDepartmentId(departmentName, parentDepartmentId);
	}

	@Override
	public boolean existDepartmentByDepartmentCodeAndExcludingSelf(String departmentCode, UUID departmentId) {
		return departmentRepository.existsByDepartmentCodeAndIdNotAndSystemStatus(departmentCode, departmentId, Department.SystemStatus.ENABLE);
	}
}
