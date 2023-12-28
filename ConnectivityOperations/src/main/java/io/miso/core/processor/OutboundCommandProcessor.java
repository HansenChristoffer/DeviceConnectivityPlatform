package io.miso.core.processor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.miso.core.InboundCommand;
import io.miso.core.OutboundCommand;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class OutboundCommandProcessor {
    private static final Logger logger = LogManager.getFormatterLogger();

    @OutboundCommandHandler(OutboundCommand.OC_PING)
    public ByteBuf handleOutboundPing(final ByteBuf buffer) {
        return createBasicAckMessage();
    }

    public void invokeInboundCommandHandler(final OutboundCommand command, final ByteBuf buffer)
            throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        final Method[] methods = this.getClass().getDeclaredMethods();

        for (final Method method : methods) {
            if (method.isAnnotationPresent(OutboundCommandHandler.class)) {
                final OutboundCommandHandler annotation = method.getAnnotation(OutboundCommandHandler.class);
                if (annotation.value() == command) {
                    method.invoke(this, buffer);
                    return;
                }
            }
        }

        // Throw an exception if no matching method was found
        logger.warn("No method found for InboundCommand: %s. Discarding %d number of bytes!",
                command.name(), buffer.capacity());
        throw new NoSuchMethodException("No method found for InboundCommand: " + command.name());
    }

    private ByteBuf createBasicAckMessage() {
        final ByteBuf ackBuf = Unpooled.buffer();
        final Instant now = Instant.now();

        ackBuf.writeShort(InboundCommand.IC_ACK.getId());
        ackBuf.writeLong(now.toEpochMilli());

        return ackBuf;
    }
}
