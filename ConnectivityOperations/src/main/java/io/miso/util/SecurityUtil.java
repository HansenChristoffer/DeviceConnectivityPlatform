package io.miso.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Handles HMAC and AES-128 encryption
 */
public class SecurityUtil {
    private static final Logger logger = LogManager.getFormatterLogger();
    private static final String AUTH_ALGORITHM = "HmacSHA256";
    private static final String CIPHER_ALGORITHM_MODE = "AES/CBC/PKCS5Padding";

    private SecurityUtil() {
    }

    public static byte[] composeHMACSecret(final byte[] keyBytes, final byte[] data) {
        if (keyBytes.length != 0) {
            try {
                final SecretKeySpec key = new SecretKeySpec(keyBytes, AUTH_ALGORITHM);
                final Mac mac = Mac.getInstance(AUTH_ALGORITHM);
                mac.init(key);
                final byte[] retVal = mac.doFinal(data);

                if (retVal.length != 32) {
                    logger.fatal("Composed invalid size of HMAC. Expected size was 32 but actual size is %d", retVal.length);
                    return new byte[0];
                }

                return retVal;
            } catch (final NoSuchAlgorithmException e) {
                logger.error("No such algorithm [%s] exists!", AUTH_ALGORITHM, e);
            } catch (final InvalidKeyException e) {
                logger.error("Invalid key given as argument!", e);
            }
        }
        return new byte[0];
    }

    public static byte[] encryptData(final byte[] data) {
        final String keyString = "TEST123456789123"; // TODO fetch key from secure backend

        try {
            // Convert key string to SecretKey
            final SecretKey secretKey = new SecretKeySpec(keyString.getBytes(), "AES");

            // Generate a random IV
            final byte[] iv = new byte[16];
            final SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            final IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            // Encrypt the plaintext
            final Cipher encryptCipher = Cipher.getInstance(CIPHER_ALGORITHM_MODE);
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            return encryptCipher.doFinal(data);
        } catch (final NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            logger.error("Invalid or no such algorithm when encrypting data!", e);
        } catch (final IllegalBlockSizeException e) {
            logger.error("Block size violation when encrypting data!", e);
        } catch (final BadPaddingException | NoSuchPaddingException e) {
            logger.error("Padding violation when encrypting data!", e);
        } catch (final InvalidKeyException e) {
            logger.fatal("Invalid or broken key when encrypting data!", e);
        }

        return new byte[0];
    }

}
