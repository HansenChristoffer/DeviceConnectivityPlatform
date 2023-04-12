package io.miso.config;

import io.miso.core.config.DefaultValue;

public interface ManagementMainConfig {
    @DefaultValue("10")
    Integer getServiceMaxCoreSize();
}
