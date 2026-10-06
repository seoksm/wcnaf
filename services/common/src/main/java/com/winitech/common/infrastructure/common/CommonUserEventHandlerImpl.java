package com.winitech.common.infrastructure.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.winitech.common.application.commonFile.CommonUserFacade;
import com.winitech.common.config.properties.MultitenantProperties;
import com.winitech.common.domain.common.CommonUser;
import com.winitech.common.domain.common.CommonUserCommand;
import com.winitech.common.exception.IllegalStatusException;
import com.winitech.common.interfaces.inboundAdapter.eventListener.CommonUserChangedMessage;
import com.winitech.common.interfaces.inboundAdapter.eventListener.CommonUserEventHandler;
import com.winitech.common.library.core.CurrentTenantHolder;
import com.winitech.common.library.core.MultitenantDatabaseConnectionPoolManager;
import com.winitech.common.library.core.MultitenantUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonUserEventHandlerImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-21 13:12
 **/
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnExpression("${winitech.cdc.common.common-user.use-consumer:false}")
public class CommonUserEventHandlerImpl implements CommonUserEventHandler {
	private final CommonUserFacade commonUserFacade;
	
	@Override
	@KafkaListener(topics = "${winitech.cdc.common.topic.prefix}common.v1.common-user", groupId = "common-user-${winitech.service-name}-consumer-group")
	public void wheneverCommonUserUpdated(String message) throws JsonProcessingException {
		log.debug("|  SV  | RECV    | TOPIC | wini.cdc.common.v1.common-user - " + message);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		CommonUserChangedMessage cdcMessage = objectMapper.readValue(message, CommonUserChangedMessage.class);
		CommonUserCommand commonUserCommand;
		if(cdcMessage.getOp() == CommonUserChangedMessage.OPERATION.c || cdcMessage.getOp() == CommonUserChangedMessage.OPERATION.u) {
			CommonUser.Status status;
			
			try {
				if (cdcMessage.getAfter().getSystemStatus() == null) {
					status = null;
				} else {
					status = CommonUser.Status.valueOf(cdcMessage.getAfter().getSystemStatus());
				}
			} catch (IllegalArgumentException e) {
				log.debug("Unknown system status: " + cdcMessage.getAfter().getSystemStatus());
				status = null;
			}
			
			commonUserCommand = CommonUserCommand.builder()
					.id(cdcMessage.getAfter().getId())
					.username(cdcMessage.getAfter().getUsername())
					.firstName(cdcMessage.getAfter().getFirstName())
					.lastName(cdcMessage.getAfter().getLastName())
					.fullName(cdcMessage.getAfter().getFullName())
					.email(cdcMessage.getAfter().getEmail())
					.phoneNumber(cdcMessage.getAfter().getPhoneNumber())
					.employeeNo(cdcMessage.getAfter().getEmployeeNo())
					.dutyName(cdcMessage.getAfter().getDutyName())
					.departmentName(cdcMessage.getAfter().getDepartmentName())
					.status(status)
					.build();
			
			commonUserFacade.saveCommonUser(commonUserCommand);
			
			log.debug("|  SV  | CMD      | SUC     | " + commonUserCommand);
		} else if (cdcMessage.getOp() == CommonUserChangedMessage.OPERATION.d) {
			commonUserFacade.removeCommonUser(cdcMessage.getBefore().getId());

			log.debug("|  SV  | CMD      | SUC     | ");
		} else if (cdcMessage.getOp() == CommonUserChangedMessage.OPERATION.r) {
			// 조직에서 사용자 제거하는 멀티테넌트용 메시지로 여기서는 따로 처리하지 않습니다.

			log.debug("|  SV  | CMD      | SUC     | ");
		} else {
			log.error("|  SV  | CMD      | ERR     | 잘못된 파라메터 타입입니다.");
		}
	}
}
