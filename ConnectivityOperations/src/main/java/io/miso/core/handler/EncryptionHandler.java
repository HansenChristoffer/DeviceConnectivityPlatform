package io.miso.core.handler;

import io.miso.util.SecurityUtil;

public class EncryptionHandler implements RemoteMessageHandler {

    private final byte[] aesKey;

    public EncryptionHandler(final byte[] aesKey) {
        this.aesKey = aesKey;
    }

    @Override
    public byte[] handle() {
        return new byte[0];
    }

    @Override
    public byte[] handle(final byte[] message) {
        return SecurityUtil.encrypt(message, aesKey);
    }
}
