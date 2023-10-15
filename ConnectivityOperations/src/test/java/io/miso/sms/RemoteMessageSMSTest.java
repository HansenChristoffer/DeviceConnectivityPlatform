package io.miso.sms;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.miso.core.OutboundCommand;
import io.miso.core.WorkOperation;
import io.miso.core.handler.EncryptionHandler;
import io.miso.core.handler.HMACHandler;
import io.miso.core.handler.HeaderHandler;
import io.miso.core.handler.PayloadHandler;

class RemoteMessageSMSTest {
    private final byte[] aesKey = {(byte) 0x91, (byte) 0x36, (byte) 0x8D, (byte) 0x38,
            (byte) 0x7C, (byte) 0xA2, (byte) 0x0F, (byte) 0x1B,
            (byte) 0x89, (byte) 0xF8, (byte) 0x69, (byte) 0x6D,
            (byte) 0x4C, (byte) 0x11, (byte) 0x92, (byte) 0x1F};
    private final byte[] hmacKey = "test_hmac_key".getBytes(); // Replace with your HMAC key
    private WorkOperation workOperation;

    @BeforeEach
    void setUp() {
        final byte[] payloadMock = new byte[57];
        Arrays.fill(payloadMock, (byte) 0x1);
        workOperation = new WorkOperation.Builder()
                .setOperationCommand(OutboundCommand.OC_PING)
                .setClusterId(0x1L) // clusterId
                .setHubId(0x1L) // hubId
                .setDeviceId(0x1L) // deviceId
                .setPayload(payloadMock) // payload
                .setTransferId(0x1L) // transferId
                .setCreatedAt(Instant.now())
                .build();
    }

    @Test
    void testHeaderHandler() {
        final HeaderHandler headerHandler = new HeaderHandler(workOperation);
        final byte[] header = headerHandler.handle();
        assertEquals(18, header.length);
    }

    @Test
    void testPayloadHandler() {
        final PayloadHandler payloadHandler = new PayloadHandler(workOperation);
        final byte[] payload = payloadHandler.handle();
        assertEquals(61, payload.length);
    }

    @Test
    void testEncryptionHandler() {
        final byte[] combinedMessage = new byte[18 + 61]; // header + payload
        Arrays.fill(combinedMessage, (byte) 0x1);

        final EncryptionHandler encryptionHandler = new EncryptionHandler(aesKey, 16);
        final byte[] encryptedMessage = encryptionHandler.handle(combinedMessage);

        /*
         * aesKey.length = AES block size (16-block in this case).
         * AES will pad the message so that it is always a multiple of the block size. In our case, 0 bytes are missing.
         */
        final int remainingBytesToPad = aesKey.length - (combinedMessage.length % aesKey.length);
        assertEquals(combinedMessage.length + remainingBytesToPad +
                aesKey.length, encryptedMessage.length);
    }

    @Test
    void testHMACHandler() {
        final byte[] encryptedMessage = new byte[18 + 61];
        Arrays.fill(encryptedMessage, (byte) 0x1);
        final HMACHandler hmacHandler = new HMACHandler(hmacKey);
        final byte[] finalMessage = hmacHandler.handle(encryptedMessage);
        assertEquals(32 + encryptedMessage.length, finalMessage.length);
    }

    @Test
    void testMessagePipeline() {
        final RemoteMessageSMS remoteMessageSMS = new RemoteMessageSMS(workOperation);
        final byte[] message = remoteMessageSMS.buildMessage();
        final int expectedMessageSize = 32 /* HMAC size */ + 96 /* Encrypted header size + encrypted payload size*/;
        assertEquals(expectedMessageSize, message.length);
    }
}
