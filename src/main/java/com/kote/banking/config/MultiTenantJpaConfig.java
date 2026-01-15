package com.kote.banking.config;

import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
@Configuration
@EnableTransactionManagement
public class MultiTenantJpaConfig {
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, MultiTenantConnectionProvider provider, CurrentTenantIdentifierResolver resolver){
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        Map<String, Object> properties = new HashMap<>();
        properties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, provider);
        properties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, resolver);
        properties.put(Environment.HBM2DDL_AUTO, "update");
        properties.put("hibernate.multiTenancy", "SCHEMA");
        em.setPackagesToScan("com.kote.banking");
        em.setJpaPropertyMap(properties);
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean factoryBean) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(factoryBean.getObject());
        return jpaTransactionManager;
    }
}
