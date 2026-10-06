package com.winitech.common.library.core;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * <pre>
 * com.winitech.common.library.core
 * └ OffsetDateTimeDeserializer.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-07-28 13:15
 **/
public class OffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {
	private final ZoneOffset systemTimeZoneOffset = ZoneId.systemDefault().getRules().getOffset(Instant.now());
	private final DateTimeFormatter yyyyMMddHHmmFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	private final DateTimeFormatter yyyyMMddHHmmssFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	@Override
	public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		String dateTimeString = p.getText();
		if (dateTimeString == null || dateTimeString.isEmpty()) {
			return null;
		}

		try {
			switch (dateTimeString.length()) {
				case 16: // "2025-07-28T13:15"
					return LocalDateTime.parse(dateTimeString.replace("T", " "), yyyyMMddHHmmFormatter).atOffset(systemTimeZoneOffset);
				case 19: // "2025-07-28T13:15:30"
					return LocalDateTime.parse(dateTimeString.replace("T", " "), yyyyMMddHHmmssFormatter).atOffset(systemTimeZoneOffset);
			}

			return OffsetDateTime.parse(dateTimeString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		} catch (DateTimeException e) {
			throw new IOException("Failed to parse OffsetDateTime: " + dateTimeString, e);
		}
	}
}
