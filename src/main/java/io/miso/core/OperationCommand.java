package io.miso.core;

import java.util.Arrays;
import java.util.Optional;

public enum OperationCommand {
    OC_PING((short) 0x0001);

    private final short id;

    OperationCommand(final short id) {
        this.id = id;
    }

    public static Optional<OperationCommand> getFromId(final short id) {
        return Arrays.stream(OperationCommand.values()).filter(oc -> oc.id == id).findFirst();
    }

    public short getId() {
        return id;
    }
}
