/*
 * #%L
 * BroadleafCommerce Database Starter
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package com.broadleafcommerce.autoconfigure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import java.util.Properties;

import javax.sql.DataSource;

/**
 * @author Jeff Fischer
 */
@Configuration
@EnableConfigurationProperties(HSQLDBProperties.class)
public class DatabaseAutoConfiguration {

    private static final Log LOG = LogFactory.getLog(DatabaseAutoConfiguration.class);

    @Autowired
    HSQLDBProperties props;

    @ConditionalOnMissingBean(name={"webDS"})
    @Bean
    public HSQLDBServer blEmbeddedDatabase() {
        Properties inMemoryConfig = new Properties();
        inMemoryConfig.setProperty("server.database.0", "mem:broadleaf");
        inMemoryConfig.setProperty("server.dbname.0", "broadleaf");
        inMemoryConfig.setProperty("server.remote_open", "true");
        inMemoryConfig.setProperty("hsqldb.reconfig_logging", "false");
        return new HSQLDBServer(inMemoryConfig, props);
    }

    @ConditionalOnMissingBean(name={"webDS"})
    @DependsOn("blEmbeddedDatabase")
    @Bean
    @Primary
    public DataSource webDS() {
        return buildDataSource();
    }

    @ConditionalOnMissingBean(name={"webSecureDS"})
    @DependsOn("blEmbeddedDatabase")
    @Bean
    public DataSource webSecureDS() {
        return buildDataSource();
    }

    @ConditionalOnMissingBean(name={"webStorageDS"})
    @DependsOn("blEmbeddedDatabase")
    @Bean
    public DataSource webStorageDS() {
        return buildDataSource();
    }

    @ConditionalOnMissingBean(name={"webEventDS"})
    @DependsOn("blEmbeddedDatabase")
    @Bean
    public DataSource webEventDS() {
        return buildDataSource();
    }

    @ConditionalOnMissingBean(name={"demoDS"})
    @ConditionalOnClass(name= "com.broadleafcommerce.registered.common.domain.PDSite")
    @DependsOn("blEmbeddedDatabase")
    @Bean
    public DataSource demoDS() {
        return buildDataSource();
    }

    protected DataSource buildDataSource() {
        return DataSourceBuilder
            .create()
            .username("SA")
            .password("")
            .url("jdbc:hsqldb:hsql://localhost/broadleaf")
            .driverClassName("org.hsqldb.jdbcDriver")
            .build();
    }

}
