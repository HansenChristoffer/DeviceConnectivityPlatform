package io.miso.core.repository.procedure;

import io.miso.core.StorageManager;
import io.miso.core.model.SystemDeviceXO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.Optional;

public class ProcedureReplaceDevice implements BaseProcedure<SystemDeviceXO> {
    private static final Logger logger = LogManager.getFormatterLogger();

    private final SystemDeviceXO systemDeviceXO;

    public ProcedureReplaceDevice(final SystemDeviceXO systemDeviceXO) {
        this.systemDeviceXO = Objects.requireNonNull(systemDeviceXO, "SystemDeviceXO is not allowed to be " +
                "null when trying to replace device!");
    }

    @Override
    public Optional<SystemDeviceXO> execute() {
        StorageManager<SystemDeviceXO> sm = new StorageManager<>("device", SystemDeviceXO.class);
        long modifiedCount = sm.replaceDocument(systemDeviceXO);

        if (modifiedCount == 0) {
            logger.warn("Was unable to replace device with id [%d] within the cluster with id [%d]",
                    systemDeviceXO.getDeviceId(), systemDeviceXO.getClusterId());
        }

        return Optional.empty();
    }
}
