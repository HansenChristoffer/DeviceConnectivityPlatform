package io.miso.config;

import io.miso.core.config.DefaultValue;

public interface DataServerConfig {
    @DefaultValue("jdbc:mysql://127.0.0.1:3306/centrals")
    String getUrl();

    @DefaultValue("bms")
    String getUser();

    @DefaultValue("123456789z")
    String getSecret();

}
