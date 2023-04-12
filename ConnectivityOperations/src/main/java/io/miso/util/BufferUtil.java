package io.miso.util;

import io.netty.buffer.ByteBuf;

public class BufferUtil {
    private BufferUtil() {
    }

    public static byte[] getArray(final ByteBuf buf) {
        if (buf.hasArray()) {
            return buf.array();
        } else {
            final byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            return bytes;
        }
    }
}
