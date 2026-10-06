package com.winitech.common.interfaces.inboundAdapter.web;

import com.winitech.common.domain.common.CommonPublicKeyInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.interfaces.inboundAdapter.web
 * └ CommonQueryDto.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-08 15:57
 **/
@NoArgsConstructor
public class CommonQueryDto {
	@ApiModel("CUD query Response")
	@Getter
	@ToString
	@NoArgsConstructor
	public static class CudQueryResponse {
		private int affectedRowCount;
		
		public CudQueryResponse(int affectedRowCount) {
			this.affectedRowCount = affectedRowCount;
		}
	}
}
