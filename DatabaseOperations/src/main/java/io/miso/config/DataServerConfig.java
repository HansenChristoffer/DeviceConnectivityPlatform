package io.miso.config;

import io.miso.core.config.DefaultValue;

public interface DataServerConfig {
    @DefaultValue("jdbc:mysql://127.0.0.1:3306/central")
    String getUrl();

    @DefaultValue("bm")
    String getUser();

    @DefaultValue("123456789")
    String getSecret();

    class DataServerConfigImpl implements DataServerConfig {
        public DataServerConfigImpl() {
        }

        @Override
        public String getUrl() {
            return "null";
        }

        @Override
        public String getUser() {
            return "null";
        }

        @Override
        public String getSecret() {
            return "null";
        }
    }
}
