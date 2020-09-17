/*
 * #%L
 * UltraCommerce Database Starter
 * %%
 * Copyright (C) 2009 - 2016 Ultra Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.ultracommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.ultracommerce.org/commercial_license-1.1.txt)
 * shall apply.
 *
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Ultra Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package com.ultracommerce.autoconfigure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * @author Jeff Fischer
 */
@Configuration
@EnableConfigurationProperties(HSQLDBProperties.class)
@ConditionalOnProperty(prefix = "demo.database", name = "autoConfigEnabled", matchIfMissing = true)
@AutoConfigureAfter(name = "com.ultracommerce.autoconfigure.DatabaseAutoConfiguration")
public class HSQLDatabaseAutoConfiguration {

    private static final Log LOG = LogFactory.getLog(HSQLDatabaseAutoConfiguration.class);

    @Autowired
    protected HSQLDBProperties props;

    @Autowired
    protected Environment environment;

    @ConditionalOnMissingBean(name="webDS")
    @Bean
    public HSQLDBServer ucEmbeddedDatabase() {
        return new HSQLDBServer(props, environment);
    }

    @ConditionalOnMissingBean(name={"webDS"})
    @DependsOn("ucEmbeddedDatabase")
    @Bean
    @Primary
    public DataSource webDS() {
        return buildDataSource();
    }

    @ConditionalOnMissingBean(name={"webSecureDS"})
    @DependsOn("ucEmbeddedDatabase")
    @Bean
    public DataSource webSecureDS() {
        return buildDataSource();
    }

    @ConditionalOnMissingBean(name={"webStorageDS"})
    @DependsOn("ucEmbeddedDatabase")
    @Bean
    public DataSource webStorageDS() {
        return buildDataSource();
    }

    @ConditionalOnMissingBean(name={"webEventDS"})
    @DependsOn("ucEmbeddedDatabase")
    @Bean
    public DataSource webEventDS() {
        return buildDataSource();
    }

    @ConditionalOnMissingBean(name={"demoDS"})
    @ConditionalOnClass(name= "com.blcdemo.core.domain.PDSite")
    @DependsOn("ucEmbeddedDatabase")
    @Bean
    public DataSource demoDS() {
        return buildDataSource();
    }

    protected DataSource buildDataSource() {
        String url = "jdbc:hsqldb:hsql://127.0.0.1:" + props.getPort() + "/" + props.getDbName();
        DatabaseDriver driver = DatabaseDriver.fromJdbcUrl(url);
        org.apache.tomcat.jdbc.pool.DataSource ds = DataSourceBuilder
                .create()
                .username("SA")
                .password("")
                .url(url)
                .driverClassName(driver.getDriverClassName())
                .type(org.apache.tomcat.jdbc.pool.DataSource.class)
                .build();

        String validationQuery = driver.getValidationQuery();
        if (validationQuery != null) {
            ds.setTestOnBorrow(true);
            ds.setValidationQuery(validationQuery);
        }

        ds.setInitSQL("SET DATABASE TRANSACTION CONTROL MVCC");
        return ds;
    }

}
