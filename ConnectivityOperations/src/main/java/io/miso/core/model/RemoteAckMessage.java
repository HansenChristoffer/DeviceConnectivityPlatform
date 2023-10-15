package io.miso.core.model;

public class RemoteAckMessage {
    private byte[] payload;

    public RemoteAckMessage() {
    }

    public RemoteAckMessage(final byte[] payload) {
        this.payload = payload;
    }

    public byte[] getPayload() {
        return payload;
    }

    public RemoteAckMessage setPayload(final byte[] payload) {
        this.payload = payload;
        return this;
    }
}
