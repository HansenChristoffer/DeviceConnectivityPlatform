package io.miso.core.model;

import io.miso.core.InboundCommand;
import io.netty.buffer.ByteBuf;

import java.time.Instant;

public class InboundMessage {
    private final Instant receivedAt = Instant.now();
    private final InboundCommand ic;
    private InboundHeader header;
    private InboundPayload payload;

    public InboundMessage(final InboundCommand ic, final ByteBuf buffer) {
        this.ic = ic;

        if (buffer.readableBytes() > 0) {
            this.header = new InboundHeader()
                    .setClusterId(buffer.readUnsignedInt())
                    .setHubId(buffer.readUnsignedInt())
                    .setCreatedAt(Instant.ofEpochMilli(buffer.readUnsignedInt()));
            if (buffer.readableBytes() > 0) {
                this.payload = new InboundPayload()
                        .setPayloadBytes(buffer.readBytes(buffer.readableBytes()).array());
            }
        }
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public InboundCommand getIc() {
        return ic;
    }

    public InboundHeader getHeader() {
        return header;
    }

    public InboundPayload getPayload() {
        return payload;
    }

    private void validate() {
        // Validate this object!
    }

    static class InboundHeader {
        private Long clusterId;
        private Long hubId;
        private Instant createdAt;

        public InboundHeader() {
        }

        public Long getClusterId() {
            return clusterId;
        }

        public InboundHeader setClusterId(final Long clusterId) {
            this.clusterId = clusterId;
            return this;
        }

        public Long getHubId() {
            return hubId;
        }

        public InboundHeader setHubId(final Long hubId) {
            this.hubId = hubId;
            return this;
        }

        public Instant getCreatedAt() {
            return createdAt;
        }

        public InboundHeader setCreatedAt(final Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }
    }

    static class InboundPayload {
        private byte[] payloadBytes;

        public InboundPayload() {
        }

        public byte[] getPayloadBytes() {
            return payloadBytes;
        }

        public InboundPayload setPayloadBytes(final byte[] payloadBytes) {
            this.payloadBytes = payloadBytes;
            return this;
        }
    }
}
