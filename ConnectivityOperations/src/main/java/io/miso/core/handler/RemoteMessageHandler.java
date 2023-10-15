package io.miso.core.handler;

public interface RemoteMessageHandler {
    byte[] handle();

    byte[] handle(byte[] message);
}
