package com.winitech.common.library.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * <pre>
 * com.winitech.common.library.core
 * └ OffsetDateTimeSerializer.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-07-28 12:46
 **/
public class OffsetDateTimeSerializer extends JsonSerializer<OffsetDateTime> {
	private final ZoneOffset systemTimeZoneOffset = ZoneId.systemDefault().getRules().getOffset(Instant.now());
	
	private final DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

	@Override
	public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if (value == null) {
			gen.writeNull();
		} else {
			String str;

			if (value.getOffset().getTotalSeconds() == systemTimeZoneOffset.getTotalSeconds()) {
				// 시스템의 타임존과 동일한 경우
				str = value.format(localDateTimeFormatter);
			} else {
				// 시스템의 타임존과 다른 경우 시스템 타임존으로 변환 후 리턴
				str = value.withOffsetSameInstant(systemTimeZoneOffset).format(localDateTimeFormatter); 
			}
			
			gen.writeString(str);
		}
	}
}
