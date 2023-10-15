package io.miso.device;

import java.util.Objects;

public enum DeviceType {
    LOGIC_DEVICE1((short) 0x0000, "LogicDevice1"),
    TEST_DEVICE1((short) 0x0001, "TestDevice1"),
    CLIMATE_DEVICE1((short) 0x0002, "ClimateDevice1"),
    VOIP_DEVICE1((short) 0x0003, "VoipDevice1"),
    INTERFACE_DEVICE1((short) 0x0004, "InterfaceDevice1"),
    SMOKE_DEVICE1((short) 0x0005, "SmokeDevice1"),
    CAMERA_DEVICE1((short) 0x0006, "CameraDevice1"),
    SWITCH_DEVICE1((short) 0x0007, "SwitchDevice1"),
    SMART_SWITCH_DEVICE1((short) 0x0008, "SmartSwitchDevice1"),
    SMART_VAULT_DEVICE1((short) 0x0009, "SmartVaultDevice1"),
    MANUAL_VAULT_DEVICE1((short) 0x0010, "ManualVaultDevice1")
    ;

    private final short deviceTypeId;

    private final String deviceTypeName;

    DeviceType(final short deviceTypeId, final String deviceTypeName) {
        this.deviceTypeId = deviceTypeId;
        this.deviceTypeName = deviceTypeName;
    }

    public static DeviceType getFromDeviceTypeId(final short deviceId) {
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

    public short getDeviceTypeId() {
        return deviceTypeId;
    }

    public String getDeviceTypeName() {
        return deviceTypeName;
    }
}
