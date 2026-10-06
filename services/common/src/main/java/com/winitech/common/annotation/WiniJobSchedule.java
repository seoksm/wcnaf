package com.winitech.common.annotation;

import com.winitech.common.domain.commonJobTrigger.CommonJobTrigger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * com.winitech.common.annotation
 * └ WiniJobSchedule.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-13 13:28
 **/
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface WiniJobSchedule {
	/**
	 * Job 이름
	 * @return
	 */
	String value();

	/**
	 * Job 그룹 이름
	 * @return
	 */
	String jobGroupName() default "DEFAULT";
	
	/**
	 * 비고
	 * @return
	 */
	String remark() default "";

	/**
	 * 트리거 활성화 여부
	 * @return
	 */
	boolean triggerEnabled() default false;
	
	/**
	 * 트리거 유형
	 * @return
	 */
	CommonJobTrigger.TriggerType triggerType() default CommonJobTrigger.TriggerType.SECONDS;

	/**
	 * 트리거 유형이 SECONDS 일때 초단위 주기
	 * @return
	 */
	long triggerSeconds() default 60L;

	/**
	 * 트리거 유형이 CRON 일때 CRON 표현식 
	 * @return
	 */
	String triggerCron() default "";
}
