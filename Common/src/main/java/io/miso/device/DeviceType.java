package io.miso.device;

import java.util.Objects;

public enum DeviceType {
    TEST_DEVICE(0x0000_0000_0000_0001, "TestDevice");

    private final long deviceTypeId;

    private final String deviceTypeName;

    DeviceType(final long deviceTypeId, final String deviceTypeName) {
        this.deviceTypeId = deviceTypeId;
        this.deviceTypeName = deviceTypeName;
    }

    public static DeviceType getFromDeviceTypeId(final Long deviceId) {
        Objects.requireNonNull(deviceId, "DeviceId is not allowed to be null!");

        for (final DeviceType e : DeviceType.values()) {
            if (e.getDeviceTypeId() == deviceId) {
                return e;
            }
        }

        return null;
    }

    public static DeviceType getFromDeviceTypeName(final String deviceName) {
        Objects.requireNonNull(deviceName, "DeviceName is not allowed to be null!");

        for (final DeviceType e : DeviceType.values()) {
            if (e.getDeviceTypeName().equalsIgnoreCase(deviceName)) {
                return e;
            }
        }

        return null;
    }

    public long getDeviceTypeId() {
        return deviceTypeId;
    }

    public String getDeviceTypeName() {
        return deviceTypeName;
    }
}
