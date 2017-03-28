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
import org.springframework.core.env.Environment;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
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

    public HSQLDBServer(final HSQLDBProperties autoProps, Environment environment) {
        clearState(autoProps, environment);
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

    protected void clearState(final HSQLDBProperties autoProps, Environment environment) {
        File dbFile = new File(autoProps.getWorkingDirectory());
        boolean isAlwaysClear = autoProps.getAlwaysClearState();
        boolean isPropertyClear = autoProps.getClearStateOnPropertyOnly();
        if (isPropertyClear) {
            if (StringUtils.isEmpty(autoProps.getClearStateProperty())) {
                LOG.warn("clearStateOnPropertyOnly was set to true, but a clearStateProperty was not defined. Not clearing database state based on the property.");
                isPropertyClear = false;
            } else {
                String propVal = environment.getProperty(autoProps.getClearStateProperty());
                if (StringUtils.isEmpty(propVal)) {
                    LOG.warn(String.format("Unable to find the %s property in the Spring environment. Not clearing database state based on the property.", autoProps.getClearStateProperty()));
                    isPropertyClear = false;
                } else {
                    if (!StringUtils.isEmpty(autoProps.getClearStatePropertyValues())) {
                        String[] vals = autoProps.getClearStatePropertyValues().split(";");
                        Arrays.sort(vals);
                        if (Arrays.binarySearch(vals, propVal) < 0) {
                            isPropertyClear = false;
                        }
                    }
                }
            }
        }
        if (dbFile.exists() && dbFile.isDirectory() && (isAlwaysClear || isPropertyClear)) {
            File[] myDBContents = dbFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith(autoProps.getDbName() + ".");
                }
            });
            for (File item : myDBContents) {
                boolean deleted = FileSystemUtils.deleteRecursively(item);
                if (!deleted) {
                    LOG.warn(String.format("Unable to clear previous temporary database file (%s). As a result, previous, unwanted values may be utilized during this run.", item.getAbsolutePath()));
                }
            }
        }
    }
}
