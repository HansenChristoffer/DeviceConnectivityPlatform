package io.miso.sms;

import io.miso.core.OperationCommand;
import io.miso.core.WorkOperation;
import io.miso.exceptions.InvalidSMSMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class RemoteMessageSMSTest {
    private WorkOperation workOperation;

    @BeforeEach
    public void setUp() {
        final OperationCommand operationCommand = OperationCommand.OC_PING;
        final int clusterId = 12345;
        final byte[] payload = new byte[]{1, 2, 3, 4, 5};
        final Instant createdAt = Instant.now();
        workOperation = WorkOperation.create()
                .operationCommand(operationCommand)
                .clusterId(clusterId)
                .payload(payload)
                .createdAt(createdAt);
    }

    @Test
    void testMessageCreation() {
        final RemoteMessageSMS remoteMessageSMS = new RemoteMessageSMS(workOperation);
        assertNotNull(remoteMessageSMS);
    }

    @Test
    void testMessageSize() {
        final RemoteMessageSMS remoteMessageSMS = new RemoteMessageSMS(workOperation);
        final int actualSize = remoteMessageSMS.getMessage().length;
        final int expectedSize = 124; // HMAC(32) + Encrypted Header(18) + Encrypted Payload(74)
        assertEquals(expectedSize, actualSize);
    }

    @Test
    void testInvalidMessageSize() {
        final byte[] largePayload = new byte[100];
        final WorkOperation largeWorkOperation = WorkOperation.create()
                .operationCommand(OperationCommand.OC_PING)
                .createdAt(Instant.now())
                .clusterId(12345)
                .payload(largePayload);

        assertThrows(InvalidSMSMessage.class, () -> new RemoteMessageSMS(largeWorkOperation));
    }
}
