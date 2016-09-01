package com.broadleafcommerce.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Jeff Fischer
 */
@ConfigurationProperties("demo.database")
public class HSQLDBProperties {

    protected Boolean include = true;

    public Boolean getInclude() {
        return include;
    }

    public void setInclude(Boolean include) {
        this.include = include;
    }
}
