package io.miso.sms;

import io.miso.core.RemoteMessagePipeline;
import io.miso.core.WorkOperation;
import io.miso.core.config.Configurator;
import io.miso.core.config.SecretConfig;
import io.miso.core.handler.*;

/**
 * Remote Message protocol (RM-protocol 0.5) using AES-128 for encryption and HMAC-SHA256 for authentication
 * <p>
 * Field	Size (bytes)	Description
 * HMAC-SHA256	32	Message Authentication Code to verify the authenticity and integrity of the message.
 * Encrypted Header	18	Encrypted header containing WorkOperation ID (2), timestamp (12), and origin ID (4).
 * Encrypted Payload	74	Encrypted payload containing data specific to the WorkOperation.
 * <p>
 * Total message size: 32 (HMAC) + 18 (Header) + 74 (Payload) = 124 bytes
 */
public class RemoteMessageSMS {
    private final WorkOperation workOperation;
    private final SecretConfig secretConfig;

    public RemoteMessageSMS(final WorkOperation workOperation) {
        this.workOperation = workOperation;
        this.secretConfig = Configurator.getConfig(SecretConfig.class);
    }

    public byte[] buildMessage() {
        final RemoteMessagePipeline pipeline = new RemoteMessagePipeline(true)
                .addHandler(PipelineStep.HEADER, new HeaderHandler(workOperation))
                .addHandler(PipelineStep.PAYLOAD, new PayloadHandler(workOperation))
                .addHandler(PipelineStep.ENCRYPTION, new EncryptionHandler(secretConfig.getAES_KEY().getBytes()))
                .addHandler(PipelineStep.HMAC, new HMACHandler(secretConfig.getHMAC_KEY().getBytes()));
        return pipeline.execute();
    }
}

