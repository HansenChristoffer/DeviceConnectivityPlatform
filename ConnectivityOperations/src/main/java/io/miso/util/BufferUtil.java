package io.miso.util;

import io.netty.buffer.ByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BufferUtil {
    private static final Logger logger = LogManager.getFormatterLogger();

    private BufferUtil() {
    }

    public static byte[] getArray(final ByteBuf buf) {
        if (buf.hasArray()) {
            // Use buf.arrayOffset() and buf.readableBytes() to correctly extract the readable portion of the buffer.
            byte[] readableArray = new byte[buf.readableBytes()];
            System.arraycopy(buf.array(), buf.arrayOffset() + buf.readerIndex(), readableArray, 0, buf.readableBytes());
            logger.info("Readable array size: %d bytes", readableArray.length);
            return readableArray;
        } else {
            final byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            logger.info("Bytes size: %d bytes", bytes.length);
            return bytes;
        }
    }
}
