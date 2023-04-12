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
        headerBuffer.putShort((short) this.workOperation.getOperationCommand().getId());
        headerBuffer.putLong(this.workOperation.getCreatedAt().toEpochMilli());
        headerBuffer.putInt(this.workOperation.getClusterId());
        headerBuffer.putInt(this.workOperation.getHubId());
        // TODO add some kind of numbering, so we can know which ack belongs to what sent message

        return headerBuffer.array();
    }

    @Override
    public byte[] handle(final byte[] message) {
        return handle();
    }
}
