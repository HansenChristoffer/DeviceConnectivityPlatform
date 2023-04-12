package io.miso.tcp;

import io.miso.core.InboundCommand;
import io.miso.core.config.Configurator;
import io.miso.core.config.SecretConfig;
import io.miso.exceptions.InvalidMessageException;
import io.miso.util.BufferUtil;
import io.miso.util.SecurityUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;

public class TcpAckHandler extends ChannelOutboundHandlerAdapter {
    private static final Logger logger = LogManager.getFormatterLogger();
    private static final SecretConfig config = Configurator.getConfig(SecretConfig.class);

    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
        if (msg != null) {
            final ByteBuf retBuf = Unpooled.buffer();

            // Encrypt the message
            final byte[] encryptedBytes = SecurityUtil.encrypt(BufferUtil.getArray(createBasicAckMessage()), config.getAES_KEY().getBytes());

            // Add HMAC to the message
            final byte[] hmacBytes = SecurityUtil.calculateHMAC(encryptedBytes, config.getHMAC_KEY().getBytes());
            retBuf.writeBytes(hmacBytes);

            // Add encrypted bytes to the message
            retBuf.writeBytes(encryptedBytes);

            // Write the combined HMAC and encrypted message to the channel
            ctx.writeAndFlush(retBuf);

            // Notify the promise that the message was successfully sent
            promise.setSuccess();
        } else {
            promise.setFailure(new InvalidMessageException("Invalid message!"));
            ctx.close();
        }
    }

    private ByteBuf createBasicAckMessage() {
        final ByteBuf ackBuf = Unpooled.buffer();

        // Write the IC_ACK command to the buffer
        ackBuf.writeShort(InboundCommand.IC_ACK.getId());

        // Write the current time to the buffer
        final Instant now = Instant.now();
        ackBuf.writeLong(now.toEpochMilli());

        return ackBuf;
    }
}
