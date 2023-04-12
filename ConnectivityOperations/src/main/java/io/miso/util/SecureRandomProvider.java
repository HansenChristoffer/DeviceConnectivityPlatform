package io.miso.util;

import java.security.SecureRandom;

public class SecureRandomProvider implements RandomProvider {
    @Override
    public byte[] getRandomBytes(final int length) {
        final byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }
}
