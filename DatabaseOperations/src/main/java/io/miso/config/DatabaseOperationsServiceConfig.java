package io.miso.config;

import io.miso.core.config.DefaultValue;

public interface DatabaseOperationsServiceConfig {
    @DefaultValue("30000")
    Long getDatabaseAliveCheckerWaitTime();
}
