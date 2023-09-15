package io.miso.config;

import io.miso.core.config.DefaultValue;

public interface DataServerConfig {
    @DefaultValue("mongodb://localhost:27017")
    String getUrl();

    @DefaultValue("root")
    String getUser();

    @DefaultValue("secret")
    String getSecret();

    @DefaultValue("dcp_central")
    String getDatabase();

    @DefaultValue("true")
    Boolean isDeprecationErrors();

    @DefaultValue("1")
    String getServerApiVersion();

    @DefaultValue("true")
    Boolean isRetryWrite();

    @DefaultValue("true")
    Boolean isRetryRead();

    @DefaultValue("10")
    Integer getMaxPoolSize();

    @DefaultValue("3")
    Integer getMaxConnecting();

    @DefaultValue("60000")
    Integer getMaxConnectionIdleTime();

    @DefaultValue("1800000")
    Integer getMaxConnectionLifeTime();

    @DefaultValue("60000")
    Integer getMaintenanceInitialDelay();

    @DefaultValue("1000")
    Integer getMaintenanceFrequency();

    @DefaultValue("255")
    Integer getReceiveBufferSize();

    @DefaultValue("255")
    Integer getSendBufferSize();

    @DefaultValue("15000")
    Integer getHeartbeatFrequency();

    @DefaultValue("2500")
    Integer getMinHeartbeatFrequency();

    @DefaultValue("false")
    Boolean isSSLEnabled();
}
