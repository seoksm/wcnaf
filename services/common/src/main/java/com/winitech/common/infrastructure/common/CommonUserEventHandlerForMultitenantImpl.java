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
import com.winitech.common.interfaces.inboundAdapter.CommonUserEventHandlerForMultitenant;
import com.winitech.common.interfaces.inboundAdapter.eventListener.CommonUserChangedMessage;
import com.winitech.common.interfaces.inboundAdapter.eventListener.CommonUserEventHandler;
import com.winitech.common.library.core.CurrentTenantHolder;
import com.winitech.common.library.core.MultitenantUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonUserEventHandlerForMultitenantImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-09-19 10:25
 **/
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnExpression("${winitech.multitenant.cdc.common.common-user.use-consumer:false} && '${winitech.multitenant.type:}'=='database'")
public class CommonUserEventHandlerForMultitenantImpl implements CommonUserEventHandlerForMultitenant {
	private final DataSource dataSource;

	private final CommonUserFacade commonUserFacade;

	private final MultitenantProperties multitenantProperties;

	@Override
	@KafkaListener(topics = "${winitech.cdc.common.topic.prefix}common.v1.common-user", groupId = "common-user-multitenant-${winitech.service-name}-consumer-group")
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

			if (multitenantProperties.isMultitenant() && multitenantProperties.getType() == MultitenantProperties.MultitenantType.DATABASE) {
				// 데이터베이스 방식 멀티테넌시인 경우에만 조직 정보 처리

				// 1안. 테넌트별로 JPA로 처리
				// 아래와 같이 테넌트를 임시로 전환할 수 있지만, 이렇게 할 경우 테넌트마다 Connection Pool이 각각 생성되므로,
				// 테넌트가 많아질수록 Connection Pool이 많아져 자원 낭비가 심해집니다.
				/*CurrentTenantHolder currentTenantHolder = CurrentTenantHolder.get();

				try {
					String[] organizationIds = cdcMessage.getAfter().getOrganizationIds();
					String[] organizationCodes = cdcMessage.getAfter().getOrganizationCodes();

					for (int i = 0; i < organizationIds.length; i++) {
						// 테넌트 임시 변경
						CurrentTenantHolder.set(UUID.fromString(organizationIds[i]), organizationCodes[i]);
						
						log.info("Set CurrentTenantHolder to organizationId: {}, organizationCode: {}", organizationIds[i], organizationCodes[i]);

						commonUserFacade.saveCommonUser(commonUserCommand);
					}
				} finally {
					// 원래 테넌트로 복원
					CurrentTenantHolder.set(currentTenantHolder.getOrganizationId(), currentTenantHolder.getTenantId());

					log.info("Restored CurrentTenantHolder to organizationId: {}, organizationCode: {}", currentTenantHolder.getOrganizationId(), currentTenantHolder.getTenantId());
				}*/

				// 1-a안. 테넌트별로 JPA로 처리 - MultitenantUtil.runAsTenant() 사용
				// 아래와 같이 테넌트를 임시로 전환할 수 있지만, 이렇게 할 경우 테넌트마다 Connection Pool이 각각 생성되므로,
				// 테넌트가 많아질수록 Connection Pool이 많아져 자원 낭비가 심해집니다.
				String[] organizationIds = cdcMessage.getAfter().getOrganizationIds();
				String[] organizationCodes = cdcMessage.getAfter().getOrganizationCodes();

				for (int i = 0; i < organizationIds.length; i++) {
					// 테넌트 임시 변경
					MultitenantUtil.runAsTenant(UUID.fromString(organizationIds[i]), organizationCodes[i], () -> {
						commonUserFacade.saveCommonUser(commonUserCommand);
					});
				}
			}

			log.debug("|  SV  | CMD      | SUC     | " + commonUserCommand);
		} else if (cdcMessage.getOp() == CommonUserChangedMessage.OPERATION.d) {
			if (multitenantProperties.isMultitenant() && multitenantProperties.getType() == MultitenantProperties.MultitenantType.DATABASE) {
				// 데이터베이스 방식 멀티테넌시인 경우에만 조직 정보 처리	

				// 1안. 테넌트별로 JPA로 처리
				// 아래와 같이 테넌트를 임시로 전환할 수 있지만, 이렇게 할 경우 테넌트마다 Connection Pool이 각각 생성되므로,
				// 테넌트가 많아질수록 Connection Pool이 많아져 자원 낭비가 심해집니다.
				CurrentTenantHolder currentTenantHolder = CurrentTenantHolder.get();

				try {
					String[] organizationIds = cdcMessage.getAfter().getOrganizationIds();
					String[] organizationCodes = cdcMessage.getAfter().getOrganizationCodes();

					for (int i = 0; i < organizationIds.length; i++) {
						// 테넌트 임시 변경
						CurrentTenantHolder.set(UUID.fromString(organizationIds[i]), organizationCodes[i]);

						commonUserFacade.removeCommonUser(cdcMessage.getBefore().getId());
					}
				} finally {
					// 원래 테넌트로 복원
					CurrentTenantHolder.set(currentTenantHolder.getOrganizationId(), currentTenantHolder.getTenantId());
				}
			}

			log.debug("|  SV  | CMD      | SUC     | ");
		} else if (cdcMessage.getOp() == CommonUserChangedMessage.OPERATION.r) {
			// 조직에서 사용자 제거

			if (multitenantProperties.isMultitenant() && multitenantProperties.getType() == MultitenantProperties.MultitenantType.DATABASE) {
				// 데이터베이스 방식 멀티테넌시인 경우에만 조직 정보 처리	

				// 2안. Connection Pool을 만들지 않고 JDBC로 직접 처리
				CommonUserChangedMessage.UserOrganizationInfo userOrganizationInfo = cdcMessage.getUserOrganizationInfo();

				if (userOrganizationInfo != null) {
					try {
						JdbcTemplate jdbcTemplate = MultitenantUtil.getJdbcTemplate(dataSource, userOrganizationInfo.getOrganizationCode());

						// 삭제할 경우 JOIN시 사용자 정보가 없어서 안 나오는 경우가 있으므로, 삭제하지 않고 사용자 상태를 DISABLE로만 변경
						jdbcTemplate.update("UPDATE common_user SET status = 'DISABLE' WHERE id = ?", UUID.fromString(userOrganizationInfo.getId()));
					} catch (SQLException e) {
						throw new IllegalStatusException("Failed to get DataSource for tenant: " + userOrganizationInfo.getOrganizationCode());
					}
				}
			}

			log.debug("|  SV  | CMD      | SUC     | ");
		} else {
			log.error("|  SV  | CMD      | ERR     | 잘못된 파라메터 타입입니다.");
		}
	}
}
