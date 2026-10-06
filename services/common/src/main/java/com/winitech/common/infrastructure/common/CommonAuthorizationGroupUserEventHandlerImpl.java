package com.winitech.common.infrastructure.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.winitech.common.application.common.CommonAuthorizationGroupUserFacade;
import com.winitech.common.domain.common.CommonAuthorizationGroupUserCommand;
import com.winitech.common.domain.common.CommonUserCommand;
import com.winitech.common.interfaces.inboundAdapter.eventListener.CommonAuthorizationGroupUserEventHandler;
import com.winitech.common.interfaces.inboundAdapter.eventListener.CommonAuthorizationGroupUserSyncMessage;
import com.winitech.common.interfaces.outboundAdapter.eventProducer.CommonAuthorizationDataChangeEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonAuthorizationGroupUserEventHandlerImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-25 09:23
 **/
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnExpression("${winitech.cdc.common.common-authorization.use-consumer:false}")
public class CommonAuthorizationGroupUserEventHandlerImpl implements CommonAuthorizationGroupUserEventHandler {
	private final CommonAuthorizationGroupUserFacade commonAuthorizationGroupUserFacade;

	private final CommonAuthorizationDataChangeEventProducer commonAuthorizationDataChangeEventProducer;
	
	@Override
	@KafkaListener(id = "${winitech.service-name}-service-common-authorization-group-user", topics = "${winitech.cdc.common.topic.prefix}common.v1.common-authorization-group-user", groupId = "common-authorization-group-user-${winitech.service-name}-consumer-group")
	public void wheneverCommonAuthorizationGroupUserSynced(String message, Acknowledgment ack) throws JsonProcessingException {
		try {
			log.debug("|  SV  | RECV    | TOPIC | wini.cdc.common.v1.common-authorization-group-user - " + message);
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			CommonAuthorizationGroupUserSyncMessage cdcMessage = objectMapper.readValue(message, CommonAuthorizationGroupUserSyncMessage.class);

			if (cdcMessage.getOp() == CommonAuthorizationGroupUserSyncMessage.OPERATION.s) {
				List<UUID> authorizationGroupIdList = cdcMessage.getAuthorizationGroupIdList();
				List<CommonAuthorizationGroupUserCommand> commandList = cdcMessage.getAuthorizationGroupUserInfoList()
						.stream()
						.map(info -> CommonAuthorizationGroupUserCommand.builder()
								.authorizationGroupId(info.getAuthorizationGroupId())
								.userId(info.getUserId())
								.build())
						.collect(Collectors.toList());

				commonAuthorizationGroupUserFacade.syncAll(authorizationGroupIdList, commandList);

				commonAuthorizationDataChangeEventProducer.authorizationDataChanged("AuthorizationGroupUser changed");

				log.debug("|  SV  | CMD      | SUC     | " + commandList);
			} else {
				log.error("|  SV  | CMD      | ERR     | 잘못된 파라메터 타입입니다.");
			}
		} finally {
			// ack-mode=manual인데 커밋을 하지 않으면 컨슈머 재기동 시마다 토픽 전체가 재생되어
			// 최신 데이터가 과거의 낡은 스냅샷으로 덮어써지는 문제가 있었다(2026-09-17 발견).
			if (ack != null) {
				ack.acknowledge();
			}
		}
	}
}
