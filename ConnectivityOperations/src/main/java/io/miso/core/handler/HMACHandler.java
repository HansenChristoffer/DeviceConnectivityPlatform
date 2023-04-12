package io.miso.core.handler;

import io.miso.core.config.Configurator;
import io.miso.core.config.SecretConfig;
import io.miso.util.SecurityUtil;

public class HMACHandler implements RemoteMessageHandler {
    private final byte[] hmacKey;

    public HMACHandler() {
        final SecretConfig secretConfig = Configurator.getConfig(SecretConfig.class);
        this.hmacKey = secretConfig.getHMAC_KEY().getBytes();
    }

    public HMACHandler(final byte[] hmacKey) {
        this.hmacKey = hmacKey;
    }

    @Override
    public byte[] handle() {
        return new byte[0];
    }

    @Override
    public byte[] handle(final byte[] message) {
        return SecurityUtil.calculateHMAC(message, this.hmacKey);
    }
}
