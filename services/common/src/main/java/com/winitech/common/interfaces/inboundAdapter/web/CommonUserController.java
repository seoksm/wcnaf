package com.winitech.common.interfaces.inboundAdapter.web;

import com.winitech.common.application.commonFile.CommonUserFacade;
import com.winitech.common.domain.common.CommonUser;
import com.winitech.common.domain.common.CommonUserInfo;
import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.common.response.CommonResponse;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.common.interfaces.inboundAdapter.web
 * └ CommonUserController.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-14 14:05
 **/
@Api(tags = "Common User")
@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/{serviceName}/commonUser")
public class CommonUserController {
	private final CommonUserFacade commonUserFacade;

	@ApiOperation(value = "사용자 목록 불러오기")
	@GetMapping
	public CommonResponse<List<CommonUserDto>> searchAllUser(
			@ApiParam(value = "서비스 이름", required = true, defaultValue = "system") @PathVariable("serviceName") String serviceName,
			@ApiParam(value = "페이지 번호 (0부터 시작)") @Valid @RequestParam(value = "page", required = false) Integer page,
			@ApiParam(value = "페이지 사이즈") @Valid @RequestParam(value = "pageSize", required = false) Integer pageSize,
			@ApiParam(value = "검색 타입", allowableValues = "FULL_NAME,DEPARTMENT_NAME,DUTY_NAME,EMAIL,EMPLOYEE_NO") @Valid @RequestParam(value = "searchType", required = false) String searchType,
			@ApiParam(value = "검색어") @Valid @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
			@ApiParam(value = "사용자 상태") @Valid @RequestParam(value = "status", required = false) String status
	) {
		CommonUser.Status userStatus = null;
		if (status != null) {
			try {
				userStatus = Enum.valueOf(CommonUser.Status.class, status);
			} catch (IllegalArgumentException e) {
				log.debug("잘못된 사용자 상태 값: {}", status);
			}
		}

		WiniPageInfo<CommonUserInfo> userInfoPage = commonUserFacade.searchAllUser(page, pageSize, searchType, searchKeyword, userStatus);
		
		return CommonResponse.success(userInfoPage.getContent().stream()
				.map(CommonUserDto::new)
				.collect(Collectors.toList()), userInfoPage);
	}
}
