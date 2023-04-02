package io.miso.core;

import java.util.Arrays;
import java.util.Optional;

public enum OperationCommand {
    OC_PING(0x0001);

    private final int id;

    OperationCommand(final int id) {
        this.id = id;
    }

    public static Optional<OperationCommand> getFromId(final int id) {
        return Arrays.stream(OperationCommand.values()).filter(oc -> oc.id == id).findFirst();
    }

    public int getId() {
        return id;
    }
}
