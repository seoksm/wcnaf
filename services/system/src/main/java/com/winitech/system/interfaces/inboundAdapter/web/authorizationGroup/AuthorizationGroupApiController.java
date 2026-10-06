package com.winitech.system.interfaces.inboundAdapter.web.authorizationGroup;

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

import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.response.CommonResponse;
import com.winitech.system.application.authorizationGroup.AuthorizationGroupFacade;
import com.winitech.system.domain.authorizationGroup.AuthorizationGroupCommand;
import com.winitech.system.domain.authorizationGroup.AuthorizationGroupInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
* com.winitech.system.interfaces.inboundAdapter.web.authorizationGroup
* ㄴ AuthorizationGroupApiController.java
* @author : 박준희 과장 (부설연구소)
* @since : 2022-03-25 오후 9:33
* @see : None
 **/
@Api(tags = "권한 그룹 서비스 API")
@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@ForceDefaultTenant
@RequestMapping("/api/v1/system/authorization-group")
public class AuthorizationGroupApiController {
    private final AuthorizationGroupFacade authorizationGroupFacade;
    private final LoginUserContext loginUserContext;
    
    @ApiParam(name = "request", value = "생성할 필드", required = true, type = "object")
    @PostMapping
    public CommonResponse<AuthorizationGroupDto.AuthorizationGroupStoreResponse> registerAuthorizationGroup (@RequestBody @Valid AuthorizationGroupDto.AuthorizationGroupRequest request) {
    	AuthorizationGroupCommand command = request.toCommand();
    	AuthorizationGroupInfo userInfo = authorizationGroupFacade.registerAuthorizationGroup(command);
		AuthorizationGroupDto.AuthorizationGroupStoreResponse response = new AuthorizationGroupDto.AuthorizationGroupStoreResponse(userInfo);
        return  CommonResponse.success(response);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "idOrGroupCode", value = "권한 그룹 일련번호 또는  그룹 코드", required = true, dataType = "String", paramType = "path"),
    })
    @GetMapping("/{idOrGroupCode}")
    public CommonResponse<AuthorizationGroupDto.AuthorizationGroupResponse> searchAuthorizationGroupInfo(@Valid @PathVariable String idOrGroupCode) {
    	AuthorizationGroupInfo authorizationGroupInfo;
    
        if (WiniString.isUuid(idOrGroupCode)) {
            UUID id = UUID.fromString(idOrGroupCode);
            authorizationGroupInfo = authorizationGroupFacade.searchAuthorizationGroup(id);
        } else {
            authorizationGroupInfo = authorizationGroupFacade.searchAuthorizationGroupByGroupCode(idOrGroupCode);
        }
        
		AuthorizationGroupDto.AuthorizationGroupResponse response = new AuthorizationGroupDto.AuthorizationGroupResponse(authorizationGroupInfo);
        return CommonResponse.success(response);
    }
    
    @GetMapping
    public CommonResponse<List<AuthorizationGroupDto.AuthorizationGroupResponse>> searchAllAuthorizationGroup() {
        List<AuthorizationGroupInfo> authorizationInfoList = authorizationGroupFacade.searchAllAuthorizationGroup();
        List<AuthorizationGroupDto.AuthorizationGroupResponse> response = authorizationInfoList.stream()
                .map(AuthorizationGroupDto.AuthorizationGroupResponse::new)
                .collect(Collectors.toList());
        return CommonResponse.success(response);
    }
    
    @GetMapping("/tree")
    public CommonResponse<List<AuthorizationGroupDto.AuthorizationGroupTreeResponse>> searchAllAuthorizationGroupTree() {
        List<AuthorizationGroupInfo.AuthorizationGroupTreeInfo> authorizationInfoList = authorizationGroupFacade.searchAllAuthorizationGroupTree();
        List<AuthorizationGroupDto.AuthorizationGroupTreeResponse> response = authorizationInfoList.stream()
                .map(AuthorizationGroupDto.AuthorizationGroupTreeResponse::new)
                .collect(Collectors.toList());
        return CommonResponse.success(response);
    }

//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "groupId", value = "그룹 ID", required = true, dataType = "String", paramType = "path"),
//            @ApiImplicitParam(name = "request", value = "수정할 필드", required = true, dataType = "object", paramType = "formData")
//    })
    @RequestMapping(value = "{id}", method = RequestMethod.PATCH)
    public CommonResponse<AuthorizationGroupDto.AuthorizationGroupResponse> modifyAuthorizationGroup(@Valid @PathVariable UUID id, @RequestBody AuthorizationGroupDto.AuthorizationGroupRequest request) {
        AuthorizationGroupCommand command = request.toCommand();
        AuthorizationGroupInfo authorizationGroupInfo = authorizationGroupFacade.modifyAuthorizationGroup(id, command);
		AuthorizationGroupDto.AuthorizationGroupResponse response = new AuthorizationGroupDto.AuthorizationGroupResponse(authorizationGroupInfo);
        return CommonResponse.success(response);
    }

//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "id", value = "권한 그룹 일련번호", required = true, dataType = "Long", paramType = "path")
//    })
    @DeleteMapping("/{id}")
    public CommonResponse<String> removeAuthorizationGroup(@Valid @PathVariable UUID id) {
        authorizationGroupFacade.removeAuthorizationGroup(id);
        return CommonResponse.success("OK");
    }
}
