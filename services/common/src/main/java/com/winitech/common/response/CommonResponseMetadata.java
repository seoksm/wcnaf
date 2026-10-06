package com.winitech.common.response;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * com.winitech.common.response
 * └ CommonResponseMetadata.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/11/12
 **/
@JsonSerialize(using = CommonResponseMetadata.CommonResponseMetadataSerializer.class)
public class CommonResponseMetadata {
	private Map<String, Object> metadata;

	private CommonResponseMetadata() {
		metadata = new HashMap<>();
	}
	
	public CommonResponseMetadata put(String key, Object value) {
		metadata.put(key, value);
		return this;
	}
	
	public Object get(String key) {
		return metadata.get(key);
	}
	
	public Set<String> keySet() {
		return metadata.keySet();
	}
	
	public static CommonResponseMetadata ofPaging(int page, long totalRecords, long pageSize) {
		CommonResponseMetadata commonResponseMetadata = new CommonResponseMetadata();
		commonResponseMetadata.put("page", page);
		commonResponseMetadata.put("totalRecords", totalRecords);
		commonResponseMetadata.put("pageSize", pageSize);
		return commonResponseMetadata;
	}
	
	public static CommonResponseMetadata ofMap(Map<String, Object> map) {
		CommonResponseMetadata commonResponseMetadata = new CommonResponseMetadata();
		commonResponseMetadata.metadata.putAll(map);
		return new CommonResponseMetadata();
	}
	
	public static CommonResponseMetadata create() {
		return new CommonResponseMetadata();
	}
	
	public static class CommonResponseMetadataSerializer extends StdSerializer<CommonResponseMetadata> {
		public CommonResponseMetadataSerializer() {
			this(null);
		}
		
		public CommonResponseMetadataSerializer(Class<CommonResponseMetadata> t) {
			super(t);
		}

		@Override
		public void serialize(CommonResponseMetadata value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			gen.writeStartObject();

			if (! value.keySet().contains("timestamp")) {
				// timestamp 필드가 없으면 현재 시간을 기록
				// 생성할 때 현재시간을 기록하지 않고 serializer에서 처리하는 이유는 metadata가 지정되지 않을 때에도 
				// 현재시간을 json의 metadata에 기록하기 위함 
				gen.writeObjectField("timestamp", OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS));
			}
			
			for (String key : value.keySet()) {
				gen.writeObjectField(key, value.get(key));
			}
			
			gen.writeEndObject();
		}
	}
}
