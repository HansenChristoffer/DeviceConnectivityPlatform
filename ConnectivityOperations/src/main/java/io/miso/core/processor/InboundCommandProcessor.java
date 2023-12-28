package io.miso.core.processor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.miso.core.InboundCommand;
import io.miso.util.BufferUtil;
import io.netty.buffer.ByteBuf;

public class InboundCommandProcessor {
    private static final Logger logger = LogManager.getFormatterLogger();

    @InboundCommandHandler(InboundCommand.IC_PING)
    public void handleInboundPing(final ByteBuf buffer) {
        logger.info("Hello OC_PING!");

        if (buffer != null && buffer.readableBytes() > 0) {
            logger.info("Bytes ::: [%s]", Arrays.toString(BufferUtil.getArray(buffer)));

            final long firstUnsignedInteger = buffer.readUnsignedInt();
            final long secondUnsignedInteger = buffer.readUnsignedInt();
            final Instant readInstant = Instant.ofEpochMilli(buffer.readLong());
            final short firstUnsignedByte = buffer.readUnsignedByte();

            logger.info(
                    "Got the following data: %n FirstUnsignedInteger :: %d%n SecondUnsignedInteger :: %d%n readInstant :: %s%n FirstUnsignedByte :: 0x%02X",
                    firstUnsignedInteger,
                    secondUnsignedInteger, readInstant != null ? readInstant.toString() : "N/A", firstUnsignedByte);
        }
    }

    public void invokeInboundCommandHandler(final InboundCommand command, final ByteBuf buffer)
            throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {

        final Method[] methods = this.getClass().getDeclaredMethods();
        for (final Method method : methods) {
            if (method.isAnnotationPresent(InboundCommandHandler.class)) {
                final InboundCommandHandler annotation = method.getAnnotation(InboundCommandHandler.class);
                if (annotation.value() == command) {
                    method.invoke(this, buffer);
                    return;
                }
            }
        }

        // Throw an exception if no matching method was found
        logger.info("No method found for InboundCommand: %s. Discarding %d number of bytes!",
                command.name(), buffer.capacity());
        throw new NoSuchMethodException("No method found for InboundCommand: " + command.name());
    }
}
