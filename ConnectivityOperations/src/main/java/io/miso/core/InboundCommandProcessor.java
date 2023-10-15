package io.miso.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.miso.util.BufferUtil;
import io.netty.buffer.ByteBuf;

public class InboundCommandProcessor {
    private static final Logger logger = LogManager.getFormatterLogger();

    public InboundCommandProcessor() {
        //
    }

    @InboundCommandHandler(InboundCommand.IC_PING)
    public void handleInboundPing(final ByteBuf buffer) {
        logger.info("Hello OC_PING!");

        if (buffer != null && buffer.readableBytes() > 0) {
            logger.info("Bytes ::: [%s]", Arrays.toString(BufferUtil.getArray(buffer)));
        }
    }

    public void invokeInboundCommandHandler(final InboundCommand command, final ByteBuf buffer) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        // Get all methods in the current class
        final Method[] methods = this.getClass().getDeclaredMethods();

        // Loop through all the methods
        for (final Method method : methods) {
            // Check if the method has the InboundCommandHandler annotation
            if (method.isAnnotationPresent(InboundCommandHandler.class)) {
                // Get the annotation from the method
                final InboundCommandHandler annotation = method.getAnnotation(InboundCommandHandler.class);
                // Check if the annotation's value matches the given command
                if (annotation.value() == command) {
                    // Invoke the method with the arguments
                    method.invoke(this, buffer);
                    // Return after invoking the first matching method found
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
