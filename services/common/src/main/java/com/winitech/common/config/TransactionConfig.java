package com.winitech.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

import java.lang.reflect.Method;

@Configuration@ConditionalOnProperty(
        value = "winitech.database.all-exception-rollback",
        havingValue = "true",
        matchIfMissing = false)
public class TransactionConfig {
    @Bean(name = "winiTransactionAttributeSource")
    @Primary    public TransactionAttributeSource winiTransactionAttributeSource() {
        return new AnnotationTransactionAttributeSource() {

            @Override
            protected TransactionAttribute findTransactionAttribute(Method method) {
                return updateRollbackRule(super.findTransactionAttribute(method));
            }

            @Override
            protected TransactionAttribute findTransactionAttribute(Class<?> clazz) {
                return updateRollbackRule(super.findTransactionAttribute(clazz));
            }

            private TransactionAttribute updateRollbackRule(TransactionAttribute attr) {
                if (attr instanceof RuleBasedTransactionAttribute) {
                    RuleBasedTransactionAttribute rbta = (RuleBasedTransactionAttribute) attr;
                    rbta.getRollbackRules().add(new RollbackRuleAttribute(Exception.class));
                }
                return attr;
            }
        };
    }
}
