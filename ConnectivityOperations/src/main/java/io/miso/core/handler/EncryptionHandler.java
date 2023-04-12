package io.miso.core.handler;

import io.miso.core.config.Configurator;
import io.miso.core.config.SecretConfig;
import io.miso.util.SecureRandomProvider;
import io.miso.util.SecurityUtil;

public class EncryptionHandler implements RemoteMessageHandler {
    private final byte[] aesKey;
    private final int blockSize;

    public EncryptionHandler() {
        final SecretConfig secretConfig = Configurator.getConfig(SecretConfig.class);
        this.aesKey = secretConfig.getAES_KEY().getBytes();
        this.blockSize = secretConfig.getIV_block_size();
    }

    public EncryptionHandler(final byte[] aesKey, final int blockSize) {
        this.aesKey = aesKey;
        this.blockSize = blockSize;
    }

    @Override
    public byte[] handle() {
        return new byte[0];
    }

    @Override
    public byte[] handle(final byte[] message) {
        final SecureRandomProvider randomProvider = new SecureRandomProvider();
        return SecurityUtil.encrypt(message, this.aesKey, randomProvider.getRandomBytes(this.blockSize));
    }
}
