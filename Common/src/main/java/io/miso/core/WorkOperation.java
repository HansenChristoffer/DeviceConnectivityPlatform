package io.miso.core;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

public class WorkOperation {
    private OutboundCommand outboundCommand;
    private Integer clusterId;
    private Integer hubId;
    private Integer deviceId;
    private byte[] payload;
    private Instant createdAt;

    private WorkOperation() {
    }

    public static WorkOperation create() {
        return new WorkOperation();
    }

    public OutboundCommand getOperationCommand() {
        return outboundCommand;
    }

    public WorkOperation setOperationCommand(final OutboundCommand outboundCommand) {
        this.outboundCommand = outboundCommand;
        return this;
    }

    public WorkOperation operationCommand(final OutboundCommand outboundCommand) {
        this.outboundCommand = outboundCommand;
        return this;
    }

    public Integer getClusterId() {
        return clusterId;
    }

    public WorkOperation setClusterId(final Integer clusterId) {
        this.clusterId = clusterId;
        return this;
    }

    public Integer getHubId() {
        return hubId;
    }

    public WorkOperation setHubId(final Integer hubId) {
        this.hubId = hubId;
        return this;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public WorkOperation setDeviceId(final Integer deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public byte[] getPayload() {
        return payload;
    }

    public WorkOperation setPayload(final byte[] payload) {
        this.payload = payload;
        return this;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public WorkOperation setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
        return this;
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
        return getOperationCommand() == that.getOperationCommand() && Objects.equals(getClusterId(), that.getClusterId())
                && Objects.equals(getHubId(), that.getHubId()) && Objects.equals(getDeviceId(), that.getDeviceId())
                && Arrays.equals(getPayload(), that.getPayload()) && Objects.equals(getCreatedAt(), that.getCreatedAt());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getOperationCommand(), getClusterId(), getHubId(), getDeviceId(), getCreatedAt());
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
                ", createdAt=" + createdAt +
                '}';
    }
}
