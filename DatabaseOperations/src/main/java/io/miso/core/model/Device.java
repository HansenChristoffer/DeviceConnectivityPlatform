package io.miso.core.model;

import java.util.Objects;
import java.util.StringJoiner;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import io.miso.device.DeviceType;

public class Device extends BaseModel {
    @BsonProperty("cluster_id")
    private long clusterId;

    @BsonProperty("device_id")
    private long deviceId;

    @BsonProperty("device_type")
    private DeviceType deviceType;

    public Device() {
        // For de-/se-rialization
    }

    public Device(final String internalId, final String internalRevision, final long clusterId,
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
    public Device setClusterId(final long clusterId) {
        this.clusterId = clusterId;
        return this;
    }

    public long getDeviceId() {
        return this.deviceId;
    }

    @BsonCreator
    public Device setDeviceId(final long deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public DeviceType getDeviceType() {
        return this.deviceType;
    }

    @BsonCreator
    public Device setDeviceType(final DeviceType deviceType) {
        this.deviceType = deviceType;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final Device device)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return this.clusterId == device.clusterId
                && this.deviceId == device.deviceId
                && this.deviceType == device.deviceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.clusterId, this.deviceId, this.deviceType);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Device.class.getSimpleName() + "[", "]")
                .add("clusterId=" + this.clusterId)
                .add("deviceId=" + this.deviceId)
                .add("deviceType=" + this.deviceType)
                .toString();
    }
}
