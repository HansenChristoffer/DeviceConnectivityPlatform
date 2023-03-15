package io.miso.exceptions;

public class InvalidSMSMessage extends RuntimeException {
    public InvalidSMSMessage() {
        super();
    }

    public InvalidSMSMessage(final String msg) {
        super(msg);
    }

    public InvalidSMSMessage(final String msg, final Throwable cause) {
        super(msg, cause);
    }

    public InvalidSMSMessage(final Throwable cause) {
        super(cause);
    }

}
