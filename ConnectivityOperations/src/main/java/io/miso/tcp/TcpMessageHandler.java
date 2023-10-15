package io.miso.tcp;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.miso.core.InboundCommand;
import io.miso.core.InboundCommandProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TcpMessageHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LogManager.getFormatterLogger();

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws InvocationTargetException,
            NoSuchMethodException, IllegalAccessException {
        if (msg instanceof final ByteBuf buffer) {
            // Read the first unsigned short from the buffer
            final int cmdId = buffer.readUnsignedShort();

            // Find the corresponding command in the InboundCommand enum
            final Optional<InboundCommand> cmd = InboundCommand.getFromId(cmdId);

            if (cmd.isPresent()) {
                // Invoke the command handler in the InboundCommandProcessor
                final InboundCommandProcessor inboundCommandProcessor = new InboundCommandProcessor();
                inboundCommandProcessor.invokeInboundCommandHandler(cmd.get(), buffer);

                // Send the ack message back to the client
                ctx.writeAndFlush(msg);
            } else {
                // Handle the case where the command is not recognized
                logger.warn("Got message from %s, which had an unrecognized command with the id %d!", ctx.channel().remoteAddress(), cmdId);
                ctx.close();
            }
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        logger.warn("Exception was caught in the %s! [from=%s]", this.getClass().getSimpleName(),
                ((InetSocketAddress) ctx.channel().remoteAddress()).toString());
        cause.printStackTrace();
    }
}
