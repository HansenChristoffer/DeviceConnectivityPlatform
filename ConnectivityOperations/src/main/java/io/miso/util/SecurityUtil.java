package io.miso.util;

import io.miso.exceptions.FailedEncryptionException;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class SecurityUtil {
    private static final String ENCRYPT_ALGORITHM = "AES";
    private static final String ENCRYPT_CIPHER = "AES/CBC/PKCS5Padding";
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private SecurityUtil() {
    }

    public static byte[] encrypt(final byte[] data, final byte[] aesKey) {
        try {
            final SecretKey secretKey = new SecretKeySpec(aesKey, ENCRYPT_ALGORITHM);
            final Cipher cipher = Cipher.getInstance(ENCRYPT_CIPHER);
            final byte[] iv = new byte[cipher.getBlockSize()];
            final SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            final IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            final byte[] encryptedData = cipher.doFinal(data);

            final byte[] encryptedDataWithIV = new byte[encryptedData.length + iv.length];
            System.arraycopy(iv, 0, encryptedDataWithIV, 0, iv.length);
            System.arraycopy(encryptedData, 0, encryptedDataWithIV, iv.length, encryptedData.length);

            return encryptedDataWithIV;
        } catch (final Exception e) {
            throw new FailedEncryptionException("Encryption error", e);
        }
    }

    public static byte[] calculateHMAC(final byte[] data, final byte[] hmacKey) {
        try {
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKey, HMAC_ALGORITHM);
            final Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(secretKey);

            final byte[] macBytes = mac.doFinal(data);
            final byte[] retBytes = new byte[macBytes.length + data.length];

            System.arraycopy(macBytes, 0, retBytes, 0, macBytes.length);
            System.arraycopy(data, 0, retBytes, macBytes.length, data.length);

            return retBytes;
        } catch (final Exception e) {
            throw new FailedEncryptionException("HMAC calculation error", e);
        }
    }
}
