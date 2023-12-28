package io.miso.core;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

public class WorkOperation {
    private final OutboundCommand outboundCommand;
    private final Long clusterId;
    private final Long hubId;
    private final Long deviceId;
    private final byte[] payload;
    private final Long transferId;
    private final Instant createdAt;

    private WorkOperation(final OutboundCommand outboundCommand, final Long clusterId,
            final Long hubId, final Long deviceId, final byte[] payload,
            final Long transferId, final Instant createdAt) {

        this.outboundCommand = outboundCommand;
        this.clusterId = clusterId;
        this.hubId = hubId;
        this.deviceId = deviceId;
        this.payload = payload;
        this.transferId = transferId;
        this.createdAt = createdAt;
    }

    public OutboundCommand getOperationCommand() {
        return outboundCommand;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public Long getHubId() {
        return hubId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public byte[] getPayload() {
        return payload;
    }

    public Long getTransferId() {
        return transferId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final WorkOperation that = (WorkOperation) o;
        return getOperationCommand() == that.getOperationCommand()
                && Objects.equals(getClusterId(), that.getClusterId())
                && Objects.equals(getHubId(), that.getHubId()) && Objects.equals(getDeviceId(), that.getDeviceId())
                && Arrays.equals(getPayload(), that.getPayload()) && Objects.equals(getCreatedAt(), that.getCreatedAt())
                && Objects.equals(getTransferId(), that.getTransferId());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getOperationCommand(), getClusterId(), getHubId(), getDeviceId(), getTransferId(),
                getCreatedAt());
        result = 31 * result + Arrays.hashCode(getPayload());
        return result;
    }

    @Override
    public String toString() {
        return "WorkOperation{" +
                "operationCommand=" + outboundCommand +
                ", clusterId=" + clusterId +
                ", hubId=" + hubId +
                ", deviceId=" + deviceId +
                ", payload=" + Arrays.toString(payload) +
                ", transferId=" + transferId +
                ", createdAt=" + createdAt +
                '}';
    }

    public static class Builder {
        private OutboundCommand outboundCommand;
        private Long clusterId;
        private Long hubId;
        private Long deviceId;
        private byte[] payload;
        private Long transferId;
        private Instant createdAt;

        public Builder() {
            //
        }

        public OutboundCommand getOperationCommand() {
            return outboundCommand;
        }

        public Builder setOperationCommand(final OutboundCommand outboundCommand) {
            this.outboundCommand = outboundCommand;
            return this;
        }

        public Builder setClusterId(final Long clusterId) {
            this.clusterId = clusterId;
            return this;
        }

        public Builder setHubId(final Long hubId) {
            this.hubId = hubId;
            return this;
        }

        public Builder setDeviceId(final Long deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public Builder setPayload(final byte[] payload) {
            this.payload = payload;
            return this;
        }

        public Builder setTransferId(final Long transferId) {
            this.transferId = transferId;
            return this;
        }

        public Builder setCreatedAt(final Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public WorkOperation build() {
            return new WorkOperation(outboundCommand, clusterId, hubId, deviceId, payload, transferId, createdAt);
        }
    }
}
