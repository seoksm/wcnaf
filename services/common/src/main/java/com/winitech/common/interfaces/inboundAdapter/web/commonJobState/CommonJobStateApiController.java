package com.winitech.common.interfaces.inboundAdapter.web.commonJobState;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.exception.EntityNotFoundException;
import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.common.response.CommonResponse;
import com.winitech.common.application.commonJobState.CommonJobStateFacade;
import com.winitech.common.domain.commonJobState.CommonJobStateCommand;
import com.winitech.common.domain.commonJobState.CommonJobStateInfo;
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
 * com.winitech.system.domain.commonJobState
 * └ CommonJobState.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:47
 **/
@RestController
@RequiredArgsConstructor
@Validated
@Api(tags = { "CommonJobState 서비스 API" }, value = "CommonJobState 서비스 API", description = "Api Controller")
@ForceDefaultTenant
@RequestMapping("/api/v1/{serviceName}")
public class CommonJobStateApiController {
	private final CommonJobStateFacade commonJobStateFacade;


	/**
	 * CommonJobState 전체 조회
	 * @return
	 */
	@ApiOperation(tags = { "CommonJobState 서비스 API" }, value = "CommonJobState 조회", nickname = "searchAllCommonJobState", notes = "CommonJobState를 조회합니다.", response = CommonJobStateDto.CommonJobStateResponse.class, responseContainer = "List")
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobStateDto.CommonJobStateResponse.class, responseContainer = "List")})
	@RequestMapping(method = RequestMethod.GET, value = "/commonJobState/all", produces = { "application/json" })
	public CommonResponse<List<CommonJobStateDto.CommonJobStateResponse>> searchAllCommonJobState(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName
	) {
		List<CommonJobStateInfo> allCommonJobState = commonJobStateFacade.getAllCommonJobState();

		allCommonJobState.sort(Comparator.comparing(CommonJobStateInfo::getId));

		//allCommonJobState.sort(Comparator.comparing(CommonJobStateInfo::getId)
		//		.thenComparing(CommonJobStateInfo::getName, Comparator.nullsFirst(Comparator.naturalOrder())));

		List<CommonJobStateDto.CommonJobStateResponse> response = allCommonJobState
				.stream()
				.map(CommonJobStateDto.CommonJobStateResponse::new)
				.collect(Collectors.toList());

		return CommonResponse.success(response);
	}
	
	/**
	 * CommonJobState 페이지 조회
	 * @return
	 */
	@ApiOperation(tags = { "CommonJobState 서비스 API" }, value = "CommonJobState 조회", nickname = "searchAllCommonJobState", notes = "CommonJobState를 조회합니다.", response = CommonJobStateDto.CommonJobStateResponse.class, responseContainer = "List")
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobStateDto.CommonJobStateResponse.class, responseContainer = "List")})
	@RequestMapping(method = RequestMethod.GET, value = "/commonJobState", produces = { "application/json" })
	public CommonResponse<List<CommonJobStateDto.CommonJobStateResponse>> searchAllCommonJobState(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@ApiParam(value = "페이지 번호 (0부터 시작)") @Valid @RequestParam(value = "page", required = false) Integer page,
			@ApiParam(value = "페이지 사이즈") @Valid @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@ApiParam(value = "검색 타입", allowableValues = "name,code") @Valid @RequestParam(value = "searchType", required = false) String searchType,
			@ApiParam(value = "검색어") @Valid @RequestParam(value = "searchKeyword", required = false) String searchKeyword
	) {
		WiniPageInfo<CommonJobStateInfo> commonJobStatePageInfo = commonJobStateFacade.searchCommonJobStatePage(page, pageSize, searchType, searchKeyword);

		List<CommonJobStateDto.CommonJobStateResponse> response = commonJobStatePageInfo
				.stream()
				.map(CommonJobStateDto.CommonJobStateResponse::new)
				.collect(Collectors.toList());

		return CommonResponse.success(response, commonJobStatePageInfo);
	}

	/**
	 * CommonJobState 상세 조회
	 * @param commonJobId
	 * @return
	 */
	@ApiOperation(tags = { "CommonJobState 서비스 API" }, value = "CommonJobState 상세 조회", nickname = "searchCommonJobState", notes = "CommonJobState의 상세정보를 조회합니다.", response = CommonJobStateDto.CommonJobStateResponse.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobStateDto.CommonJobStateResponse.class)})
	@RequestMapping(method = RequestMethod.GET, value = "/commonJob/{commonJobId}/state", produces = { "application/json" })
	public CommonResponse<CommonJobStateDto.CommonJobStateResponse> searchCommonJobStateByCommonJobId(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@ApiParam(value = "ID", required = true) @PathVariable("commonJobId") UUID commonJobId
	) {
		try {
			CommonJobStateInfo commonJobStateInfo = commonJobStateFacade.searchCommonJobStateByCommonJobId(commonJobId);

			CommonJobStateDto.CommonJobStateResponse response = new CommonJobStateDto.CommonJobStateResponse(commonJobStateInfo);
		
			return CommonResponse.success(response);
		} catch (EntityNotFoundException e) {
			return CommonResponse.success(new CommonJobStateDto.CommonJobStateResponse());
		}
	}

	/**
	 * CommonJobState 상세 조회
	 * @param commonJobStateId
	 * @return
	 */
	@ApiOperation(tags = { "CommonJobState 서비스 API" }, value = "CommonJobState 상세 조회", nickname = "searchCommonJobState", notes = "CommonJobState의 상세정보를 조회합니다.", response = CommonJobStateDto.CommonJobStateResponse.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobStateDto.CommonJobStateResponse.class)})
	@RequestMapping(method = RequestMethod.GET, value = "/commonJobState/{commonJobStateId}", produces = { "application/json" })
	public CommonResponse<CommonJobStateDto.CommonJobStateResponse> searchCommonJobState(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@ApiParam(value = "ID", required = true) @PathVariable("commonJobStateId") UUID commonJobStateId
	) {
		CommonJobStateInfo commonJobStateInfo = commonJobStateFacade.searchCommonJobStateById(commonJobStateId);

		CommonJobStateDto.CommonJobStateResponse response = new CommonJobStateDto.CommonJobStateResponse(commonJobStateInfo);

		return CommonResponse.success(response);
	}

	/**
	 * CommonJobState 등록
	 * @param commonJobStateDto
	 * @return
	 */
	@ApiOperation(tags = { "CommonJobState 서비스 API" }, value = "CommonJobState 등록", nickname = "registerCommonJobState", notes = "단일 CommonJobState 항목을 등록합니다.", response = CommonJobStateDto.CommonJobStateStoreResponse.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobStateDto.CommonJobStateStoreResponse.class)})
	@RequestMapping(method = RequestMethod.POST, value = "/commonJobState", produces = { "application/json" }, consumes = { "application/json" })
	public CommonResponse<CommonJobStateDto.CommonJobStateStoreResponse> registerCommonJobState(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@ApiParam(value = "") @Valid @RequestBody(required = false) CommonJobStateDto.CommonJobStateRegisterRequest commonJobStateDto
	) {
		CommonJobStateCommand.RegisterRequestCommand command = commonJobStateDto.toCommand();

		CommonJobStateInfo commonJobStateInfo = commonJobStateFacade.registerCommonJobState(command);

		CommonJobStateDto.CommonJobStateStoreResponse response = new CommonJobStateDto.CommonJobStateStoreResponse(commonJobStateInfo.getId());

		return CommonResponse.success(response);
	}

	/**
	 * CommonJobState 수정
	 * @param commonJobStateId
	 * @param commonJobStateDto
	 * @return
	 */
	@ApiOperation(tags = { "CommonJobState 서비스 API" }, value = "CommonJobState 수정", nickname = "modifyCommonJobState", notes = "단일 CommonJobState 항목을 수정합니다.", response = CommonJobStateDto.CommonJobStateStoreResponse.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobStateDto.CommonJobStateStoreResponse.class)})
	@RequestMapping(method = RequestMethod.PATCH, value = "/commonJobState/{commonJobStateId}", produces = { "application/json" }, consumes = { "application/json" })
	public CommonResponse<CommonJobStateDto.CommonJobStateStoreResponse> modifyCommonJobState(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@PathVariable("commonJobStateId") UUID commonJobStateId, @ApiParam(value = "") @Valid @RequestBody(required = false) CommonJobStateDto.CommonJobStateModifyRequest commonJobStateDto
	) {
		CommonJobStateCommand.ModifyRequestCommand command = commonJobStateDto.toCommand();

		CommonJobStateInfo commonJobStateInfo = commonJobStateFacade.modifyCommonJobState(commonJobStateId, command);

		CommonJobStateDto.CommonJobStateStoreResponse response = new CommonJobStateDto.CommonJobStateStoreResponse(commonJobStateInfo.getId());

		return CommonResponse.success(response);
	}

	@ApiOperation(tags = { "CommonJobState 서비스 API" }, value = "CommonJobState 삭제", nickname = "removeCommonJobState", notes = "CommonJobState 삭제합니다. (soft delete)", response = String.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
	@RequestMapping(method = RequestMethod.DELETE, value = "/commonJobState/{commonJobStateId}", produces = { "application/json" })
	public CommonResponse<String> removeCommonJobState(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@PathVariable("commonJobStateId") UUID commonJobStateId
	) {
		commonJobStateFacade.removeCommonJobState(commonJobStateId);

		return CommonResponse.success("OK");
	}
}
