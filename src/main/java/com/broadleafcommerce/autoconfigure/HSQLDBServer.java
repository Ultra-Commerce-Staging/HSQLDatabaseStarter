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
import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerAcl;
import org.springframework.context.SmartLifecycle;

import java.io.IOException;
import java.util.Properties;

/**
 * Adapted from https://www.javacodegeeks.com/2012/11/embedding-hsqldb-server-instance-in-spring.html by Allen Chee
 *
 * @author Jeff Fischer
 */
public class HSQLDBServer implements SmartLifecycle {

    private static final Log LOG = LogFactory.getLog(HSQLDBServer.class);
    protected HsqlProperties properties;
    protected HSQLDBProperties autoProps;
    protected Server server;
    protected boolean running = false;

    public HSQLDBServer(Properties props, HSQLDBProperties autoProps) {
        properties = new HsqlProperties(props);
        this.autoProps = autoProps;
        startDB();
    }

    @Override
    public boolean isRunning() {
        if (server != null) {
            server.checkRunning(running);
        }
        return running;
    }

    public void startDB() {
        if (server == null && autoProps.getInclude()) {
            LOG.info("Starting HSQL server...");
            LOG.warn("HSQL embedded database server is for demonstration purposes only and is not intended for production usage.");
            server = new Server();
            try {
                server.setProperties(properties);
                server.start();
                running = true;
            } catch (ServerAcl.AclFormatException afe) {
                LOG.error("Error starting HSQL server.", afe);
            } catch (IOException e) {
                LOG.error("Error starting HSQL server.", e);
            }
        }
    }

    @Override
    public void start() {
        //do nothing
    }

    @Override
    public void stop() {
        LOG.info("Stopping HSQL server...");
        if (server != null && autoProps.getInclude()) {
            server.stop();
            running = false;
        }
    }

    @Override
    public int getPhase() {
        return Integer.MIN_VALUE;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable runnable) {
        stop();
        runnable.run();
    }
}
