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

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Jeff Fischer
 */
@ConfigurationProperties("demo.database")
public class HSQLDBProperties {

    protected Boolean autoConfigEnabled = true;
    protected String dbName = "broadleaf";
    protected String workingDirectory = System.getProperty("java.io.tmpdir") + "/broadleaf-hsqldb/";
    protected Boolean clearPersistedState = true;
    protected int port = 9001;

    public Boolean getAutoConfigEnabled() {
        return autoConfigEnabled;
    }

    public void setAutoConfigEnabled(Boolean autoConfigEnabled) {
        this.autoConfigEnabled = autoConfigEnabled;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }
    
    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }

    public Boolean getClearPersistedState() {
        return clearPersistedState;
    }

    public void setClearPersistedState(Boolean clearPersistedState) {
        this.clearPersistedState = clearPersistedState;
    }
}
