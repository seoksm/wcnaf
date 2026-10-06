package com.winitech.common.interfaces.inboundAdapter.web.commonJobRun;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.common.response.CommonResponse;
import com.winitech.common.application.commonJobRun.CommonJobRunFacade;
import com.winitech.common.domain.commonJobRun.CommonJobRunCommand;
import com.winitech.common.domain.commonJobRun.CommonJobRunInfo;
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
 * com.winitech.system.domain.commonJobRun
 * └ CommonJobRun.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:48
 **/
@RestController
@RequiredArgsConstructor
@Validated
@Api(tags = { "CommonJobRun 서비스 API" }, value = "CommonJobRun 서비스 API", description = "Api Controller")
@ForceDefaultTenant
@RequestMapping("/api/v1/{serviceName}")
public class CommonJobRunApiController {
	private final CommonJobRunFacade commonJobRunFacade;


	/**
	 * CommonJobRun 전체 조회
	 * @return
	 */
	@ApiOperation(tags = { "CommonJobRun 서비스 API" }, value = "CommonJobRun 조회", nickname = "searchAllCommonJobRun", notes = "CommonJobRun를 조회합니다.", response = CommonJobRunDto.CommonJobRunResponse.class, responseContainer = "List")
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobRunDto.CommonJobRunResponse.class, responseContainer = "List")})
	@RequestMapping(method = RequestMethod.GET, value = "/commonJobRun/all", produces = { "application/json" })
	public CommonResponse<List<CommonJobRunDto.CommonJobRunResponse>> searchAllCommonJobRun(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName
	) {
		List<CommonJobRunInfo> allCommonJobRun = commonJobRunFacade.getAllCommonJobRun();

		allCommonJobRun.sort(Comparator.comparing(CommonJobRunInfo::getId));

		//allCommonJobRun.sort(Comparator.comparing(CommonJobRunInfo::getId)
		//		.thenComparing(CommonJobRunInfo::getName, Comparator.nullsFirst(Comparator.naturalOrder())));

		List<CommonJobRunDto.CommonJobRunResponse> response = allCommonJobRun
				.stream()
				.map(CommonJobRunDto.CommonJobRunResponse::new)
				.collect(Collectors.toList());

		return CommonResponse.success(response);
	}
	
	/**
	 * CommonJobRun 페이지 조회
	 * @return
	 */
	@ApiOperation(tags = { "CommonJobRun 서비스 API" }, value = "CommonJobRun 조회", nickname = "searchAllCommonJobRun", notes = "CommonJobRun를 조회합니다.", response = CommonJobRunDto.CommonJobRunResponse.class, responseContainer = "List")
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobRunDto.CommonJobRunResponse.class, responseContainer = "List")})
	@RequestMapping(method = RequestMethod.GET, value = "/commonJob/{commonJobId}/run", produces = { "application/json" })
	public CommonResponse<List<CommonJobRunDto.CommonJobRunResponse>> searchAllCommonJobRun(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@PathVariable("commonJobId") UUID commonJobId,
			CommonJobRunDto.CommonJobRunSearchRequest commonJobRunSearchRequest
//			@ApiParam(value = "페이지 번호 (0부터 시작)") @Valid @RequestParam(value = "page", required = false) Integer page,
//			@ApiParam(value = "페이지 사이즈") @Valid @RequestParam(value = "pageSize", required = false) Integer pageSize,
//			@ApiParam(value = "검색 타입", allowableValues = "name,code") @Valid @RequestParam(value = "searchType", required = false) String searchType,
//			@ApiParam(value = "검색어") @Valid @RequestParam(value = "searchKeyword", required = false) String searchKeyword
	) {
		WiniPageInfo<CommonJobRunInfo> commonJobRunPageInfo = commonJobRunFacade.searchCommonJobRunPage(commonJobRunSearchRequest.toCommand(commonJobId));

		List<CommonJobRunDto.CommonJobRunResponse> response = commonJobRunPageInfo
				.stream()
				.map(CommonJobRunDto.CommonJobRunResponse::new)
				.collect(Collectors.toList());

		return CommonResponse.success(response, commonJobRunPageInfo);
	}
	
	/**
	 * CommonJobRun 상세 조회
	 * @param commonJobRunId
	 * @return
	 */
	@ApiOperation(tags = { "CommonJobRun 서비스 API" }, value = "CommonJobRun 상세 조회", nickname = "searchCommonJobRun", notes = "CommonJobRun의 상세정보를 조회합니다.", response = CommonJobRunDto.CommonJobRunResponse.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobRunDto.CommonJobRunResponse.class)})
	@RequestMapping(method = RequestMethod.GET, value = "/commonJobRun/{commonJobRunId}", produces = { "application/json" })
	public CommonResponse<CommonJobRunDto.CommonJobRunResponse> searchCommonJobRun(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@ApiParam(value = "ID", required = true) @PathVariable("commonJobRunId") UUID commonJobRunId
	) {
		CommonJobRunInfo commonJobRunInfo = commonJobRunFacade.searchCommonJobRunById(commonJobRunId);

		CommonJobRunDto.CommonJobRunResponse response = new CommonJobRunDto.CommonJobRunResponse(commonJobRunInfo);

		return CommonResponse.success(response);
	}

	/**
	 * CommonJobRun 등록
	 * @param commonJobRunDto
	 * @return
	 */
	@ApiOperation(tags = { "CommonJobRun 서비스 API" }, value = "CommonJobRun 등록", nickname = "registerCommonJobRun", notes = "단일 CommonJobRun 항목을 등록합니다.", response = CommonJobRunDto.CommonJobRunStoreResponse.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobRunDto.CommonJobRunStoreResponse.class)})
	@RequestMapping(method = RequestMethod.POST, value = "/commonJobRun", produces = { "application/json" }, consumes = { "application/json" })
	public CommonResponse<CommonJobRunDto.CommonJobRunStoreResponse> registerCommonJobRun(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@ApiParam(value = "") @Valid @RequestBody(required = false) CommonJobRunDto.CommonJobRunRegisterRequest commonJobRunDto
	) {
		CommonJobRunCommand.RegisterRequestCommand command = commonJobRunDto.toCommand();

		CommonJobRunInfo commonJobRunInfo = commonJobRunFacade.registerCommonJobRun(command);

		CommonJobRunDto.CommonJobRunStoreResponse response = new CommonJobRunDto.CommonJobRunStoreResponse(commonJobRunInfo.getId());

		return CommonResponse.success(response);
	}

	/**
	 * CommonJobRun 수정
	 * @param commonJobRunId
	 * @param commonJobRunDto
	 * @return
	 */
	@ApiOperation(tags = { "CommonJobRun 서비스 API" }, value = "CommonJobRun 수정", nickname = "modifyCommonJobRun", notes = "단일 CommonJobRun 항목을 수정합니다.", response = CommonJobRunDto.CommonJobRunStoreResponse.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobRunDto.CommonJobRunStoreResponse.class)})
	@RequestMapping(method = RequestMethod.PATCH, value = "/commonJobRun/{commonJobRunId}", produces = { "application/json" }, consumes = { "application/json" })
	public CommonResponse<CommonJobRunDto.CommonJobRunStoreResponse> modifyCommonJobRun(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@PathVariable("commonJobRunId") UUID commonJobRunId, @ApiParam(value = "") @Valid @RequestBody(required = false) CommonJobRunDto.CommonJobRunModifyRequest commonJobRunDto
	) {
		CommonJobRunCommand.ModifyRequestCommand command = commonJobRunDto.toCommand();

		CommonJobRunInfo commonJobRunInfo = commonJobRunFacade.modifyCommonJobRun(commonJobRunId, command);

		CommonJobRunDto.CommonJobRunStoreResponse response = new CommonJobRunDto.CommonJobRunStoreResponse(commonJobRunInfo.getId());

		return CommonResponse.success(response);
	}

	@ApiOperation(tags = { "CommonJobRun 서비스 API" }, value = "CommonJobRun 삭제", nickname = "removeCommonJobRun", notes = "CommonJobRun 삭제합니다. (soft delete)", response = String.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
	@RequestMapping(method = RequestMethod.DELETE, value = "/commonJobRun/{commonJobRunId}", produces = { "application/json" })
	public CommonResponse<String> removeCommonJobRun(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@PathVariable("commonJobRunId") UUID commonJobRunId
	) {
		commonJobRunFacade.removeCommonJobRun(commonJobRunId);

		return CommonResponse.success("OK");
	}
}
