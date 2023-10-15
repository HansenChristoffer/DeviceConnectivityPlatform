package io.miso.tcp;

import static io.miso.util.ConditionUtils.isNotNullAndNotEmpty;

import java.net.InetSocketAddress;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.miso.core.config.Configurator;
import io.miso.core.config.SecretConfig;
import io.miso.exceptions.InvalidMessageException;
import io.miso.util.BufferUtil;
import io.miso.util.SecurityUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

// TODO: We want to make sure to encrypt and HMAC calculate all outbound as well. Perhaps extend different or write a wrapper that handles both ways!
// TODO: Down the line, add DTLS to this handler. This should be added last, just to make testing and development easier for now!
public class TcpAuthHandler extends ChannelDuplexHandler {
    private static final Logger logger = LogManager.getFormatterLogger();

    private final byte[] hmacKey;
    private final byte[] aesKey;

    public TcpAuthHandler() {
        final SecretConfig config = Configurator.getConfig(SecretConfig.class);

        this.hmacKey = config.getHMAC_KEY().getBytes();
        this.aesKey = config.getAES_KEY().getBytes();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        Objects.requireNonNull(msg, String.format("%s 'msg' is not allowed to be null in %s!",
                msg.getClass().getSimpleName(), this.getClass().getSimpleName()));

        try {
            if (msg instanceof final ByteBuf buffer) {
                if ((isNotNullAndNotEmpty(hmacKey)) && (isNotNullAndNotEmpty(aesKey))) {
                    final byte[] encryptedBytes = SecurityUtil.validateHMAC(BufferUtil.getArray(buffer), hmacKey);

                    Objects.requireNonNull(encryptedBytes, "'encryptedBytes' is not allowed to be null, " +
                            "probably means that the message was invalid!");

                    final byte[] decryptedBytes = SecurityUtil.decrypt(encryptedBytes, aesKey);
                    Objects.requireNonNull(decryptedBytes, "'decryptedBytes' is not allowed to be null, " +
                            "probably means that the message was invalid!");

                    ctx.fireChannelRead(Unpooled.wrappedBuffer(decryptedBytes));
                } else {
                    logger.fatal("Unable to get HMAC or AES key from SecretConfig!");
                }
            } else {
                ctx.fireChannelRead(msg);
            }
        } catch (final InvalidMessageException e) {
            logger.warn("Invalid message from %s, does not have the correct HMAC. Discarding!",
                    ctx.channel().remoteAddress());
            ctx.channel().close();
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        logger.warn("Exception was caught in the %s! [from=%s]", this.getClass().getSimpleName(),
                ((InetSocketAddress) ctx.channel().remoteAddress()).toString());
        cause.printStackTrace();
    }
}
