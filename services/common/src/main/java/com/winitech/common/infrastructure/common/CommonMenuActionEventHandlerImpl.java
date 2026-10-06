package com.winitech.common.infrastructure.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.winitech.common.application.common.CommonMenuActionFacade;
import com.winitech.common.domain.common.CommonAuthorizationGroupUserCommand;
import com.winitech.common.domain.common.CommonMenuAction;
import com.winitech.common.domain.common.CommonMenuActionCommand;
import com.winitech.common.interfaces.inboundAdapter.eventListener.CommonMenuActionSyncMessage;
import com.winitech.common.interfaces.inboundAdapter.eventListener.CommonMenuActionEventHandler;
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
 * └ CommonMenuActionEventHandlerImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-25 09:22
 **/
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnExpression("${winitech.cdc.common.common-authorization.use-consumer:false}")
public class CommonMenuActionEventHandlerImpl implements CommonMenuActionEventHandler {
	private final CommonMenuActionFacade commonMenuActionFacade;

	private final CommonAuthorizationDataChangeEventProducer commonAuthorizationDataChangeEventProducer;
	
	@Override
	@KafkaListener(id = "${winitech.service-name}-service-common-menu-action", topics = "${winitech.cdc.common.topic.prefix}common.v1.common-menu-action", groupId = "common-menu-action-${winitech.service-name}-consumer-group")
	public void wheneverCommonMenuActionSynced(String message, Acknowledgment ack) throws JsonProcessingException {
		try {
			log.debug("|  SV  | RECV    | TOPIC | wini.cdc.common.v1.common-menu-action - " + message);
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			CommonMenuActionSyncMessage cdcMessage = objectMapper.readValue(message, CommonMenuActionSyncMessage.class);

			if (cdcMessage.getOp() == CommonMenuActionSyncMessage.OPERATION.s) {
				List<UUID> programIdList = cdcMessage.getProgramIdList();
				List<CommonMenuActionCommand> commandList = cdcMessage.getMenuActionInfoList()
						.stream()
						.map(info -> CommonMenuActionCommand.builder()
								.menuId(info.getMenuId())
								.programId(info.getProgramId())
								.programCode(info.getProgramCode())
								.actionType(info.getActionType())
								.authType(info.getAuthType())
								.uri(info.getUri())
								.build())
						.collect(Collectors.toList());

				commonMenuActionFacade.syncAll(programIdList, commandList);

				commonAuthorizationDataChangeEventProducer.authorizationDataChanged("MenuAction changed");

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
