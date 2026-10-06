package com.winitech.common.interfaces.inboundAdapter.web.commonJobGroup;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.domain.commonJobGroup.CommonJobGroup;
import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.common.response.CommonResponse;
import com.winitech.common.application.commonJobGroup.CommonJobGroupFacade;
import com.winitech.common.domain.commonJobGroup.CommonJobGroupCommand;
import com.winitech.common.domain.commonJobGroup.CommonJobGroupInfo;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.domain.commonJobGroup
 * └ CommonJobGroup.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:36
 **/
@RestController
@RequiredArgsConstructor
@Validated
@Api(tags = { "CommonJobGroup 서비스 API" }, value = "CommonJobGroup 서비스 API", description = "Api Controller")
@ForceDefaultTenant
@RequestMapping("/api/v1/{serviceName}")
public class CommonJobGroupApiController {
	private final CommonJobGroupFacade commonJobGroupFacade;


	/**
	 * CommonJobGroup 전체 조회
	 * @return
	 */
	@ApiOperation(tags = { "CommonJobGroup 서비스 API" }, value = "CommonJobGroup 조회", nickname = "searchAllCommonJobGroup", notes = "CommonJobGroup를 조회합니다.", response = CommonJobGroupDto.CommonJobGroupResponse.class, responseContainer = "List")
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobGroupDto.CommonJobGroupResponse.class, responseContainer = "List")})
	@RequestMapping(method = RequestMethod.GET, value = "/commonJobGroup/all", produces = { "application/json" })
	public CommonResponse<List<CommonJobGroupDto.CommonJobGroupResponse>> searchAllCommonJobGroup(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName
	) {
		List<CommonJobGroupInfo> allCommonJobGroup = commonJobGroupFacade.getAllCommonJobGroup();

		allCommonJobGroup.sort(Comparator.comparing(CommonJobGroupInfo::getId));

		//allCommonJobGroup.sort(Comparator.comparing(CommonJobGroupInfo::getId)
		//		.thenComparing(CommonJobGroupInfo::getName, Comparator.nullsFirst(Comparator.naturalOrder())));

		List<CommonJobGroupDto.CommonJobGroupResponse> response = allCommonJobGroup
				.stream()
				.map(CommonJobGroupDto.CommonJobGroupResponse::new)
				.collect(Collectors.toList());

		return CommonResponse.success(response);
	}
	
	/**
	 * CommonJobGroup 페이지 조회
	 * @return
	 */
	@ApiOperation(tags = { "CommonJobGroup 서비스 API" }, value = "CommonJobGroup 조회", nickname = "searchAllCommonJobGroup", notes = "CommonJobGroup를 조회합니다.", response = CommonJobGroupDto.CommonJobGroupResponse.class, responseContainer = "List")
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobGroupDto.CommonJobGroupResponse.class, responseContainer = "List")})
	@RequestMapping(method = RequestMethod.GET, value = "/commonJobGroup", produces = { "application/json" })
	public CommonResponse<List<CommonJobGroupDto.CommonJobGroupResponse>> searchAllCommonJobGroup(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@ApiParam(value = "페이지 번호 (0부터 시작)") @Valid @RequestParam(value = "page", required = false) Integer page,
			@ApiParam(value = "페이지 사이즈") @Valid @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@ApiParam(value = "검색 타입", allowableValues = "NAME") @Valid @RequestParam(value = "searchType", required = false) String searchType,
			@ApiParam(value = "검색어") @Valid @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
			@ApiParam(value = "상태") @Valid @RequestParam(value = "searchStatus", required = false) CommonJobGroup.Status searchStatus
	) {
		WiniPageInfo<CommonJobGroupInfo> commonJobGroupPageInfo = commonJobGroupFacade.searchCommonJobGroupPage(page, pageSize, searchType, searchKeyword, searchStatus);

		List<CommonJobGroupDto.CommonJobGroupResponse> response = commonJobGroupPageInfo
				.stream()
				.map(CommonJobGroupDto.CommonJobGroupResponse::new)
				.collect(Collectors.toList());

		return CommonResponse.success(response, commonJobGroupPageInfo);
	}
	
	/**
	 * CommonJobGroup 상세 조회
	 * @param commonJobGroupId
	 * @return
	 */
	@ApiOperation(tags = { "CommonJobGroup 서비스 API" }, value = "CommonJobGroup 상세 조회", nickname = "searchCommonJobGroup", notes = "CommonJobGroup의 상세정보를 조회합니다.", response = CommonJobGroupDto.CommonJobGroupResponse.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobGroupDto.CommonJobGroupResponse.class)})
	@RequestMapping(method = RequestMethod.GET, value = "/commonJobGroup/{commonJobGroupId}", produces = { "application/json" })
	public CommonResponse<CommonJobGroupDto.CommonJobGroupResponse> searchCommonJobGroup(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@ApiParam(value = "ID", required = true) @PathVariable("commonJobGroupId") UUID commonJobGroupId
	) {
		CommonJobGroupInfo commonJobGroupInfo = commonJobGroupFacade.searchCommonJobGroupById(commonJobGroupId);

		CommonJobGroupDto.CommonJobGroupResponse response = new CommonJobGroupDto.CommonJobGroupResponse(commonJobGroupInfo);

		return CommonResponse.success(response);
	}

	/**
	 * CommonJobGroup 등록
	 * @param commonJobGroupDto
	 * @return
	 */
	@ApiOperation(tags = { "CommonJobGroup 서비스 API" }, value = "CommonJobGroup 등록", nickname = "registerCommonJobGroup", notes = "단일 CommonJobGroup 항목을 등록합니다.", response = CommonJobGroupDto.CommonJobGroupStoreResponse.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobGroupDto.CommonJobGroupStoreResponse.class)})
	@RequestMapping(method = RequestMethod.POST, value = "/commonJobGroup", produces = { "application/json" }, consumes = { "application/json" })
	public CommonResponse<CommonJobGroupDto.CommonJobGroupStoreResponse> registerCommonJobGroup(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@ApiParam(value = "") @Valid @RequestBody(required = false) CommonJobGroupDto.CommonJobGroupRegisterRequest commonJobGroupDto
	) {
		CommonJobGroupCommand.RegisterRequestCommand command = commonJobGroupDto.toCommand();

		CommonJobGroupInfo commonJobGroupInfo = commonJobGroupFacade.registerCommonJobGroup(command);

		CommonJobGroupDto.CommonJobGroupStoreResponse response = new CommonJobGroupDto.CommonJobGroupStoreResponse(commonJobGroupInfo.getId());

		return CommonResponse.success(response);
	}

	/**
	 * CommonJobGroup 수정
	 * @param commonJobGroupId
	 * @param commonJobGroupDto
	 * @return
	 */
	@ApiOperation(tags = { "CommonJobGroup 서비스 API" }, value = "CommonJobGroup 수정", nickname = "modifyCommonJobGroup", notes = "단일 CommonJobGroup 항목을 수정합니다.", response = CommonJobGroupDto.CommonJobGroupStoreResponse.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobGroupDto.CommonJobGroupStoreResponse.class)})
	@RequestMapping(method = RequestMethod.PATCH, value = "/commonJobGroup/{commonJobGroupId}", produces = { "application/json" }, consumes = { "application/json" })
	public CommonResponse<CommonJobGroupDto.CommonJobGroupStoreResponse> modifyCommonJobGroup(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@PathVariable("commonJobGroupId") UUID commonJobGroupId, @ApiParam(value = "") @Valid @RequestBody(required = false) CommonJobGroupDto.CommonJobGroupModifyRequest commonJobGroupDto
	) {
		CommonJobGroupCommand.ModifyRequestCommand command = commonJobGroupDto.toCommand();

		CommonJobGroupInfo commonJobGroupInfo = commonJobGroupFacade.modifyCommonJobGroup(commonJobGroupId, command);

		CommonJobGroupDto.CommonJobGroupStoreResponse response = new CommonJobGroupDto.CommonJobGroupStoreResponse(commonJobGroupInfo.getId());

		return CommonResponse.success(response);
	}

	@ApiOperation(tags = { "CommonJobGroup 서비스 API" }, value = "CommonJobGroup 삭제", nickname = "removeCommonJobGroup", notes = "CommonJobGroup 삭제합니다. (soft delete)", response = String.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
	@RequestMapping(method = RequestMethod.DELETE, value = "/commonJobGroup/{commonJobGroupId}", produces = { "application/json" })
	public CommonResponse<String> removeCommonJobGroup(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@PathVariable("commonJobGroupId") UUID commonJobGroupId
	) {
		commonJobGroupFacade.removeCommonJobGroup(commonJobGroupId);

		return CommonResponse.success("OK");
	}
}
