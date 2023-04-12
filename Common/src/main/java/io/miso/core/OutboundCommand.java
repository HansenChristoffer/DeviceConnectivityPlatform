package io.miso.core;

import java.util.Arrays;
import java.util.Optional;

public enum OutboundCommand {
    OC_PING(0x0001);

    private final int id;

    OutboundCommand(final int id) {
        this.id = id;
    }

    public static Optional<OutboundCommand> getFromId(final int id) {
        return Arrays.stream(OutboundCommand.values()).filter(oc -> oc.id == id).findFirst();
    }

    public int getId() {
        return id;
    }
}
