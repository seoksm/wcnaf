package com.winitech.common.application.commonJobTrigger;

import com.winitech.common.domain.commonJobTrigger.CommonJobTrigger;
import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.common.domain.commonJobTrigger.CommonJobTriggerCommand;
import com.winitech.common.domain.commonJobTrigger.CommonJobTriggerInfo;
import com.winitech.common.domain.commonJobTrigger.CommonJobTriggerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
/**
 * <pre>
 * com.winitech.system.domain.commonJobTrigger
 * └ CommonJobTrigger.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:45
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class CommonJobTriggerFacade {
	private final CommonJobTriggerService commonJobTriggerService;

	public CommonJobTriggerInfo registerCommonJobTrigger(CommonJobTriggerCommand.RegisterRequestCommand commonJobTriggerCommand) {
		return commonJobTriggerService.registerCommonJobTrigger(commonJobTriggerCommand);
	}

	public CommonJobTriggerInfo modifyCommonJobTrigger(UUID id, CommonJobTriggerCommand.ModifyRequestCommand commonJobTriggerCommand) {
		return commonJobTriggerService.modifyCommonJobTrigger(id, commonJobTriggerCommand);
	}

	public void removeCommonJobTrigger(UUID commonJobId, UUID commonJobTriggerId) {
		commonJobTriggerService.removeCommonJobTrigger(commonJobId, commonJobTriggerId);
	}

	public CommonJobTriggerInfo searchCommonJobTriggerById(UUID id) {
		return commonJobTriggerService.searchCommonJobTriggerById(id);
	}

	public List<CommonJobTriggerInfo> getAllCommonJobTriggerByCommonJobId(UUID commonJobId) {
		return commonJobTriggerService.getAllCommonJobTriggerByCommonJobId(commonJobId);
	}

	public WiniPageInfo<CommonJobTriggerInfo> searchCommonJobTriggerPage(UUID commonJobId, Integer page, Integer pageSize, CommonJobTrigger.Status status, String searchType, String searchKeyword) {
		return commonJobTriggerService.searchCommonJobTriggerPage(commonJobId, page, pageSize, status, searchType, searchKeyword);
	}

	public OffsetDateTime getLastPendingUpdateAt() {
		return commonJobTriggerService.getLastPendingUpdateAt();
	}

	public CommonJobTriggerInfo.CheckCronExpression checkCronExpression(String cronExpression) {
		boolean isValid = true;
		String errMsg = "";
		List<OffsetDateTime> exampleList = new ArrayList<>();

		try {
			CronExpression expr = new CronExpression(cronExpression);
			Date currentTime = new Date();
			
			for (int i = 0; i < 3; i++) {
				currentTime = expr.getNextValidTimeAfter(currentTime);

				if (currentTime == null) {
					break;
				}

				OffsetDateTime offsetDateTime = OffsetDateTime.ofInstant(currentTime.toInstant(), java.time.ZoneId.systemDefault()); 
				exampleList.add(offsetDateTime);
			}
		} catch (ParseException e) {
			isValid = false;
			errMsg = e.getMessage();
		}

		return new CommonJobTriggerInfo.CheckCronExpression(isValid, errMsg, exampleList);
	}
}
