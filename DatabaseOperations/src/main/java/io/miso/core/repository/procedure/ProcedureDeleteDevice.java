package io.miso.core.repository.procedure;

import io.miso.core.StorageManager;
import io.miso.core.model.SystemDeviceXO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.BsonDocument;
import org.bson.BsonElement;
import org.bson.BsonInt64;
import org.bson.BsonString;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static io.miso.util.ConditionUtils.isNotNullAndNotBlank;

public class ProcedureDeleteDevice implements BaseProcedure<SystemDeviceXO> {
    private static final Logger logger = LogManager.getFormatterLogger();

    private final SystemDeviceXO systemDeviceXO;

    public ProcedureDeleteDevice(final SystemDeviceXO systemDeviceXO) {
        this.systemDeviceXO = Objects.requireNonNull(systemDeviceXO, "SystemDeviceXO is not allowed to be " +
                "null when trying to delete device!");
    }

    @Override
    public Optional<SystemDeviceXO> execute() {
        StorageManager<SystemDeviceXO> sm = new StorageManager<>("device", SystemDeviceXO.class);
        long deleteCount = 0L;

        if (isNotNullAndNotBlank(systemDeviceXO.getInternalId())) {
            deleteCount = sm.deleteDocument(new BsonDocument("internal_id", new BsonString(systemDeviceXO.getInternalId())));
        } else if (systemDeviceXO.getClusterId() > 0 && systemDeviceXO.getDeviceId() > 0) {
            deleteCount = sm.deleteDocument(new BsonDocument(List.of(
                    new BsonElement("cluster_id", new BsonInt64(systemDeviceXO.getClusterId())),
                    new BsonElement("device_id", new BsonInt64(systemDeviceXO.getDeviceId()))
            )));
        }

        if (deleteCount == 0) {
            logger.warn("Was unable to delete device with id [%d] within the cluster with id [%d]",
                    systemDeviceXO.getDeviceId(), systemDeviceXO.getClusterId());
        }

        return Optional.empty();
    }
}
