package io.miso.tcp;

import static io.miso.util.ConditionUtils.isNotNullAndNotEmpty;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

public class TcpAckHandler extends ChannelOutboundHandlerAdapter {
    private static final Logger logger = LogManager.getFormatterLogger();

    private final byte[] hmacKey;
    private final byte[] aesKey;
    private final int blockSize;

    public TcpAckHandler() {
        final SecretConfig secretConfig = Configurator.getConfig(SecretConfig.class);

        this.hmacKey = secretConfig.getHMAC_KEY().getBytes(StandardCharsets.ISO_8859_1);
        this.aesKey = secretConfig.getAES_KEY().getBytes(StandardCharsets.ISO_8859_1);
        this.blockSize = secretConfig.getIV_block_size();
    }

    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) {
        Objects.requireNonNull(msg, String.format("%s 'msg' is not allowed to be null in %s!",
                msg.getClass().getSimpleName(), this.getClass().getSimpleName()));

        if (isNotNullAndNotEmpty(hmacKey) && isNotNullAndNotEmpty(aesKey) && blockSize > 0) {
            final byte[] inIV = new byte[blockSize];

            if (msg instanceof final ByteBuf inMsg) {
                System.arraycopy(inMsg.array(), 0, inIV, 0, inIV.length);
            }

            logger.debug(" IV == %s", inIV.length > 0 ? Arrays.toString(inIV) : "EMPTY");

            final ByteBuf retBuf = Unpooled.buffer();
            logger.info("Initial buffer size: %d bytes", retBuf.readableBytes());

            // Encrypt the message
            final SecureRandomProvider randomProvider = new SecureRandomProvider();
            final byte[] encryptedBytes = SecurityUtil.encrypt(BufferUtil.getArray(createBasicAckMessage()),
                    aesKey, inIV.length > 0 ? inIV : randomProvider.getRandomBytes(blockSize));
            logger.info("Size of encrypted data: %d bytes", encryptedBytes.length);
            logger.info("EncryptedBytes: %s", encryptedBytes.length > 0 ? Arrays.toString(encryptedBytes) : "N/A");

            // Calculate HMAC of the message
            final byte[] hmacBytes = SecurityUtil.calculateHMAC(encryptedBytes, this.hmacKey);
            logger.info("HMAC-Bytes: %s", hmacBytes.length > 0 ? Arrays.toString(hmacBytes) : "N/A");

            // Add calculated HMAC bytes to encrypted bytes and write to returning buffer
            final byte[] dataWithHmacBytes = SecurityUtil.addHMAC(hmacBytes, encryptedBytes);
            retBuf.writeBytes(dataWithHmacBytes);
            logger.info("Buffer size after writing HMAC and encrypted bytes: %d bytes", retBuf.readableBytes());

            logger.debug("Message we're sending: %s",
                    retBuf != null ? Arrays.toString(BufferUtil.getArray(retBuf)) : "N/A");
            ctx.writeAndFlush(retBuf);

            retBuf.clear(); // To make sure we do not have anything lingering. Should not be the case as
                            // this frame gets discarded after success.

            // Notify the promise that the message was successfully sent
            promise.setSuccess();
        } else {
            logger.fatal("HMAC & AES not allowed to be null and blockSize is required to be greater than 0!");
        }
    }

    private ByteBuf createBasicAckMessage() {
        final ByteBuf ackBuf = Unpooled.buffer();

        // Write the IC_ACK command to the buffer
        ackBuf.writeShort(InboundCommand.IC_ACK.getId());

        // Write the current time to the buffer
        final Instant now = Instant.now();
        ackBuf.writeLong(now.toEpochMilli());

        logger.debug("BasicAckMessages size when created: %d bytes", ackBuf.readableBytes());
        return ackBuf;
    }
}
