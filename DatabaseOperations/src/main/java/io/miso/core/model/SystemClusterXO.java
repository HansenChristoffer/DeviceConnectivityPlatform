package io.miso.core.model;

import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class SystemClusterXO extends SystemBaseModel {
    @BsonProperty("cluster_id")
    private Long clusterId;

    @BsonProperty("devices")
    private List<SystemDeviceXO> systemDeviceXOS;

    public SystemClusterXO() {
        // For de-/serialization
    }

    public SystemClusterXO(final String internalRevision, final long clusterId,
                           final List<SystemDeviceXO> systemDeviceXOS) {
        super(internalRevision);
        this.clusterId = clusterId;
        this.systemDeviceXOS = systemDeviceXOS;
    }

    public SystemClusterXO(final String internalId, final String internalRevision, final long clusterId,
                           final List<SystemDeviceXO> systemDeviceXOS) {
        super(internalId, internalRevision);
        this.clusterId = clusterId;
        this.systemDeviceXOS = systemDeviceXOS;
    }

    public long getClusterId() {
        return this.clusterId;
    }

    public SystemClusterXO setClusterId(final long clusterId) {
        this.clusterId = clusterId;
        return this;
    }

    public List<SystemDeviceXO> getDevices() {
        return this.systemDeviceXOS;
    }

    public SystemClusterXO setDevices(final List<SystemDeviceXO> systemDeviceXOS) {
        this.systemDeviceXOS = systemDeviceXOS;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final SystemClusterXO systemClusterXO)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return this.clusterId == systemClusterXO.clusterId && Objects.equals(this.systemDeviceXOS, systemClusterXO.systemDeviceXOS);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.clusterId, this.systemDeviceXOS);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SystemClusterXO.class.getSimpleName() + "[", "]")
                .add("clusterId=" + this.clusterId)
                .add("devices=" + this.systemDeviceXOS)
                .toString();
    }

    public static class Builder {
        private String internalRevision;
        private long clusterId;
        private List<SystemDeviceXO> systemDeviceXOS;

        public Builder() {
        }

        public Builder setInternalRevision(final String internalRevision) {
            this.internalRevision = internalRevision;
            return this;
        }

        public Builder setClusterId(final long clusterId) {
            this.clusterId = clusterId;
            return this;
        }

        public Builder setSystemDeviceXOS(final List<SystemDeviceXO> systemDeviceXOS) {
            this.systemDeviceXOS = systemDeviceXOS;
            return this;
        }

        public SystemClusterXO build() {
            return new SystemClusterXO(internalRevision, clusterId, systemDeviceXOS);
        }
    }
}
