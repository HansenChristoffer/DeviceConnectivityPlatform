package io.miso.core.model;

import java.util.List;

public class Cluster {

    private String internalId;

    private String internalRevision;

    private long clusterId;

    private List<Device> devices;

    public Cluster() {
        // For de-/serialization
    }

    public Cluster(final String internalId, final String internalRevision, final long clusterId, final List<Device> devices) {
        this.internalId = internalId;
        this.internalRevision = internalRevision;
        this.clusterId = clusterId;
        this.devices = devices;
    }

    public String getInternalId() {
        return internalId;
    }

    public Cluster setInternalId(final String internalId) {
        this.internalId = internalId;
        return this;
    }

    public String getInternalRevision() {
        return internalRevision;
    }

    public Cluster setInternalRevision(final String internalRevision) {
        this.internalRevision = internalRevision;
        return this;
    }

    public long getClusterId() {
        return clusterId;
    }

    public Cluster setClusterId(final long clusterId) {
        this.clusterId = clusterId;
        return this;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public Cluster setDevices(final List<Device> devices) {
        this.devices = devices;
        return this;
    }
}
