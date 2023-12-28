package io.miso.util;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.miso.exceptions.FailedDecryptionException;
import io.miso.exceptions.FailedEncryptionException;
import io.miso.exceptions.InvalidMessageException;

public class SecurityUtil {
    private static final Logger logger = LogManager.getFormatterLogger();
    private static final String ENCRYPT_ALGORITHM = "AES";
    private static final String ENCRYPT_CIPHER = "AES/CBC/PKCS5Padding";
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private SecurityUtil() {
    }

    public static byte[] encrypt(final byte[] data, final byte[] aesKey, final byte[] iv) {
        try {
            logger.debug("This is the IV used when encrypting: %s", iv.length > 0 ? Arrays.toString(iv) : "N/A");

            final SecretKey secretKey = new SecretKeySpec(aesKey, ENCRYPT_ALGORITHM);
            final Cipher cipher = Cipher.getInstance(ENCRYPT_CIPHER);
            final IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

            logger.debug("Data before encrypted: %s", data.length > 0 ? Arrays.toString(data) : "N/A");

            final byte[] encryptedData = cipher.doFinal(data);

            logger.debug("Data after encrypted: %s", encryptedData.length > 0 ? Arrays.toString(encryptedData) : "N/A");

            final byte[] encryptedDataWithIV = new byte[encryptedData.length + iv.length];
            System.arraycopy(iv, 0, encryptedDataWithIV, 0, iv.length);
            System.arraycopy(encryptedData, 0, encryptedDataWithIV, iv.length, encryptedData.length);

            logger.debug("Encrypted data with IV : %s",
                    encryptedDataWithIV.length > 0 ? Arrays.toString(encryptedDataWithIV) : "N/A");

            return encryptedDataWithIV;
        } catch (final Exception e) {
            logger.debug("Failed to encrypt!", e);
            throw new FailedEncryptionException("Encryption error", e);
        }
    }

    public static byte[] decrypt(final byte[] encryptedData, final byte[] aesKey) {
        try {
            final SecretKey secretKey = new SecretKeySpec(aesKey, ENCRYPT_ALGORITHM);
            final Cipher cipher = Cipher.getInstance(ENCRYPT_CIPHER);
            final byte[] iv = new byte[cipher.getBlockSize()];
            System.arraycopy(encryptedData, 0, iv, 0, iv.length);

            logger.debug("This is the IV found when decrypting: %s", iv.length > 0 ? Arrays.toString(iv) : "EMPTY");

            final IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

            return cipher.doFinal(encryptedData, iv.length, encryptedData.length - iv.length);
        } catch (final Exception e) {
            throw new FailedDecryptionException("Decryption error", e);
        }
    }

    public static byte[] calculateHMAC(final byte[] data, final byte[] hmacKey) {
        try {
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKey, HMAC_ALGORITHM);
            final Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(secretKey);

            final byte[] macBytes = mac.doFinal(data);
            logger.debug("HMAC bytes: %s", macBytes.length > 0 ? Arrays.toString(macBytes) : "N/A");

            return macBytes;
        } catch (final Exception e) {
            throw new FailedEncryptionException("HMAC calculation error", e);
        }
    }

    public static byte[] addHMAC(final byte[] hmacBytes, final byte[] data) {
        final byte[] retBytes = new byte[hmacBytes.length + data.length];

        logger.debug("Message before adding calculated HMAC to it: %s",
                data.length > 0 ? Arrays.toString(data) : "N/A");

        System.arraycopy(hmacBytes, 0, retBytes, 0, hmacBytes.length);
        System.arraycopy(data, 0, retBytes, hmacBytes.length, data.length);

        logger.debug("Message after adding calculated HMAC to it: %s",
                retBytes.length > 0 ? Arrays.toString(retBytes) : "N/A");

        return retBytes;
    }

    public static byte[] validateHMAC(final byte[] dataWithHMAC, final byte[] hmacKey) {
        try {
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKey, HMAC_ALGORITHM);
            final Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(secretKey);

            final int hmacLength = mac.getMacLength();
            final byte[] expectedHmac = Arrays.copyOfRange(dataWithHMAC, 0, hmacLength);
            final byte[] data = Arrays.copyOfRange(dataWithHMAC, hmacLength, dataWithHMAC.length);
            final byte[] actualHmac = mac.doFinal(data);

            logger.debug("HMAC from validation: %s", expectedHmac.length > 0 ? Arrays.toString(expectedHmac) : "N/A");
            logger.debug("Data without HMAC: %s", data.length > 0 ? Arrays.toString(data) : "N/A");

            if (!Arrays.equals(expectedHmac, actualHmac)) {
                logger.error("HMAC mismatch. Expected: %s, Actual: %s",
                        Arrays.toString(expectedHmac) != null ? Arrays.toString(expectedHmac) : "N/A",
                        Arrays.toString(actualHmac) != null ? Arrays.toString(actualHmac) : "N/A");
                throw new InvalidMessageException("HMAC validation error");
            }

            return data;
        } catch (final Exception e) {
            throw new FailedDecryptionException("HMAC validation error", e);
        }
    }
}
