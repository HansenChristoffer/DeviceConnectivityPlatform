package io.miso.core.repository.procedure;

import io.miso.core.StorageManager;
import io.miso.core.model.SystemDeviceXO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.BsonDocument;
import org.bson.BsonElement;
import org.bson.BsonInt64;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ProcedureFindDevice implements BaseProcedure<SystemDeviceXO> {
    private static final Logger logger = LogManager.getFormatterLogger();

    private final SystemDeviceXO systemDeviceXO;

    public ProcedureFindDevice(final SystemDeviceXO systemDeviceXO) {
        this.systemDeviceXO = Objects.requireNonNull(systemDeviceXO, "SystemDeviceXO is not allowed to be " +
                "null when trying to find device!");
    }

    @Override
    public Optional<SystemDeviceXO> execute() {
        StorageManager<SystemDeviceXO> sm = new StorageManager<>("device", SystemDeviceXO.class);
        List<SystemDeviceXO> result = sm.find(new BsonDocument(List.of(
                new BsonElement("cluster_id", new BsonInt64(systemDeviceXO.getClusterId())),
                new BsonElement("device_id", new BsonInt64(systemDeviceXO.getDeviceId())))));

        if (result.size() == 0) {
            logger.warn("Was unable to find the device with the id " +
                    "[%d] within the cluster with the id [%d]", systemDeviceXO.getDeviceId(), systemDeviceXO.getClusterId());
            return Optional.empty();
        }

        if (result.size() > 1) {
            logger.warn("Executed the %s and it resulted in more " +
                    "than 1 result, will return the first result!", this.getClass());
        }

        return Optional.of(result.get(0));
    }
}
