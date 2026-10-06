package com.winitech.system.interfaces.inboundAdapter.web.department;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.winitech.system.domain.department.Department;
import com.winitech.system.domain.department.DepartmentCommand;
import com.winitech.system.domain.department.DepartmentInfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
* com.winitech.system.interfaces.userGroup
* ㄴ UserGroupDto.java
* @author : 박준희 과장 (부설연구소)
* @since : 2022-03-25 오후 9:34
* @see : None
 **/
public class DepartmentDto {
    /*
    * ID
    * 그룹 코드
    * 그룹 이름
    * */
	@Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartmentRequest {
		@NotNull(message = "부서Code는 필수 값입니다.")
		@ApiModelProperty(value = "부서Code", example = "BE2", required = true)
		private String departmentCode;
        @NotNull(message = "부서명은 필수 값입니다.")
        @ApiModelProperty(value = "부서명", example = "BE2팀", required = true)
        private String departmentName;
        @ApiModelProperty(value = "정렬순서", example = "0")
        private Integer sortSeq;
        @NotNull(message = "사용여부는 필수 값입니다.")
        @ApiModelProperty(value = "사용여부", example = "ENABLE", required = true)
        private Department.Status status;
    	
    	@ApiModelProperty(value = "상위부서 ID", example = "017b6b3b-1b3b-4b6b-8b3b-1b3b4b6b8b3b", required = false)
    	private UUID parentDepartmentId;

        public DepartmentCommand toCommand() {
            return DepartmentCommand.builder()
                    .departmentCode(departmentCode)
                    .departmentName(departmentName)
                    .sortSeq(sortSeq)
                    .status(status)
                    .parentDepartmentId(parentDepartmentId)
                    .build();
        }
    }
    @Getter
    @ToString
    public static class DepartmentStoreResponse {
        @ApiModelProperty(value = "부서 ID", example = "017b6b3b-1b3b-4b6b-8b3b-1b3b4b6b8b3b")
        private final UUID departmentId;

        public DepartmentStoreResponse (DepartmentInfo departmentInfo) {
            this.departmentId = departmentInfo.getId();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartmentOrderModifyRequest {
        @ApiModelProperty(value = "부서 ID", example = "017b6b3b-1b3b-4b6b-8b3b-1b3b4b6b8b3b", required = true)
        private UUID id;
        @ApiModelProperty(value = "상위부서 ID", example = "017b6b3b-1b3b-4b6b-8b3b-1b3b4b6b8b3b", required = true)
        private UUID parentDepartmentId;

        public DepartmentCommand.OrderModifyRequestCommand toCommand() {
            return DepartmentCommand.OrderModifyRequestCommand.builder()
                    .departmentId(id)
                    .parentDepartmentId(parentDepartmentId)
                    .build();
        }
    }

    @Getter
    @ToString
    public static class DepartmentResponse {
        @ApiModelProperty(value = "부서 ID", example = "017b6b3b-1b3b-4b6b-8b3b-1b3b4b6b8b3b")
        private final UUID id;
        @ApiModelProperty(value = "부서 Code", example = "BE2", required = true)
        private final String departmentCode;
        @ApiModelProperty(value = "부서명", example = "BE2팀", required = true)
        private final String departmentName;
        @ApiModelProperty(value = "상위부서 ID", example = "017b6b3b-1b3b-4b6b-8b3b-1b3b4b6b8b3b", required = false)
        private final UUID parentDepartmentId;
        @ApiModelProperty(value = "정렬순서", example = "0")
        private final Integer sortSeq;
        @ApiModelProperty(value = "사용여부", example = "ENABLE", required = false)
        private final Department.Status status;

        public DepartmentResponse (DepartmentInfo departmentInfo) {
            this.id = departmentInfo.getId();
            this.departmentCode = departmentInfo.getDepartmentCode();
            this.departmentName = departmentInfo.getDepartmentName();
            this.parentDepartmentId = departmentInfo.getParentDepartmentId();
            this.sortSeq = departmentInfo.getSortSeq();
            this.status = departmentInfo.getStatus();
        }
    }

    @Getter
    @ToString
    public static class DepartmentTreeResponse {
        @ApiModelProperty(value = "부서 ID", example = "017b6b3b-1b3b-4b6b-8b3b-1b3b4b6b8b3b")
        private final UUID id;
        @ApiModelProperty(value = "부서 Code", example = "BE2", required = true)
        private final String departmentCode;
        @ApiModelProperty(value = "부서명", example = "BE2팀", required = true)
        private final String departmentName;
        @ApiModelProperty(value = "상위부서 ID", example = "017b6b3b-1b3b-4b6b-8b3b-1b3b4b6b8b3b", required = false)
        private final UUID parentDepartmentId;
        @ApiModelProperty(value = "정렬순서", example = "0")
        private final Integer sortSeq;
        @ApiModelProperty(value = "사용여부", example = "ENABLE", required = false)
        private final Department.Status status;
        @ApiModelProperty(value = "하위부서", required = false)     
        private final List<DepartmentTreeResponse> children;

        public DepartmentTreeResponse (DepartmentInfo.DepartmentTreeInfo departmentInfo) {
            this.id = departmentInfo.getId();
            this.departmentCode = departmentInfo.getDepartmentCode();
            this.departmentName = departmentInfo.getDepartmentName();
            this.parentDepartmentId = departmentInfo.getParentDepartmentId();
            this.sortSeq = departmentInfo.getSortSeq();
            this.status = departmentInfo.getStatus();

            if (departmentInfo.getChildrenDepartment() == null) {
                this.children = new ArrayList<>();
            } else {
                this.children = departmentInfo.getChildrenDepartment()
                        .stream()
                        .map(DepartmentTreeResponse::new)
                        .collect(Collectors.toList());
            }
        }
    }
}
