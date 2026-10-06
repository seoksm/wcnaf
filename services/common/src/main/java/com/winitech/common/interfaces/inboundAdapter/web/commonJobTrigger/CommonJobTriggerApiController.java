package com.winitech.common.interfaces.inboundAdapter.web.commonJobTrigger;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.domain.commonJobTrigger.CommonJobTrigger;
import com.winitech.common.exception.EntityNotFoundException;
import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.common.response.CommonResponse;
import com.winitech.common.application.commonJobTrigger.CommonJobTriggerFacade;
import com.winitech.common.domain.commonJobTrigger.CommonJobTriggerCommand;
import com.winitech.common.domain.commonJobTrigger.CommonJobTriggerInfo;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.domain.commonJobTrigger
 * └ CommonJobTrigger.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:45
 **/
@RestController
@RequiredArgsConstructor
@Validated
@Api(tags = { "CommonJobTrigger 서비스 API" }, value = "CommonJobTrigger 서비스 API", description = "Api Controller")
@ForceDefaultTenant
@RequestMapping("/api/v1/{serviceName}")
public class CommonJobTriggerApiController {
	private final CommonJobTriggerFacade commonJobTriggerFacade;

	/**
	 * CommonJobTrigger 전체 조회
	 * @return
	 */
	@ApiOperation(tags = { "CommonJobTrigger 서비스 API" }, value = "CommonJobTrigger 조회", nickname = "searchAllCommonJobTrigger", notes = "CommonJobTrigger를 조회합니다.", response = CommonJobTriggerDto.CommonJobTriggerResponse.class, responseContainer = "List")
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobTriggerDto.CommonJobTriggerResponse.class, responseContainer = "List")})
	@RequestMapping(method = RequestMethod.GET, value = "/commonJob/{commonJobId}/trigger/all", produces = { "application/json" })
	public CommonResponse<List<CommonJobTriggerDto.CommonJobTriggerResponse>> searchAllCommonJobTrigger(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@PathVariable("commonJobId") UUID commonJobId
	) {
		List<CommonJobTriggerInfo> allCommonJobTrigger = commonJobTriggerFacade.getAllCommonJobTriggerByCommonJobId(commonJobId);

		List<CommonJobTriggerDto.CommonJobTriggerResponse> response = allCommonJobTrigger
				.stream()
				.map(CommonJobTriggerDto.CommonJobTriggerResponse::new)
				.collect(Collectors.toList());

		return CommonResponse.success(response);
	}

	/**
	 * CommonJobTrigger 페이지 조회
	 * @return
	 */
	@ApiOperation(tags = { "CommonJobTrigger 서비스 API" }, value = "CommonJobTrigger 조회", nickname = "searchAllCommonJobTrigger", notes = "CommonJobTrigger를 조회합니다.", response = CommonJobTriggerDto.CommonJobTriggerResponse.class, responseContainer = "List")
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobTriggerDto.CommonJobTriggerResponse.class, responseContainer = "List")})
	@RequestMapping(method = RequestMethod.GET, value = "/commonJob/{commonJobId}/trigger", produces = { "application/json" })
	public CommonResponse<List<CommonJobTriggerDto.CommonJobTriggerResponse>> searchAllCommonJobTrigger(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@PathVariable("commonJobId") UUID commonJobId,
			@ApiParam(value = "페이지 번호 (0부터 시작)") @Valid @RequestParam(value = "page", required = false) Integer page,
			@ApiParam(value = "페이지 사이즈") @Valid @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@ApiParam(value = "상태") @Valid @RequestParam(value = "ENABLE", required = false) CommonJobTrigger.Status status,
			@ApiParam(value = "검색 타입", allowableValues = "name") @Valid @RequestParam(value = "searchType", required = false) String searchType,
			@ApiParam(value = "검색어") @Valid @RequestParam(value = "searchKeyword", required = false) String searchKeyword
	) {
		WiniPageInfo<CommonJobTriggerInfo> commonJobTriggerPageInfo = commonJobTriggerFacade.searchCommonJobTriggerPage(commonJobId, page, pageSize, status, searchType, searchKeyword);

		List<CommonJobTriggerDto.CommonJobTriggerResponse> response = commonJobTriggerPageInfo
				.stream()
				.map(CommonJobTriggerDto.CommonJobTriggerResponse::new)
				.collect(Collectors.toList());

		return CommonResponse.success(response, commonJobTriggerPageInfo);
	}

	/**
	 * CommonJobTrigger 상세 조회
	 * @param commonJobTriggerId
	 * @return
	 */
	@ApiOperation(tags = { "CommonJobTrigger 서비스 API" }, value = "CommonJobTrigger 상세 조회", nickname = "searchCommonJobTrigger", notes = "CommonJobTrigger의 상세정보를 조회합니다.", response = CommonJobTriggerDto.CommonJobTriggerResponse.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobTriggerDto.CommonJobTriggerResponse.class)})
	@RequestMapping(method = RequestMethod.GET, value = "/commonJob/{commonJobId}/trigger/{commonJobTriggerId}", produces = { "application/json" })
	public CommonResponse<CommonJobTriggerDto.CommonJobTriggerResponse> searchCommonJobTrigger(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@PathVariable("commonJobId") UUID commonJobId,
			@ApiParam(value = "ID", required = true) @PathVariable("commonJobTriggerId") UUID commonJobTriggerId
	) {
		CommonJobTriggerInfo commonJobTriggerInfo = commonJobTriggerFacade.searchCommonJobTriggerById(commonJobTriggerId);
		
		if (commonJobTriggerInfo.getCommonJob() == null || !commonJobTriggerInfo.getCommonJob().getId().equals(commonJobId)) {
			throw new EntityNotFoundException();
		}

		CommonJobTriggerDto.CommonJobTriggerResponse response = new CommonJobTriggerDto.CommonJobTriggerResponse(commonJobTriggerInfo);

		return CommonResponse.success(response);
	}

	/**
	 * CommonJobTrigger 등록
	 * @param commonJobTriggerDto
	 * @return
	 */
	@ApiOperation(tags = { "CommonJobTrigger 서비스 API" }, value = "CommonJobTrigger 등록", nickname = "registerCommonJobTrigger", notes = "단일 CommonJobTrigger 항목을 등록합니다.", response = CommonJobTriggerDto.CommonJobTriggerStoreResponse.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobTriggerDto.CommonJobTriggerStoreResponse.class)})
	@RequestMapping(method = RequestMethod.POST, value = "/commonJob/{commonJobId}/trigger", produces = { "application/json" }, consumes = { "application/json" })
	public CommonResponse<CommonJobTriggerDto.CommonJobTriggerStoreResponse> registerCommonJobTrigger(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@PathVariable("commonJobId") UUID commonJobId,
			@ApiParam(value = "") @Valid @RequestBody(required = false) CommonJobTriggerDto.CommonJobTriggerRegisterRequest commonJobTriggerDto
	) {
		CommonJobTriggerCommand.RegisterRequestCommand command = commonJobTriggerDto.toCommand(commonJobId);

		CommonJobTriggerInfo commonJobTriggerInfo = commonJobTriggerFacade.registerCommonJobTrigger(command);

		CommonJobTriggerDto.CommonJobTriggerStoreResponse response = new CommonJobTriggerDto.CommonJobTriggerStoreResponse(commonJobTriggerInfo.getId());

		return CommonResponse.success(response);
	}

	/**
	 * CommonJobTrigger 수정
	 * @param commonJobTriggerId
	 * @param commonJobTriggerDto
	 * @return
	 */
	@ApiOperation(tags = { "CommonJobTrigger 서비스 API" }, value = "CommonJobTrigger 수정", nickname = "modifyCommonJobTrigger", notes = "단일 CommonJobTrigger 항목을 수정합니다.", response = CommonJobTriggerDto.CommonJobTriggerStoreResponse.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobTriggerDto.CommonJobTriggerStoreResponse.class)})
	@RequestMapping(method = RequestMethod.PATCH, value = "/commonJob/{commonJobId}/trigger/{commonJobTriggerId}", produces = { "application/json" }, consumes = { "application/json" })
	public CommonResponse<CommonJobTriggerDto.CommonJobTriggerStoreResponse> modifyCommonJobTrigger(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@PathVariable("commonJobId") UUID commonJobId,
			@PathVariable("commonJobTriggerId") UUID commonJobTriggerId, 
			@ApiParam(value = "") @Valid @RequestBody(required = false) CommonJobTriggerDto.CommonJobTriggerModifyRequest commonJobTriggerDto
	) {
		CommonJobTriggerCommand.ModifyRequestCommand command = commonJobTriggerDto.toCommand(commonJobId);

		CommonJobTriggerInfo commonJobTriggerInfo = commonJobTriggerFacade.modifyCommonJobTrigger(commonJobTriggerId, command);
		
		CommonJobTriggerDto.CommonJobTriggerStoreResponse response = new CommonJobTriggerDto.CommonJobTriggerStoreResponse(commonJobTriggerInfo.getId());

		return CommonResponse.success(response);
	}

	/**
	 * CommonJobTrigger 삭제
	 * @param serviceName
	 * @param commonJobId
	 * @param commonJobTriggerId
	 * @return
	 */
	@ApiOperation(tags = { "CommonJobTrigger 서비스 API" }, value = "CommonJobTrigger 삭제", nickname = "removeCommonJobTrigger", notes = "CommonJobTrigger 삭제합니다. (soft delete)", response = String.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
	@RequestMapping(method = RequestMethod.DELETE, value = "/commonJob/{commonJobId}/trigger/{commonJobTriggerId}", produces = { "application/json" })
	public CommonResponse<String> removeCommonJobTrigger(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@PathVariable("commonJobId") UUID commonJobId,
			@PathVariable("commonJobTriggerId") UUID commonJobTriggerId
	) {
		commonJobTriggerFacade.removeCommonJobTrigger(commonJobId, commonJobTriggerId);

		return CommonResponse.success("OK");
	}

	/**
	 * CRON 표현식 점검
	 * @param serviceName
	 * @param commonJobId
	 * @param cronExpression
	 * @return
	 */
	@ApiOperation(tags = { "CommonJobTrigger 서비스 API" }, value = "CRON 표현식 점검", nickname = "checkCronExpression", notes = "CRON 표현식을 점검합니다.", response = String.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
	@RequestMapping(method = RequestMethod.GET, value = "/commonJob/{commonJobId}/trigger/check-cron-Expression", produces = { "application/json" })
	public CommonResponse<CommonJobTriggerDto.CheckCronExpressionResult> checkCronExpression(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@PathVariable("commonJobId") UUID commonJobId,
			@RequestParam(value = "cronExpression") String cronExpression
	) {
		CommonJobTriggerInfo.CheckCronExpression info = commonJobTriggerFacade.checkCronExpression(cronExpression);
		CommonJobTriggerDto.CheckCronExpressionResult result = new CommonJobTriggerDto.CheckCronExpressionResult(info);

		return CommonResponse.success(result);
	}
}
