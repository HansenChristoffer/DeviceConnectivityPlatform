package io.miso.core.handler;

import io.miso.core.WorkOperation;

import java.nio.ByteBuffer;

public class HeaderHandler implements RemoteMessageHandler {
    private final WorkOperation workOperation;

    public HeaderHandler(final WorkOperation workOperation) {
        this.workOperation = workOperation;
    }

    @Override
    public byte[] handle() {
        final ByteBuffer headerBuffer = ByteBuffer.allocate(18);
        headerBuffer.putShort((short) workOperation.getOperationCommand().getId());
        headerBuffer.putLong(workOperation.getCreatedAt().toEpochMilli());
        headerBuffer.putInt((int) (workOperation.getClusterId() & 0xFFFF_FFFF));
        headerBuffer.putInt((int) (workOperation.getHubId() & 0xFFFF_FFFF));
        headerBuffer.putInt((int) (workOperation.getTransferId() & 0xFFFF_FFFF));

        return headerBuffer.array();
    }

    @Override
    public byte[] handle(final byte[] message) {
        return handle();
    }
}
