package com.winitech.common.infrastructure.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.winitech.common.application.common.CommonAuthorizationGroupPermissionFacade;
import com.winitech.common.domain.common.CommonAuthorizationGroupPermissionCommand;
import com.winitech.common.domain.common.CommonAuthorizationGroupUserCommand;
import com.winitech.common.domain.common.CommonUserCommand;
import com.winitech.common.interfaces.inboundAdapter.eventListener.CommonAuthorizationGroupPermissionEventHandler;
import com.winitech.common.interfaces.inboundAdapter.eventListener.CommonAuthorizationGroupPermissionSyncMessage;
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
 * └ CommonAuthorizationGroupPermissionEventHandlerImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-25 09:23
 **/
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnExpression("${winitech.cdc.common.common-authorization.use-consumer:false}")
public class CommonAuthorizationGroupPermissionEventHandlerImpl implements CommonAuthorizationGroupPermissionEventHandler {
	private final CommonAuthorizationGroupPermissionFacade commonAuthorizationGroupPermissionFacade;

	private final CommonAuthorizationDataChangeEventProducer commonAuthorizationDataChangeEventProducer;
	
	@Override
	@KafkaListener(id = "${winitech.service-name}-service-common-authorization-group-permission", topics = "${winitech.cdc.common.topic.prefix}common.v1.common-authorization-group-permission", groupId = "common-authorization-group-permission-${winitech.service-name}-consumer-group")
	public void wheneverCommonAuthorizationGroupPermissionSynced(String message, Acknowledgment ack) throws JsonProcessingException {
		try {
			log.debug("|  SV  | RECV    | TOPIC | wini.cdc.common.v1.common-authorization-group-permission - " + message);
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			CommonAuthorizationGroupPermissionSyncMessage cdcMessage = objectMapper.readValue(message, CommonAuthorizationGroupPermissionSyncMessage.class);

			if (cdcMessage.getOp() == CommonAuthorizationGroupPermissionSyncMessage.OPERATION.s) {
				List<UUID> authorizationGroupIdList = cdcMessage.getAuthorizationGroupIdList();
				List<CommonAuthorizationGroupPermissionCommand> commandList = cdcMessage.getAuthorizationGroupPermissionInfoList()
						.stream()
						.map(info -> CommonAuthorizationGroupPermissionCommand.builder()
								.authorizationGroupId(info.getAuthorizationGroupId())
								.menuId(info.getMenuId())
								.groupCode(info.getGroupCode())
								.selectStatus(info.getSelectStatus())
								.insertStatus(info.getInsertStatus())
								.updateStatus(info.getUpdateStatus())
								.deleteStatus(info.getDeleteStatus())
								.printStatus(info.getPrintStatus())
								.downStatus(info.getDownStatus())
								.manageStatus(info.getManageStatus())
								.custom1Status(info.getCustom1Status())
								.custom2Status(info.getCustom2Status())
								.custom3Status(info.getCustom3Status())
								.build())
						.collect(Collectors.toList());

				commonAuthorizationGroupPermissionFacade.syncAll(authorizationGroupIdList, commandList);

				commonAuthorizationDataChangeEventProducer.authorizationDataChanged("AuthorizationGroupPermission changed");

				log.debug("|  SV  | CMD      | SUC     | " + commandList);
			} else {
				log.error("|  SV  | CMD      | ERR     | 잘못된 파라메터 타입입니다.");
			}
		} finally {
			// ack-mode=manual인데 여기서 커밋하지 않으면 오프셋이 영원히 전진하지 않아,
			// 컨슈머(api-gateway 등)가 재기동될 때마다 토픽 전체가 재생되어 이후에 반영된
			// 최신 권한 데이터를 과거의 낡은 스냅샷으로 덮어써버리는 문제가 있었다(2026-09-17 발견).
			if (ack != null) {
				ack.acknowledge();
			}
		}
	}
}
