package io.miso.tcp;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.miso.util.BufferUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TcpAnnounceHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LogManager.getFormatterLogger();

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        Objects.requireNonNull(msg, String.format("%s 'msg' not allowed to be null in %s!",
                msg.getClass().getSimpleName(), this.getClass().getSimpleName()));

        if (msg instanceof final ByteBuf buffer) {
            logger.info("Connection from %s with data message %s", ctx.channel().remoteAddress(),
                    Arrays.toString(BufferUtil.getArray(buffer)));

            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        logger.warn("Exception was caught in the %s! [from=%s]", this.getClass().getSimpleName(),
                ((InetSocketAddress) ctx.channel().remoteAddress()).toString());
        cause.printStackTrace();
    }
}
