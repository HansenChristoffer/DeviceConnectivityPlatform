package io.miso.exception;

public class ConfigPropertyException extends RuntimeException {
    public ConfigPropertyException() {
        super();
    }

    public ConfigPropertyException(final String msg) {
        super(msg);
    }

    public ConfigPropertyException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
