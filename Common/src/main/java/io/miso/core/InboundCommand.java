package io.miso.core;

import java.util.Arrays;
import java.util.Optional;

public enum InboundCommand {
    IC_ACK(0x0000, true),
    IC_PING(0x0001, true);

    private final int id;
    private final boolean smsCapable;

    InboundCommand(final int id, final boolean smsCapable) {
        this.id = id;
        this.smsCapable = smsCapable;
    }

    public static Optional<InboundCommand> getFromId(final int id) {
        return Arrays.stream(InboundCommand.values()).filter(oc -> oc.id == id).findFirst();
    }

    public int getId() {
        return id;
    }

    public boolean isSmsCapable() {
        return smsCapable;
    }
}
