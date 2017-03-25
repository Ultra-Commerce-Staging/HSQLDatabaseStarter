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
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

/**
 * Adapted from https://www.javacodegeeks.com/2012/11/embedding-hsqldb-server-instance-in-spring.html by Allen Chee
 *
 * @author Jeff Fischer
 */
public class HSQLDBServer implements SmartLifecycle {

    private static final Log LOG = LogFactory.getLog(HSQLDBServer.class);
    protected HsqlProperties props;
    protected Server server;

    public HSQLDBServer(HSQLDBProperties autoProps) {
        Properties databaseConfig = new Properties();
        databaseConfig.setProperty("server.database.0", "file:" + autoProps.getWorkingDirectory() + autoProps.getDbName());
        databaseConfig.setProperty("server.dbname.0", autoProps.getDbName());
        databaseConfig.setProperty("server.remote_open", "true");
        databaseConfig.setProperty("hsqldb.reconfig_logging", "false");
        databaseConfig.setProperty("server.port", Integer.toString(autoProps.getPort()));
        
        this.props = new HsqlProperties(databaseConfig);
        
        // start on construction since we need this to be active immediately
        start();
    }

    @Override
    public boolean isRunning() {
        try (Socket ignored = new Socket(InetAddress.getByName(null), props.getIntegerProperty("server.port", 0))) {
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    @Override
    public void start() {
        // Extra isRunning() check since this is invoked on construction
        if (server == null && !isRunning()) {
            LOG.info("Starting HSQL server...");
            LOG.warn("HSQL embedded database server is for demonstration purposes only and is not intended for production usage.");
            server = new Server();
            try {
                server.setProperties(props);
                server.start();
            } catch (ServerAcl.AclFormatException afe) {
                LOG.error("Error starting HSQL server.", afe);
            } catch (IOException e) {
                LOG.error("Error starting HSQL server.", e);
            }
        }
    }

    @Override
    public void stop() {
        if (server != null) {
            LOG.info("Stopping HSQL server...");
            server.shutdown();
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
