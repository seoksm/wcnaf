package com.winitech.common.interfaces.inboundAdapter.web;

import com.winitech.common.domain.common.CommonPublicKeyInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.interfaces.inboundAdapter.web
 * └ CommonSecurityDto.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-31 09:16
 **/
@NoArgsConstructor
public class CommonSecurityDto {
	@ApiModel("Generated Public Private Key Pair Response")
	@Getter
	@Builder
	public static class GenerateKeyPairResponse {
		@ApiModelProperty(value = "공개키", required = true) private String publicKey;
	}
}
