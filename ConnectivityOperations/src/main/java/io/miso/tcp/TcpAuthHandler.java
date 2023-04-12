package io.miso.tcp;

import io.miso.core.config.Configurator;
import io.miso.core.config.SecretConfig;
import io.miso.exceptions.InvalidMessageException;
import io.miso.util.BufferUtil;
import io.miso.util.SecurityUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class TcpAuthHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static final Logger logger = LogManager.getFormatterLogger();
    private static final SecretConfig config = Configurator.getConfig(SecretConfig.class);

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final ByteBuf msg) throws Exception {
        Objects.requireNonNull(msg, String.format("ByteBuf 'msg' is not allowed to be null in %s!", this.getClass().getSimpleName()));

        try {
            final String hmacKey = config.getHMAC_KEY();
            final String aesKey = config.getAES_KEY();
            logger.debug("HMACKEY ::: %s", hmacKey);
            logger.debug("AESKEY ::: %s", aesKey);

            if ((hmacKey != null && !hmacKey.isBlank()) && (aesKey != null && !aesKey.isBlank())) {
                final byte[] encryptedBytes = SecurityUtil.validateHMAC(BufferUtil.getArray(msg), hmacKey.getBytes());
                Objects.requireNonNull(encryptedBytes, "'encryptedBytes' is not allowed to be null, probably means that the message was invalid!");

                final byte[] decryptedBytes = SecurityUtil.decrypt(encryptedBytes, aesKey.getBytes());
                Objects.requireNonNull(decryptedBytes, "'decryptedBytes' is not allowed to be null, probably means that the message was invalid!");

                ctx.fireChannelRead(Unpooled.wrappedBuffer(decryptedBytes));
            } else {
                logger.fatal("Unable to get HMAC or AES key from SecretConfig!");
            }
        } catch (final InvalidMessageException e) {
            logger.warn("Invalid message from %s, does not have the correct HMAC. Discarding!", ctx.channel().remoteAddress());
            ctx.channel().close();
        }
    }
}
