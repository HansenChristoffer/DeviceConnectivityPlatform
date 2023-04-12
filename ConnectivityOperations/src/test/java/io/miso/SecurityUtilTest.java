package io.miso;

import io.miso.util.RandomProvider;
import io.miso.util.SecurityUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class SecurityUtilTest {
    private static final int IV_BLOCK_SIZE = 16;
    private static final byte[] originalMessage = "This is a message to everyone out there! Please save the internet and Lahaina!".getBytes();
    private static byte[] hmacKey;
    private static byte[] aesKey;
    private static byte[] expectedBytesAfterEncryption;
    private static byte[] expectedBytesAfterHMACCalculation;

    @BeforeAll
    public static void setUp() {
        hmacKey = new byte[]{
                (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67,
                (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF,
                (byte) 0x10, (byte) 0x32, (byte) 0x54, (byte) 0x76,
                (byte) 0x98, (byte) 0xBA, (byte) 0xDC, (byte) 0xFE,
                (byte) 0x0F, (byte) 0x1E, (byte) 0x2D, (byte) 0x3C,
                (byte) 0x4B, (byte) 0x5A, (byte) 0x69, (byte) 0x78,
                (byte) 0x87, (byte) 0x96, (byte) 0xA5, (byte) 0xB4,
                (byte) 0xC3, (byte) 0xD2, (byte) 0xE1, (byte) 0xF0
        };

        aesKey = new byte[]{
                (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF,
                (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF
        };

        expectedBytesAfterEncryption = new byte[]{
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x8C, (byte) 0x25, (byte) 0xA2, (byte) 0x2B,
                (byte) 0x39, (byte) 0xE9, (byte) 0xFB, (byte) 0xFC, (byte) 0xEC, (byte) 0x2E, (byte) 0x5E, (byte) 0x68, (byte) 0x0D, (byte) 0xCD,
                (byte) 0x85, (byte) 0x9F, (byte) 0x96, (byte) 0x49, (byte) 0x9F, (byte) 0x78, (byte) 0x6A, (byte) 0x52, (byte) 0xFB, (byte) 0xD2,
                (byte) 0xFB, (byte) 0xB9, (byte) 0xDF, (byte) 0xDB, (byte) 0x72, (byte) 0x68, (byte) 0x24, (byte) 0x2B, (byte) 0x4C, (byte) 0xB4,
                (byte) 0x86, (byte) 0x7D, (byte) 0x6E, (byte) 0xCF, (byte) 0xD3, (byte) 0x5B, (byte) 0x8F, (byte) 0xF6, (byte) 0x33, (byte) 0xA4,
                (byte) 0xFA, (byte) 0x37, (byte) 0xA2, (byte) 0x97, (byte) 0xE4, (byte) 0x5F, (byte) 0x59, (byte) 0x5A, (byte) 0xF1, (byte) 0x4A,
                (byte) 0xBE, (byte) 0x7A, (byte) 0x74, (byte) 0x7A, (byte) 0xFB, (byte) 0x9E, (byte) 0x90, (byte) 0xF9, (byte) 0xFA, (byte) 0xE3,
                (byte) 0x8D, (byte) 0x20, (byte) 0x52, (byte) 0x48, (byte) 0x05, (byte) 0x9B, (byte) 0x45, (byte) 0x46, (byte) 0x5E, (byte) 0xD5,
                (byte) 0x9A, (byte) 0x04, (byte) 0x07, (byte) 0xE4, (byte) 0xED, (byte) 0x6B
        };

        expectedBytesAfterHMACCalculation = new byte[]{
                (byte) 0x89, (byte) 0xDA, (byte) 0x9D, (byte) 0xC5, (byte) 0x25, (byte) 0xB5, (byte) 0x8D, (byte) 0x5B,
                (byte) 0xB2, (byte) 0x6B, (byte) 0x58, (byte) 0x29, (byte) 0xEE, (byte) 0x7E, (byte) 0x69, (byte) 0x20,
                (byte) 0xF9, (byte) 0x57, (byte) 0xE8, (byte) 0xE7, (byte) 0x16, (byte) 0x4A, (byte) 0xD2, (byte) 0x40,
                (byte) 0xC5, (byte) 0xCF, (byte) 0xAB, (byte) 0xEC, (byte) 0x22, (byte) 0xEB, (byte) 0xA8, (byte) 0x76,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x8C, (byte) 0x25, (byte) 0xA2, (byte) 0x2B, (byte) 0x39, (byte) 0xE9, (byte) 0xFB, (byte) 0xFC,
                (byte) 0xEC, (byte) 0x2E, (byte) 0x5E, (byte) 0x68, (byte) 0x0D, (byte) 0xCD, (byte) 0x85, (byte) 0x9F,
                (byte) 0x96, (byte) 0x49, (byte) 0x9F, (byte) 0x78, (byte) 0x6A, (byte) 0x52, (byte) 0xFB, (byte) 0xD2,
                (byte) 0xFB, (byte) 0xB9, (byte) 0xDF, (byte) 0xDB, (byte) 0x72, (byte) 0x68, (byte) 0x24, (byte) 0x2B,
                (byte) 0x4C, (byte) 0xB4, (byte) 0x86, (byte) 0x7D, (byte) 0x6E, (byte) 0xCF, (byte) 0xD3, (byte) 0x5B,
                (byte) 0x8F, (byte) 0xF6, (byte) 0x33, (byte) 0xA4, (byte) 0xFA, (byte) 0x37, (byte) 0xA2, (byte) 0x97,
                (byte) 0xE4, (byte) 0x5F, (byte) 0x59, (byte) 0x5A, (byte) 0xF1, (byte) 0x4A, (byte) 0xBE, (byte) 0x7A,
                (byte) 0x74, (byte) 0x7A, (byte) 0xFB, (byte) 0x9E, (byte) 0x90, (byte) 0xF9, (byte) 0xFA, (byte) 0xE3,
                (byte) 0x8D, (byte) 0x20, (byte) 0x52, (byte) 0x48, (byte) 0x05, (byte) 0x9B, (byte) 0x45, (byte) 0x46,
                (byte) 0x5E, (byte) 0xD5, (byte) 0x9A, (byte) 0x04, (byte) 0x07, (byte) 0xE4, (byte) 0xED, (byte) 0x6B
        };
    }

    @Test
    void encryptTest() {
        final PredictableRandomProvider randomProvider = new PredictableRandomProvider();
        final byte[] actualBytes = SecurityUtil.encrypt(originalMessage, aesKey, randomProvider.getRandomBytes(IV_BLOCK_SIZE));
        assertArrayEquals(expectedBytesAfterEncryption, actualBytes);
    }

    @Test
    void decryptTest() {
        final byte[] actualBytes = SecurityUtil.decrypt(expectedBytesAfterEncryption, aesKey);
        assertArrayEquals(originalMessage, actualBytes);
    }

    @Test
    void calculateHMACTest() {
        final byte[] actualBytes = SecurityUtil.calculateHMAC(expectedBytesAfterEncryption, hmacKey);
        assertArrayEquals(expectedBytesAfterHMACCalculation, actualBytes);
    }

    @Test
    void validateHMACTest() {
        final byte[] actualBytes = SecurityUtil.validateHMAC(expectedBytesAfterHMACCalculation, hmacKey);
        assertArrayEquals(expectedBytesAfterEncryption, actualBytes);
    }

    protected static class PredictableRandomProvider implements RandomProvider {
        @Override
        public byte[] getRandomBytes(final int length) {
            return new byte[length];
        }
    }
}
