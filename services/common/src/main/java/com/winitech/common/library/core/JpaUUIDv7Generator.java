package com.winitech.common.library.core;

import com.winitech.common.library.WiniCom;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.tuple.ValueGenerator;

import java.io.Serializable;
import java.util.UUID;

/**
 * com.winitech.common.library
 * └ JpaUUIDv7Generator.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/11/06
 **/
public class JpaUUIDv7Generator implements IdentifierGenerator, ValueGenerator<UUID> {
	@Override
	public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
		return WiniCom.getUUIDv7();
	}

	@Override
	public boolean supportsJdbcBatchInserts() {
		return true;
	}

	@Override
	public UUID generateValue(Session session, Object owner) {
		return WiniCom.getUUIDv7();
	}
}

// Hibernate 6 이상에서는 아래와 같이 편리하게 사용 가능
//package com.winitech.common.annotation;
//
//import org.hibernate.annotations.GenericGenerator;
//
//import javax.persistence.GeneratedValue;
//import javax.persistence.Id;
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//import java.lang.annotation.Target;
//
//import static java.lang.annotation.ElementType.FIELD;
//import static java.lang.annotation.ElementType.METHOD;
//
///**
// * com.winitech.common.annotation
// * └ WiniId.java
// * @author : coding (클라우드팀)
// * @see : None
// * @since : 2024/11/07
// **/
//// @IdGeneratorType( com.winitech.common.library.core.JpaUUIDv7Generator.class ) // hibernate v6 이상을 사용하면 사용 가능
//@Retention(RetentionPolicy.RUNTIME)
//@Target({ FIELD, METHOD })
//public @interface WiniId {
//}
