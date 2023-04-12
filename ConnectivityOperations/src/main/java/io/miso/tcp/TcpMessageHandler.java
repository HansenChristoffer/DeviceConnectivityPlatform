package io.miso.tcp;

import io.miso.core.InboundCommand;
import io.miso.core.InboundCommandProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class TcpMessageHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static final Logger logger = LogManager.getFormatterLogger();

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final ByteBuf buffer) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        // Read the first unsigned short from the buffer
        final int cmdId = buffer.readUnsignedShort();

        // Find the corresponding command in the InboundCommand enum
        final Optional<InboundCommand> cmd = InboundCommand.getFromId(cmdId);

        if (cmd.isPresent()) {
            // Invoke the command handler in the InboundCommandProcessor
            InboundCommandProcessor.getInstance().invokeInboundCommandHandler(cmd.get(), buffer);

            // Send the ack message back to the client
            ctx.fireChannelRead(buffer);
        } else {
            // Handle the case where the command is not recognized
            logger.warn("Got message from %s, which had an unrecognized command with the id %d!", ctx.channel().remoteAddress(), cmdId);
            ctx.close();
        }
    }
}
