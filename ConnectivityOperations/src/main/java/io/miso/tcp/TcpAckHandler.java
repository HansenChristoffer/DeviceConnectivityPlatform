package io.miso.tcp;

import io.miso.core.InboundCommand;
import io.miso.core.config.Configurator;
import io.miso.core.config.SecretConfig;
import io.miso.util.BufferUtil;
import io.miso.util.SecureRandomProvider;
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

    private final byte[] hmacKey;
    private final byte[] aesKey;
    private final int blockSize;

    public TcpAckHandler() {
        final SecretConfig secretConfig = Configurator.getConfig(SecretConfig.class);

        this.hmacKey = secretConfig.getHMAC_KEY().getBytes();
        this.aesKey = secretConfig.getAES_KEY().getBytes();
        this.blockSize = secretConfig.getIV_block_size();
    }

    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) {
        final ByteBuf retBuf = Unpooled.buffer();
        logger.info("Initial buffer size: %d bytes", retBuf.readableBytes());

        // Encrypt the message
        final SecureRandomProvider randomProvider = new SecureRandomProvider();
        final byte[] encryptedBytes = SecurityUtil.encrypt(BufferUtil.getArray(createBasicAckMessage()),
                aesKey, randomProvider.getRandomBytes(blockSize));
        logger.info("Size of encrypted data: %d bytes", encryptedBytes.length);

        // Add HMAC to the message
        final byte[] hmacBytes = SecurityUtil.calculateHMAC(encryptedBytes, this.hmacKey);
        retBuf.writeBytes(hmacBytes);
        logger.info("Buffer size after writing HMAC: %d bytes", retBuf.readableBytes());

        // Add encrypted bytes to the message
        retBuf.writeBytes(encryptedBytes);
        logger.info("Buffer size after writing encrypted data: %d bytes", retBuf.readableBytes());

        // Write the combined HMAC and encrypted message to the channel
        ctx.writeAndFlush(retBuf);

        retBuf.clear();

        // Notify the promise that the message was successfully sent
        promise.setSuccess();
    }

    private ByteBuf createBasicAckMessage() {
        final ByteBuf ackBuf = Unpooled.buffer();

        // Write the IC_ACK command to the buffer
        ackBuf.writeShort(InboundCommand.IC_ACK.getId());

        // Write the current time to the buffer
        final Instant now = Instant.now();
        ackBuf.writeLong(now.toEpochMilli());

        logger.info("BasicAckMessages size when created: %d bytes", ackBuf.readableBytes());

        return ackBuf;
    }
}
