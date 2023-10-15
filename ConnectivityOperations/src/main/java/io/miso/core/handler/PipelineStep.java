package io.miso.core.handler;

public enum PipelineStep {
    HEADER,
    PAYLOAD,
    ENCRYPTION,
    HMAC,
    AUTH,
    MESSAGE,
    ACK,
    ANNOUNCER
}
