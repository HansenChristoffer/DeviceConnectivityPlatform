package io.miso.util;

public class ConditionUtils {
    private ConditionUtils() {
        // Private for hiding the constructor.
    }

    public static boolean isNotNullAndNotBlank(final String s) {
        return s != null && !s.isBlank();
    }

    public static boolean isNotNullAndNotEmpty(final byte[] b) {
        return b != null && b.length > 0;
    }
}
