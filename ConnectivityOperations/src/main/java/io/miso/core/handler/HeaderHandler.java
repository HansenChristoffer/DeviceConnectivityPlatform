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
        headerBuffer.putInt(workOperation.getClusterId());
        headerBuffer.putInt(workOperation.getHubId());

        return headerBuffer.array();
    }

    @Override
    public byte[] handle(final byte[] message) {
        return handle();
    }
}
