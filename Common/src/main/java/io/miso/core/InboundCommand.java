package io.miso.core;

import java.util.Arrays;
import java.util.Optional;

public enum InboundCommand {
    IC_ACK(0x0000),
    IC_PING(0x0001);

    private final int id;

    InboundCommand(final int id) {
        this.id = id;
    }

    public static Optional<InboundCommand> getFromId(final int id) {
        return Arrays.stream(InboundCommand.values()).filter(oc -> oc.id == id).findFirst();
    }

    public int getId() {
        return id;
    }
}
