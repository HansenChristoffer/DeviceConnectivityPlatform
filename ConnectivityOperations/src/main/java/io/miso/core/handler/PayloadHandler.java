package io.miso.core.handler;

import io.miso.core.WorkOperation;

import java.nio.ByteBuffer;

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
            payloadBuffer.putInt(workOperation.getDeviceId());
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
