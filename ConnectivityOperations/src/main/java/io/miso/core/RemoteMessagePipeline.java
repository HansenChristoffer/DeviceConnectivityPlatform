package io.miso.core;

import io.miso.core.handler.PipelineStep;
import io.miso.core.handler.RemoteMessageHandler;
import io.miso.exceptions.InvalidSMSMessage;

import java.util.EnumMap;

public class RemoteMessagePipeline {
    private final EnumMap<PipelineStep, RemoteMessageHandler> handlers = new EnumMap<>(PipelineStep.class);
    private final boolean isSMS;

    public RemoteMessagePipeline(final boolean isSMS) {
        this.isSMS = isSMS;
    }

    public RemoteMessagePipeline addHandler(final PipelineStep step, final RemoteMessageHandler handler) {
        handlers.put(step, handler);
        return this;
    }

    public byte[] execute() {
        final byte[] header = handlers.get(PipelineStep.HEADER).handle();
        final byte[] payload = handlers.get(PipelineStep.PAYLOAD).handle();
        final byte[] combinedMessage = new byte[header.length + payload.length];
        System.arraycopy(header, 0, combinedMessage, 0, header.length);
        System.arraycopy(payload, 0, combinedMessage, header.length, payload.length);

        final byte[] encryptedMessage = handlers.get(PipelineStep.ENCRYPTION).handle(combinedMessage);
        final byte[] hmacedMessage = handlers.get(PipelineStep.HMAC).handle(encryptedMessage);
        validate(hmacedMessage);

        return hmacedMessage;
    }

    private void validate(final byte[] data) {
        if (isSMS) {
            if (data.length > 128) {
                throw new InvalidSMSMessage(String.format("Exceeds the SMS limits, data is %d long and we only allow 128 " +
                        "because of encryption(s)", data.length));
            }
        } else {
            // TODO validate non-sms message
        }
    }
}

