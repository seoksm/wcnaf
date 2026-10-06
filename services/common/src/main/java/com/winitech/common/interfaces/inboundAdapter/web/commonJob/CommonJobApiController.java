package com.winitech.common.interfaces.inboundAdapter.web.commonJob;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.domain.commonJob.CommonJob;
import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.common.response.CommonResponse;
import com.winitech.common.application.commonJob.CommonJobFacade;
import com.winitech.common.domain.commonJob.CommonJobCommand;
import com.winitech.common.domain.commonJob.CommonJobInfo;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.domain.commonJob
 * └ CommonJob.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:37
 **/
@RestController
@RequiredArgsConstructor
@Validated
@Api(tags = { "CommonJob 서비스 API" }, value = "CommonJob 서비스 API", description = "Api Controller")
@ForceDefaultTenant
@RequestMapping("/api/v1/{serviceName}")
public class CommonJobApiController {
	private final CommonJobFacade commonJobFacade;
	private final SchedulerFactoryBean schedulerFactoryBean;

	/**
	 * CommonJob 전체 조회
	 * @return
	 */
	@ApiOperation(tags = { "CommonJob 서비스 API" }, value = "CommonJob 조회", nickname = "searchAllCommonJob", notes = "CommonJob를 조회합니다.", response = CommonJobDto.CommonJobResponse.class, responseContainer = "List")
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobDto.CommonJobResponse.class, responseContainer = "List")})
	@RequestMapping(method = RequestMethod.GET, value = "/commonJob/all", produces = { "application/json" })
	public CommonResponse<List<CommonJobDto.CommonJobResponse>> searchAllCommonJob(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName
	) {
		List<CommonJobInfo> allCommonJob = commonJobFacade.getAllCommonJob();

		allCommonJob.sort(Comparator.comparing(CommonJobInfo::getId));

		//allCommonJob.sort(Comparator.comparing(CommonJobInfo::getId)
		//		.thenComparing(CommonJobInfo::getName, Comparator.nullsFirst(Comparator.naturalOrder())));

		List<CommonJobDto.CommonJobResponse> response = allCommonJob
				.stream()
				.map(CommonJobDto.CommonJobResponse::new)
				.collect(Collectors.toList());

		return CommonResponse.success(response);
	}
	
	/**
	 * CommonJob 페이지 조회
	 * @return
	 */
	@ApiOperation(tags = { "CommonJob 서비스 API" }, value = "CommonJob 조회", nickname = "searchAllCommonJob", notes = "CommonJob를 조회합니다.", response = CommonJobDto.CommonJobResponse.class, responseContainer = "List")
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobDto.CommonJobResponse.class, responseContainer = "List")})
	@RequestMapping(method = RequestMethod.GET, value = "/commonJobGroup/{commonJobGroupId}/job", produces = { "application/json" })
	public CommonResponse<List<CommonJobDto.CommonJobResponse>> searchAllCommonJob(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@PathVariable("commonJobGroupId") UUID commonJobGroupId,
			@ApiParam(value = "페이지 번호 (0부터 시작)") @Valid @RequestParam(value = "page", required = false) Integer page,
			@ApiParam(value = "페이지 사이즈") @Valid @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@ApiParam(value = "검색 타입", allowableValues = "name") @Valid @RequestParam(value = "searchType", required = false) String searchType,
			@ApiParam(value = "검색어") @Valid @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
			@ApiParam(value = "작업 유형", allowableValues = "JAVA,SQL") @Valid @RequestParam(value = "searchJobType", required = false) CommonJob.JobType searchJobType,
			@ApiParam(value = "상태") @Valid @RequestParam(value = "searchStatus", required = false) CommonJob.Status searchStatus
	) {
		WiniPageInfo<CommonJobInfo> commonJobPageInfo = commonJobFacade.searchCommonJobPage(commonJobGroupId, page, pageSize, searchType, searchKeyword, searchStatus, searchJobType);

		List<CommonJobDto.CommonJobResponse> response = commonJobPageInfo
				.stream()
				.map(CommonJobDto.CommonJobResponse::new)
				.collect(Collectors.toList());

		return CommonResponse.success(response, commonJobPageInfo);
	}
	
	/**
	 * CommonJob 상세 조회
	 * @param commonJobId
	 * @return
	 */
	@ApiOperation(tags = { "CommonJob 서비스 API" }, value = "CommonJob 상세 조회", nickname = "searchCommonJob", notes = "CommonJob의 상세정보를 조회합니다.", response = CommonJobDto.CommonJobResponse.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobDto.CommonJobResponse.class)})
	@RequestMapping(method = RequestMethod.GET, value = "/commonJob/{commonJobId}", produces = { "application/json" })
	public CommonResponse<CommonJobDto.CommonJobResponse> searchCommonJob(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@ApiParam(value = "ID", required = true) @PathVariable("commonJobId") UUID commonJobId
	) {
		CommonJobInfo commonJobInfo = commonJobFacade.searchCommonJobById(commonJobId);

		CommonJobDto.CommonJobResponse response = new CommonJobDto.CommonJobResponse(commonJobInfo);

		return CommonResponse.success(response);
	}

	/**
	 * CommonJob 등록
	 * @param commonJobDto
	 * @return
	 */
	@ApiOperation(tags = { "CommonJob 서비스 API" }, value = "CommonJob 등록", nickname = "registerCommonJob", notes = "단일 CommonJob 항목을 등록합니다.", response = CommonJobDto.CommonJobStoreResponse.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobDto.CommonJobStoreResponse.class)})
	@RequestMapping(method = RequestMethod.POST, value = "/commonJob", produces = { "application/json" }, consumes = { "application/json" })
	public CommonResponse<CommonJobDto.CommonJobStoreResponse> registerCommonJob(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@ApiParam(value = "") @Valid @RequestBody(required = false) CommonJobDto.CommonJobRegisterRequest commonJobDto
	) {
		CommonJobCommand.RegisterRequestCommand command = commonJobDto.toCommand();

		CommonJobInfo commonJobInfo = commonJobFacade.registerCommonJob(command);

		CommonJobDto.CommonJobStoreResponse response = new CommonJobDto.CommonJobStoreResponse(commonJobInfo.getId().toString());

		return CommonResponse.success(response);
	}

	/**
	 * CommonJob 수정
	 * @param commonJobId
	 * @param commonJobDto
	 * @return
	 */
	@ApiOperation(tags = { "CommonJob 서비스 API" }, value = "CommonJob 수정", nickname = "modifyCommonJob", notes = "단일 CommonJob 항목을 수정합니다.", response = CommonJobDto.CommonJobStoreResponse.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = CommonJobDto.CommonJobStoreResponse.class)})
	@RequestMapping(method = RequestMethod.PATCH, value = "/commonJob/{commonJobId}", produces = { "application/json" }, consumes = { "application/json" })
	public CommonResponse<CommonJobDto.CommonJobStoreResponse> modifyCommonJob(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@PathVariable("commonJobId") UUID commonJobId, @ApiParam(value = "") @Valid @RequestBody(required = false) CommonJobDto.CommonJobModifyRequest commonJobDto
	) {
		CommonJobCommand.ModifyRequestCommand command = commonJobDto.toCommand();

		CommonJobInfo commonJobInfo = commonJobFacade.modifyCommonJob(commonJobId, command);

		CommonJobDto.CommonJobStoreResponse response = new CommonJobDto.CommonJobStoreResponse(commonJobInfo.getId().toString());

		return CommonResponse.success(response);
	}

	/**
	 * CommonJob 삭제
	 * @param serviceName
	 * @param commonJobId
	 * @return
	 */
	@ApiOperation(tags = { "CommonJob 서비스 API" }, value = "CommonJob 삭제", nickname = "removeCommonJob", notes = "CommonJob 삭제합니다. (soft delete)", response = String.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
	@RequestMapping(method = RequestMethod.DELETE, value = "/commonJob/{commonJobId}", produces = { "application/json" })
	public CommonResponse<String> removeCommonJob(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@PathVariable("commonJobId") UUID commonJobId
	) {
		commonJobFacade.removeCommonJob(commonJobId);

		return CommonResponse.success("OK");
	}

	/**
	 * CommonJob와 Quartz 동기화
	 * @param serviceName
	 * @return
	 */
	@ApiOperation(tags = { "CommonJob 서비스 API" }, value = "CommonJob 동기화", nickname = "syncJobs", notes = "CommonJob과 Quartz을 동기화합니다.)", response = String.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
	@RequestMapping(method = RequestMethod.GET, value = "/commonJob/all/sync", produces = { "application/json" })
	public CommonResponse<String> syncJobs(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName
	) {
		commonJobFacade.syncAllCommonJob(false);

		return CommonResponse.success("OK");
	}

	/**
	 * 작업 수동 트리거
	 * @param serviceName
	 * @param commonJobId
	 * @return
	 */
	@ApiOperation(tags = { "CommonJob 서비스 API" }, value = "CommonJob 수동 트리거", nickname = "triggerCommonJob", notes = "CommonJob을 수동으로 실행시킵니다.", response = String.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
	@RequestMapping(method = RequestMethod.POST, value = "/commonJob/{commonJobId}/fire", produces = { "application/json" })
	public CommonResponse<String> fireCommonJob(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@PathVariable("commonJobId") UUID commonJobId
	) {
		commonJobFacade.triggerCommonJob(commonJobId);

		return CommonResponse.success("OK");
	}

	/**
	 * 작업 취소
	 */
	@ApiOperation(tags = { "CommonJob 서비스 API" }, value = "CommonJob 취소", nickname = "cancelCommonJob", notes = "CommonJob을 취소합니다.", response = String.class)
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
	@RequestMapping(method = RequestMethod.POST, value = "/commonJob/{commonJobId}/cancel", produces = { "application/json" })
	public CommonResponse<String> cancelCommonJob(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@PathVariable("commonJobId") UUID commonJobId
	) {
		commonJobFacade.cancelCommonJob(commonJobId);

		return CommonResponse.success("OK");
	}
}
