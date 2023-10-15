package io.miso.core.model;

import io.miso.device.DeviceType;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Objects;
import java.util.StringJoiner;

public class SystemDeviceXO extends SystemBaseModel {
    @BsonProperty("cluster_id")
    private Long clusterId;

    @BsonProperty("device_id")
    private Long deviceId;

    @BsonProperty("device_type")
    private DeviceType deviceType;

    public SystemDeviceXO() {
        // For de-/se-rialization
    }

    public SystemDeviceXO(final String internalRevision, final long clusterId, final long deviceId,
                          final DeviceType deviceType) {
        super(internalRevision);
        this.clusterId = clusterId;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }

    public SystemDeviceXO(final String internalId, final String internalRevision, final long clusterId,
                          final long deviceId, final DeviceType deviceType) {
        super(internalId, internalRevision);
        this.clusterId = clusterId;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }

    public long getClusterId() {
        return this.clusterId;
    }

    @BsonCreator
    public SystemDeviceXO setClusterId(final long clusterId) {
        this.clusterId = clusterId;
        return this;
    }

    public long getDeviceId() {
        return this.deviceId;
    }

    @BsonCreator
    public SystemDeviceXO setDeviceId(final long deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public DeviceType getDeviceType() {
        return this.deviceType;
    }

    @BsonCreator
    public SystemDeviceXO setDeviceType(final DeviceType deviceType) {
        this.deviceType = deviceType;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final SystemDeviceXO systemDeviceXO)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return this.clusterId == systemDeviceXO.clusterId
                && this.deviceId == systemDeviceXO.deviceId
                && this.deviceType == systemDeviceXO.deviceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.clusterId, this.deviceId, this.deviceType);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SystemDeviceXO.class.getSimpleName() + "[", "]")
                .add("clusterId=" + this.clusterId)
                .add("deviceId=" + this.deviceId)
                .add("deviceType=" + this.deviceType)
                .toString();
    }

    public static class Builder {
        private String internalId;

        private String internalRevision;

        private long clusterId;

        private long deviceId;

        private DeviceType deviceType;

        public Builder() {
        }

        public Builder setInternalId(final String internalId) {
            this.internalId = internalId;
            return this;
        }

        public Builder setInternalRevision(final String internalRevision) {
            this.internalRevision = internalRevision;
            return this;
        }

        public Builder setClusterId(final long clusterId) {
            this.clusterId = clusterId;
            return this;
        }

        public Builder setDeviceId(final long deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public Builder setDeviceType(final DeviceType deviceType) {
            this.deviceType = deviceType;
            return this;
        }

        public SystemDeviceXO build() {
            return new SystemDeviceXO(internalId, internalRevision, clusterId, deviceId, deviceType);
        }
    }
}
