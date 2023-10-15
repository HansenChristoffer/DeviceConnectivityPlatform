package io.miso.core.repository.procedure;

import io.miso.core.StorageManager;
import io.miso.core.model.SystemDeviceXO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.Optional;

public class ProcedureInsertDevice implements BaseProcedure<SystemDeviceXO> {
    private static final Logger logger = LogManager.getFormatterLogger();

    private final SystemDeviceXO systemDeviceXO;

    public ProcedureInsertDevice(final SystemDeviceXO systemDeviceXO) {
        this.systemDeviceXO = Objects.requireNonNull(systemDeviceXO, "SystemDeviceXO is not allowed to be " +
                "null when trying to insert device!");
    }

    @Override
    public Optional<SystemDeviceXO> execute() {
        StorageManager<SystemDeviceXO> sm = new StorageManager<>("device", SystemDeviceXO.class);
        String resId = sm.insertDocument(systemDeviceXO);

        if (resId.isEmpty()) {
            logger.warn("Was unable to insert device with id [%d] within the cluster with id [%d]",
                    systemDeviceXO.getDeviceId(), systemDeviceXO.getClusterId());
        }

        return Optional.empty();
    }
}
