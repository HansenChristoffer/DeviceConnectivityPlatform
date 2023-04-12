package io.miso.core.model;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class Cluster extends BaseModel {
    @BsonProperty("cluster_id")
    private long clusterId;

    @BsonProperty("devices")
    private List<Device> devices;

    public Cluster() {
        // For de-/serialization
    }

    public Cluster(final String internalId, final String internalRevision, final long clusterId, final List<Device> devices) {
        super(internalId, internalRevision);
        this.clusterId = clusterId;
        this.devices = devices;
    }

    public long getClusterId() {
        return this.clusterId;
    }

    public Cluster setClusterId(final long clusterId) {
        this.clusterId = clusterId;
        return this;
    }

    public List<Device> getDevices() {
        return this.devices;
    }

    public Cluster setDevices(final List<Device> devices) {
        this.devices = devices;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final Cluster cluster)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return this.clusterId == cluster.clusterId && Objects.equals(this.devices, cluster.devices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.clusterId, this.devices);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Cluster.class.getSimpleName() + "[", "]")
                .add("clusterId=" + this.clusterId)
                .add("devices=" + this.devices)
                .toString();
    }
}
