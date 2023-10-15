package io.miso.core.config;

public interface SecretConfig {
    @DefaultValue("testAES123456789")
    String getAES_KEY();

    @DefaultValue("testHMAC123456789")
    String getHMAC_KEY();

    @DefaultValue("16")
    Integer getIV_block_size();
}
