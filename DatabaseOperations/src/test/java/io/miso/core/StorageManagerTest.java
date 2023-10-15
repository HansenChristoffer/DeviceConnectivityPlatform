package io.miso.core;

import io.miso.core.model.SystemClusterXO;
import io.miso.core.model.SystemDeviceXO;
import io.miso.device.DeviceType;
import org.bson.BsonDocument;
import org.bson.BsonElement;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.miso.util.ConditionUtils.isNotNullAndNotBlank;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StorageManagerTest {
    private static final long CLUSTER_ID = 1L;
    private static final long DEVICE_ID = 1L;
    private static final String INTERNAL_REVISION = "0";

    private static StorageManager<SystemDeviceXO> deviceStorageManager;
    private static StorageManager<SystemClusterXO> clusterStorageManager;

    private static SystemClusterXO systemClusterXO;
    private static SystemDeviceXO systemDeviceXO;

    @BeforeAll
    public static void beforeAll() {
        deviceStorageManager = new StorageManager<>("device", SystemDeviceXO.class);
        clusterStorageManager = new StorageManager<>("cluster", SystemClusterXO.class);

        systemDeviceXO = new SystemDeviceXO.Builder()
                .setInternalId(null)
                .setInternalRevision(INTERNAL_REVISION)
                .setClusterId(CLUSTER_ID)
                .setDeviceId(DEVICE_ID)
                .setDeviceType(DeviceType.TEST_DEVICE1)
                .build();

        systemClusterXO = new SystemClusterXO.Builder()
                .setInternalRevision(INTERNAL_REVISION)
                .setClusterId(CLUSTER_ID)
                .setSystemDeviceXOS(List.of(systemDeviceXO))
                .build();
    }

    @Test
    @Order(1)
    public void insertDeviceTest() {
        String resId = deviceStorageManager.insertDocument(systemDeviceXO);
        assertTrue(isNotNullAndNotBlank(resId));
    }

    @Test
    @Order(2)
    public void findDeviceTest() {
        List<SystemDeviceXO> result = deviceStorageManager.find(new BsonDocument(List.of(
                new BsonElement("cluster_id", new BsonInt64(systemDeviceXO.getClusterId())),
                new BsonElement("device_id", new BsonInt64(systemDeviceXO.getDeviceId())))));

        assertEquals(1, result.size());
    }

    @Test
    @Order(3)
    public void replaceDeviceTest() {
        long modifiedCount = deviceStorageManager.replaceDocument(systemDeviceXO);
        assertEquals(1, modifiedCount);
    }

    @Test
    @Order(4)
    public void updateDeviceTest() {
        long modifiedCount = deviceStorageManager.updateDocument(new BsonDocument(List.of(
                        new BsonElement("device_id", new BsonInt64(systemDeviceXO.getDeviceId())),
                        new BsonElement("cluster_id", new BsonInt64(systemDeviceXO.getClusterId())))),
                new BsonDocument("$set", new BsonDocument("internal_revision", new BsonString("1337"))));
        assertEquals(1, modifiedCount);
    }

    @Test
    @Order(5)
    public void deleteDeviceTest() {
        long deleteCount = deviceStorageManager.deleteDocument(new BsonDocument("internal_revision",
                new BsonString("1337")));
        assertEquals(1, deleteCount);
    }

    @Test
    @Order(6)
    public void nullCheckTest() {
        assertThrows(NullPointerException.class, () -> deviceStorageManager.insertDocument(null));
        assertThrows(NullPointerException.class, () -> deviceStorageManager.updateDocument(null, null));
        assertThrows(NullPointerException.class, () -> deviceStorageManager.replaceDocument(null));
        assertThrows(NullPointerException.class, () -> deviceStorageManager.find(null));
        assertThrows(NullPointerException.class, () -> deviceStorageManager.deleteDocument(null));
    }
}
