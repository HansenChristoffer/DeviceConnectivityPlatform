package io.miso.core.config;

public interface NettyConfig {

    @DefaultValue("8888")
    Integer getTcpPort();
}
