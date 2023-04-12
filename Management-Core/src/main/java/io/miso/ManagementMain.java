package io.miso;

import io.miso.config.ManagementMainConfig;
import io.miso.core.DatabaseOperationsService;
import io.miso.core.InboundCommand;
import io.miso.core.Service;
import io.miso.core.config.SecretConfig;
import io.miso.util.BufferUtil;
import io.miso.util.DataOutputHandler;
import io.miso.util.SecureRandomProvider;
import io.miso.util.SecurityUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ManagementMain {
    private static final Logger logger = LogManager.getFormatterLogger();
    private static final SecretConfig SECRET_CONFIG = io.miso.core.config.Configurator.getConfig(SecretConfig.class);
    private static final ManagementMainConfig MANAGEMENT_MAIN_CONFIG = io.miso.core.config.Configurator.getConfig(ManagementMainConfig.class);

    private final List<Service> services = new CopyOnWriteArrayList<>();

    private ExecutorService serviceExecutor;

    private static void configureLog4j2() {
        Configurator.initialize(null, "src/main/resources/log4j2.xml");
    }

    public static void main(final String... args) {
        if (args.length > 0) {
            logger.debug("Args length is greater than 0! %s", Arrays.toString(args));
            new ManagementMain().clientStart();
        } else {
            new ManagementMain().mainStart();
        }
    }

    private void clientStart() {
        final NettyClient client = new NettyClient("localhost", 8888);

        try {
            final DataOutputHandler doh = new DataOutputHandler();
            // Header
            doh.writeUnsignedShort(InboundCommand.IC_PING.getId());
            doh.writeUnsignedInt(123456L);
            doh.writeUnsignedInt(54321L);
            doh.writeUnsignedLong(Instant.now().toEpochMilli());

            // Payload
            doh.writeUnsignedByte((short) 0xFF);

            // Encrypt it
            final SecureRandomProvider randomProvider = new SecureRandomProvider();
            final byte[] encryptedBytes = SecurityUtil.encrypt(doh.toByteArray(), SECRET_CONFIG.getAES_KEY().getBytes(),
                    randomProvider.getRandomBytes(SECRET_CONFIG.getIV_block_size()));
            logger.debug("encryptedBytes ::: %s", Arrays.toString(encryptedBytes));

            // Add hmac
            final byte[] hmacEncryptedBytes = SecurityUtil.calculateHMAC(encryptedBytes, SECRET_CONFIG.getHMAC_KEY().getBytes());
            logger.debug("hmacEncryptedBytes ::: %s", Arrays.toString(hmacEncryptedBytes));

            // Send to server
            client.send(hmacEncryptedBytes);
        } catch (final InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void mainStart() {
        logger.info("ManagementMain starting up...");

        serviceExecutor = new ScheduledThreadPoolExecutor(MANAGEMENT_MAIN_CONFIG.getServiceMaxCoreSize());

        configureLog4j2();
        configureShutdownHook();
        startDatabaseOperations();
        startConnectivityOperations();

        logger.info("ManagementMain is done setting everything up!");
    }

    private void startConnectivityOperations() {
        final ConnectivityOperationsService connectivityOperationsService = new ConnectivityOperationsService();
        services.add(connectivityOperationsService);

        serviceExecutor.submit(connectivityOperationsService);
    }

    private void startDatabaseOperations() {
        final DatabaseOperationsService databaseOperationsService = new DatabaseOperationsService();
        services.add(databaseOperationsService);

        serviceExecutor.submit(databaseOperationsService);
    }

    private void configureShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdownServices));
    }

    private void shutdownServices() {
        for (final Service s : this.services) {
            s.stop();
        }
    }

    public static class NettyClient {
        private final String host;
        private final int port;

        public NettyClient(final String host, final int port) {
            this.host = host;
            this.port = port;
        }

        public void send(final byte[] data) throws InterruptedException {
            final Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(new NioEventLoopGroup())
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(this.host, this.port))
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(final SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                                        // Handle server response
                                        @Override
                                        protected void channelRead0(final ChannelHandlerContext ctx, final ByteBuf msg) {
                                            final SecretConfig secretConfig = io.miso.core.config.Configurator.getConfig(SecretConfig.class);
                                            Objects.requireNonNull(msg, "The ByteBuf 'msg' is not allowed to be null!");

                                            logger.debug("Received %s number of bytes from Server!",
                                                    BufferUtil.getArray(msg).length);

                                            // Validate HMAC
                                            final byte[] encryptedBytes = SecurityUtil
                                                    .validateHMAC(BufferUtil.getArray(msg), secretConfig.getHMAC_KEY()
                                                            .getBytes());

                                            logger.debug("There is %d total bytes after validating HMAC!",
                                                    encryptedBytes.length);

                                            if (encryptedBytes.length > 0) {
                                                final byte[] decryptedBytes = SecurityUtil.decrypt(encryptedBytes,
                                                        secretConfig.getAES_KEY().getBytes());

                                                if (decryptedBytes != null && decryptedBytes.length > 0) {
                                                    logger.debug("There is %d total bytes after decrypting HMAC!",
                                                            decryptedBytes.length);

                                                    logger.debug("Got a response from server [%s], the response is: %s",
                                                            ctx.channel().remoteAddress(), Arrays.toString(decryptedBytes));
                                                } else {
                                                    logger.warn("Empty response?!? %s]",
                                                            Arrays.toString(BufferUtil.getArray(msg)));
                                                }
                                            } else {
                                                logger.warn("INVALID RESPONSE! [%s]",
                                                        Arrays.toString(BufferUtil.getArray(msg)));
                                            }
                                        }
                                    });
                        }
                    });

            logger.debug("Sending %d total of bytes to Server!", data.length);
            final ChannelFuture future = bootstrap.connect().sync();
            future.channel().writeAndFlush(Unpooled.copiedBuffer(data));
            future.channel().closeFuture().sync();
        }
    }
}
