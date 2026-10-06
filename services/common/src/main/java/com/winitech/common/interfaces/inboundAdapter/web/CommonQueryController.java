package com.winitech.common.interfaces.inboundAdapter.web;

import com.winitech.common.infrastructure.common.AbstractSqlSessionDao;
import com.winitech.common.infrastructure.common.CommonSqlSessionDao;
import com.winitech.common.response.CommonResponse;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.common.interfaces.inboundAdapter.web
 * └ CommonQueryController.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-08 14:32
 **/
@Api(tags = "Common Query")
@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/{serviceName}/crud")
@ConditionalOnExpression("${winitech.mybatis.enabled:false}")
public class CommonQueryController {
	private final CommonSqlSessionDao commonSqlSessionDao;

	@GetMapping("/{mapperName}/{queryId}")
	public CommonResponse selectCrudData(@PathVariable("serviceName") String serviceName,
										 @PathVariable("mapperName") String mapperName,
										 @PathVariable("queryId") String queryId,
										 HttpServletRequest req,
										 HttpServletResponse res) {
		String fullQueryId = "com.winitech." + serviceName + ".infrastructure." + mapperName + "." + queryId;
		
		Map<String, String> map = req.getParameterMap().entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						e -> e.getValue() != null && e.getValue().length > 0 ? e.getValue()[0] : null
				));
		
		List<Object> result = commonSqlSessionDao.selectList(fullQueryId, map);

		return CommonResponse.success(result);
	}

	@PostMapping("/{mapperName}/{queryId}")
	public CommonResponse insertCrudData(@PathVariable("serviceName") String serviceName,
										 @PathVariable("mapperName") String mapperName,
										 @PathVariable("queryId") String queryId,
										 @RequestBody Map map,
										 HttpServletRequest req,
										 HttpServletResponse res) {
		String fullQueryId = "com.winitech." + serviceName + ".infrastructure." + mapperName + "." + queryId;

		int affectedRowCount = commonSqlSessionDao.insert(fullQueryId, map);
		CommonQueryDto.CudQueryResponse result = new CommonQueryDto.CudQueryResponse(affectedRowCount);

		return CommonResponse.success(result);
	}

	@PatchMapping("/{mapperName}/{queryId}")
	public CommonResponse updateCrudData(@PathVariable("serviceName") String serviceName,
										 @PathVariable("mapperName") String mapperName,
										 @PathVariable("queryId") String queryId,
										 @RequestBody Map map,
										 HttpServletRequest req,
										 HttpServletResponse res) {
		String fullQueryId = "com.winitech." + serviceName + ".infrastructure." + mapperName + "." + queryId;

		int affectedRowCount = commonSqlSessionDao.update(fullQueryId, map);
		CommonQueryDto.CudQueryResponse result = new CommonQueryDto.CudQueryResponse(affectedRowCount);

		return CommonResponse.success(result);
	}

	@DeleteMapping("/{mapperName}/{queryId}")
	public CommonResponse deleteCrudData(@PathVariable("serviceName") String serviceName,
										 @PathVariable("mapperName") String mapperName,
										 @PathVariable("queryId") String queryId,
										 @RequestBody Map map,
										 HttpServletRequest req,
										 HttpServletResponse res) {
		String fullQueryId = "com.winitech." + serviceName + ".infrastructure." + mapperName + "." + queryId;

		int affectedRowCount = commonSqlSessionDao.delete(fullQueryId, map);
		CommonQueryDto.CudQueryResponse result = new CommonQueryDto.CudQueryResponse(affectedRowCount);

		return CommonResponse.success(result);
	}
}
