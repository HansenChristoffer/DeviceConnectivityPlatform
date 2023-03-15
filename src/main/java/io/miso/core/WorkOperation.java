package io.miso.core;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

public class WorkOperation {
    private OperationCommand operationCommand;
    private Integer clusterId;
    private byte[] payload;
    private Instant createdAt;

    private WorkOperation() {
    }

    public static WorkOperation create() {
        return new WorkOperation();
    }

    public OperationCommand getOperationCommand() {
        return operationCommand;
    }

    public WorkOperation operationCommand(final OperationCommand operationCommand) {
        this.operationCommand = operationCommand;
        return this;
    }

    public Integer getClusterId() {
        return clusterId;
    }

    public WorkOperation clusterId(final Integer clusterId) {
        this.clusterId = clusterId;
        return this;
    }

    public byte[] getPayload() {
        return payload;
    }

    public WorkOperation payload(final byte[] payload) {
        this.payload = payload;
        return this;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public WorkOperation createdAt(final Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final WorkOperation that = (WorkOperation) o;
        return operationCommand == that.operationCommand && Objects.equals(clusterId, that.clusterId) &&
                Arrays.equals(payload, that.payload) &&
                Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(operationCommand, clusterId, createdAt);
        result = 31 * result + Arrays.hashCode(payload);
        return result;
    }

    @Override
    public String toString() {
        return "WorkOperation{" +
                "operationCommand=" + operationCommand +
                ", clusterId=" + clusterId +
                ", payload=" + Arrays.toString(payload) +
                ", createdAt=" + createdAt +
                '}';
    }
}
