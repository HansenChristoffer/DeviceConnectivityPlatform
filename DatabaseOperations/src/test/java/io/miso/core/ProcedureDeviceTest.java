package io.miso.core;

import io.miso.core.model.SystemDeviceXO;
import io.miso.core.repository.procedure.*;
import io.miso.device.DeviceType;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProcedureDeviceTest {
    private static final long CLUSTER_ID = 1L;
    private static final long DEVICE_ID = 1L;
    private static final String INTERNAL_REVISION = "0";
    private static SystemDeviceXO systemDeviceXO;

    @BeforeAll
    public static void beforeAll() {
        systemDeviceXO = new SystemDeviceXO.Builder()
                .setInternalId(null)
                .setInternalRevision(INTERNAL_REVISION)
                .setClusterId(CLUSTER_ID)
                .setDeviceId(DEVICE_ID)
                .setDeviceType(DeviceType.TEST_DEVICE1)
                .build();
    }

    @Test
    @Order(1)
    public void insertDeviceTest() {
        ProcedureInsertDevice procedure = new ProcedureInsertDevice(systemDeviceXO);
        assertDoesNotThrow(procedure::execute);
    }

    @Test
    @Order(2)
    public void findDeviceTest() {
        ProcedureFindDevice procedure = new ProcedureFindDevice(systemDeviceXO);
        Optional<SystemDeviceXO> device = procedure.execute();

        assertTrue(device.isPresent());
        assertEquals(CLUSTER_ID, device.get().getClusterId());
        assertEquals(DEVICE_ID, device.get().getDeviceId());
    }

    @Test
    @Order(3)
    public void replaceDeviceTest() {
        ProcedureReplaceDevice procedure = new ProcedureReplaceDevice(systemDeviceXO);
        assertDoesNotThrow(procedure::execute);
    }

    @Test
    @Order(4)
    public void updateDeviceTest() {
        SystemDeviceXO updateDeviceXO = new SystemDeviceXO.Builder()
                .setInternalId(systemDeviceXO.getInternalId())
                .setDeviceId(systemDeviceXO.getDeviceId())
                .setClusterId(systemDeviceXO.getClusterId())
                .setDeviceType(systemDeviceXO.getDeviceType())
                .setInternalRevision("1337")
                .build();

        ProcedureUpdateDevice procedure = new ProcedureUpdateDevice(updateDeviceXO);
        assertDoesNotThrow(procedure::execute);
    }

    @Test
    @Order(5)
    public void deleteDeviceTest() {
        ProcedureDeleteDevice procedure = new ProcedureDeleteDevice(systemDeviceXO);
        assertDoesNotThrow(procedure::execute);
    }
}
