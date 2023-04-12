package io.miso;

import io.miso.core.RemoteMessagePipeline;
import io.miso.core.handler.PipelineStep;
import io.miso.core.handler.RemoteMessageHandler;
import io.miso.exceptions.InvalidSMSMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoteMessagePipelineTest {

    @Mock
    RemoteMessageHandler headerHandler;

    @Mock
    RemoteMessageHandler payloadHandler;

    @Mock
    RemoteMessageHandler encryptionHandler;

    @Mock
    RemoteMessageHandler hmacHandler;

    // TODO: This tests old classes / SMS classes.  Add or change this to test the TCP connectivity!
    @Test
    void testValidateValidMessageLength() {
        when(headerHandler.handle()).thenReturn(new byte[10]);
        when(payloadHandler.handle()).thenReturn(new byte[100]);
        when(encryptionHandler.handle(any(byte[].class))).thenReturn(new byte[110]);
        when(hmacHandler.handle(any(byte[].class))).thenReturn(new byte[110]);

        final RemoteMessagePipeline pipeline = new RemoteMessagePipeline(true)
                .addHandler(PipelineStep.HEADER, headerHandler)
                .addHandler(PipelineStep.PAYLOAD, payloadHandler)
                .addHandler(PipelineStep.ENCRYPTION, encryptionHandler)
                .addHandler(PipelineStep.HMAC, hmacHandler);

        assertDoesNotThrow(pipeline::execute);
    }

    // TODO: This tests old / SMS classes. Add or change this to test the TCP connectivity!
    @Test
    void testValidateInvalidMessageLength() {
        when(headerHandler.handle()).thenReturn(new byte[10]);
        when(payloadHandler.handle()).thenReturn(new byte[119]);
        when(encryptionHandler.handle(any(byte[].class))).thenReturn(new byte[129]);
        when(hmacHandler.handle(any(byte[].class))).thenReturn(new byte[129]);

        final RemoteMessagePipeline pipeline = new RemoteMessagePipeline(true)
                .addHandler(PipelineStep.HEADER, headerHandler)
                .addHandler(PipelineStep.PAYLOAD, payloadHandler)
                .addHandler(PipelineStep.ENCRYPTION, encryptionHandler)
                .addHandler(PipelineStep.HMAC, hmacHandler);

        assertThrows(InvalidSMSMessage.class, pipeline::execute);
    }
}
