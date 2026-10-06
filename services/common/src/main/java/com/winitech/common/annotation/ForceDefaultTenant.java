package com.winitech.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * com.winitech.common.annotation
 * └ ForceDefaultTenant.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-08-21 18:03
 **/
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ForceDefaultTenant {
}
