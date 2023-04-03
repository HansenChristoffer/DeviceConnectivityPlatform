package io.miso.core.model;

import io.miso.device.DeviceType;

import java.util.Objects;

public class Device {
    private String internalId;

    private String internalRevision;

    private long clusterId;

    private long deviceId;

    private DeviceType deviceType;

    public Device() {
        // For de-/se-rialization
    }

    public Device(final String internalId, final String internalRevision, final long clusterId,
                  final long deviceId, final DeviceType deviceType) {
        this.internalId = internalId;
        this.internalRevision = internalRevision;
        this.clusterId = clusterId;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }

    public String getInternalId() {
        return internalId;
    }

    public Device setInternalId(final String internalId) {
        this.internalId = internalId;
        return this;
    }

    public String getInternalRevision() {
        return internalRevision;
    }

    public Device setInternalRevision(final String internalRevision) {
        this.internalRevision = internalRevision;
        return this;
    }

    public long getClusterId() {
        return clusterId;
    }

    public Device setClusterId(final long clusterId) {
        this.clusterId = clusterId;
        return this;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public Device setDeviceId(final long deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public Device setDeviceType(final DeviceType deviceType) {
        this.deviceType = deviceType;
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
        final Device device = (Device) o;
        return getClusterId() == device.getClusterId() && getDeviceId() == device.getDeviceId()
                && Objects.equals(getInternalId(), device.getInternalId())
                && Objects.equals(getInternalRevision(), device.getInternalRevision())
                && getDeviceType() == device.getDeviceType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInternalId(), getInternalRevision(), getClusterId(), getDeviceId(), getDeviceType());
    }

    @Override
    public String toString() {
        return "Device{" +
                "internalId='" + internalId + '\'' +
                ", internalReversion='" + internalRevision + '\'' +
                ", clusterId=" + clusterId +
                ", deviceId=" + deviceId +
                ", deviceType=" + deviceType +
                '}';
    }
}
