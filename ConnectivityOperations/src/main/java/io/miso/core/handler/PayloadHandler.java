package io.miso.core.handler;

import java.nio.ByteBuffer;

import io.miso.core.WorkOperation;

public class PayloadHandler implements RemoteMessageHandler {
    private final WorkOperation workOperation;

    public PayloadHandler(final WorkOperation workOperation) {
        this.workOperation = workOperation;
    }

    @Override
    public byte[] handle() {
        if (workOperation.getDeviceId() != null) {
            final byte[] payloadBytes = workOperation.getPayload();
            final ByteBuffer payloadBuffer = ByteBuffer.allocate(payloadBytes.length + 4); // 4 = integers byte size
            payloadBuffer.putInt((int) (workOperation.getDeviceId() & 0xFFFF_FFFF));
            payloadBuffer.put(payloadBytes);

            return payloadBuffer.array();
        }

        return workOperation.getPayload();
    }

    @Override
    public byte[] handle(final byte[] message) {
        return handle();
    }
}
