package io.miso.exception;

public class ConnectException extends RuntimeException {
    public ConnectException() {
    }

    public ConnectException(final String message) {
        super(message);
    }

    public ConnectException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
