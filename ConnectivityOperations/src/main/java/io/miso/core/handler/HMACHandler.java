package io.miso.core.handler;

import io.miso.util.SecurityUtil;

public class HMACHandler implements RemoteMessageHandler {
    private final byte[] hmacKey;

    public HMACHandler(final byte[] hmacKey) {
        this.hmacKey = hmacKey;
    }

    @Override
    public byte[] handle() {
        return new byte[0];
    }

    @Override
    public byte[] handle(final byte[] message) {
        return SecurityUtil.calculateHMAC(message, hmacKey);
    }
}
