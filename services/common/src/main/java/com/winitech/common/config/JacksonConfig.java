package com.winitech.common.config;

import com.winitech.common.library.core.OffsetDateTimeDeserializer;
import com.winitech.common.library.core.OffsetDateTimeSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.OffsetDateTime;

/**
 * <pre>
 * com.winitech.common.config
 * └ JacksonConfig.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-07-28 12:45
 **/
@Configuration
public class JacksonConfig {
	@Value("${winitech.config.use-k-timezone:false}")
	private boolean useKTimeZone;
	
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer customDateTime() {
		if (useKTimeZone) {
			return builder -> {
				builder.serializerByType(OffsetDateTime.class, new OffsetDateTimeSerializer());
				builder.deserializerByType(OffsetDateTime.class, new OffsetDateTimeDeserializer());
			};
		} else {
			return builder -> { };
		}
	}
}
