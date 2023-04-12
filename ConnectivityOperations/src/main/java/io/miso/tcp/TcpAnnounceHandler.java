package io.miso.tcp;

import io.miso.util.BufferUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Objects;

public class TcpAnnounceHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static final Logger logger = LogManager.getFormatterLogger();

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final ByteBuf msg) throws Exception {
        Objects.requireNonNull(msg, String.format("ByteBuf 'msg' not allowed to be null in %s!",
                this.getClass().getSimpleName()));

        logger.printf(Level.INFO, "Connection from %s with data message %s", ctx.channel().remoteAddress(),
                Arrays.toString(BufferUtil.getArray(msg)));

        ctx.fireChannelRead(msg);
    }
}

