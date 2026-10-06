package com.winitech.system.interfaces.inboundAdapter.web.department;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.library.WiniString;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.winitech.common.response.CommonResponse;
import com.winitech.system.application.department.DepartmentFacade;
import com.winitech.system.domain.department.DepartmentCommand;
import com.winitech.system.domain.department.DepartmentInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
* com.winitech.system.interfaces.inboundAdapter.web.department
* ㄴ DepartmentApiController.java
* @author : 박준희 과장 (부설연구소)
* @since : 2022-03-25 오후 9:33
* @see : None
 **/
@Api(tags = "Department", description = "부서를 관리하는 API Controller")
@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@ForceDefaultTenant
@RequestMapping("/api/v1/system/department")
public class DepartmentApiController {
    private final DepartmentFacade departmentFacade;
    
    @ApiParam(name = "request", value = "생성할 필드", required = true, type = "object")
    @PostMapping
    public CommonResponse<DepartmentDto.DepartmentStoreResponse> registerDepartment(@RequestBody @Valid DepartmentDto.DepartmentRequest request) {
        DepartmentCommand command = request.toCommand();
        DepartmentInfo userInfo = departmentFacade.registerDepartment(command);
		DepartmentDto.DepartmentStoreResponse response = new DepartmentDto.DepartmentStoreResponse(userInfo);
        return  CommonResponse.success(response);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "departmentIdOrCode", value = "부서 ID 또는 코드", required = true, dataType = "string", paramType = "path", example = "00000000-0000-0000-0000-000000000000"),
    })
    @GetMapping("/{departmentIdOrCode}")
    public CommonResponse<DepartmentDto.DepartmentResponse> searchDepartmentInfo(@Valid @PathVariable String departmentIdOrCode) {
        DepartmentInfo departmentInfo;
        
        if (WiniString.isUuid(departmentIdOrCode)) {
            departmentInfo = departmentFacade.searchDepartmentInfo(UUID.fromString(departmentIdOrCode));
        } else {
            departmentInfo = departmentFacade.searchByDepartmentCode(departmentIdOrCode);
        }

		DepartmentDto.DepartmentResponse response = new DepartmentDto.DepartmentResponse(departmentInfo);
        return CommonResponse.success(response);
    }

    @GetMapping
    public CommonResponse<List<DepartmentDto.DepartmentResponse>> searchAllDepartment() {
        List<DepartmentInfo> userInfoList = departmentFacade.searchAllDepartment();
        List<DepartmentDto.DepartmentResponse> response = userInfoList.stream()
                .map(DepartmentDto.DepartmentResponse::new).collect(Collectors.toList());
        return CommonResponse.success(response);
    }

    @GetMapping("/tree")
    public CommonResponse<List<DepartmentDto.DepartmentTreeResponse>> searchMenuTree() {
        List<DepartmentInfo.DepartmentTreeInfo> allMenu = departmentFacade.searchDepartmentTree();

        List<DepartmentDto.DepartmentTreeResponse> response = allMenu
                .stream()
                .map(DepartmentDto.DepartmentTreeResponse::new)
                .collect(Collectors.toList());

        return CommonResponse.success(response);
    }

    @GetMapping("{departmentId}/children")
    public CommonResponse<List<DepartmentDto.DepartmentResponse>> searchByParentDepartment(@Valid @PathVariable UUID departmentId) {
    	List<DepartmentInfo> userInfoList = departmentFacade.searchByParentDepartmentId(departmentId);
    	List<DepartmentDto.DepartmentResponse> response = userInfoList.stream()
    			.map(DepartmentDto.DepartmentResponse::new).collect(Collectors.toList());
    	return CommonResponse.success(response);
    }

//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "departmentId", value = "부서 ID", required = true, dataType = "UUID", paramType = "path"),
//            @ApiImplicitParam(name = "request", value = "수정할 필드", required = true, dataType = "object", paramType = "formData")
//    })
    @RequestMapping(value = "{departmentId}", method = RequestMethod.PATCH)
    public CommonResponse<DepartmentDto.DepartmentStoreResponse> modifyDepartment(@Valid @PathVariable UUID departmentId, @RequestBody DepartmentDto.DepartmentRequest request) {
        DepartmentCommand command = request.toCommand();
        DepartmentInfo departmentInfo = departmentFacade.modifyDepartment(departmentId, command);
        DepartmentDto.DepartmentStoreResponse response = new DepartmentDto.DepartmentStoreResponse(departmentInfo);
        return CommonResponse.success(response);
    }
    
    @RequestMapping(value = "order", method = RequestMethod.PATCH)
    public CommonResponse<String> modifyDepartmentOrder(@RequestBody List<DepartmentDto.DepartmentOrderModifyRequest> request) {
        List<DepartmentCommand.OrderModifyRequestCommand> commands = request
                .stream()
                .map(DepartmentDto.DepartmentOrderModifyRequest::toCommand)
                .collect(Collectors.toList());
        
        departmentFacade.modifyDepartmentOrder(commands);
        
        return CommonResponse.success("OK");
    }

//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "userGroupId", value = "유저 그룹 ID", required = true, dataType = "Long", paramType = "path")
//    })
    @DeleteMapping("/{departmentId}")
    public CommonResponse<String> removeDepartment(@Valid @PathVariable UUID departmentId) {
        departmentFacade.removeDepartment(departmentId);
        return CommonResponse.success("OK");
    }
}
