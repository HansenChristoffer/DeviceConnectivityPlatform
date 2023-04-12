package io.miso;

import io.miso.core.DatabaseOperationsService;
import io.miso.core.InboundCommand;
import io.miso.core.Service;
import io.miso.util.BufferUtil;
import io.miso.util.DataOutputHandler;
import io.miso.util.SecurityUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.Level;
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

public class ManagementMain {
    private static final Logger logger = LogManager.getFormatterLogger();

    private final List<Service> services = new CopyOnWriteArrayList<>();

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
            final byte[] encryptedBytes = SecurityUtil.encrypt(doh.toByteArray(), "testAES123456789".getBytes());

            // Add hmac
            final byte[] hmacEncryptedBytes = SecurityUtil.calculateHMAC(encryptedBytes, "testHMAC123456789".getBytes());

            // Send to server
            client.send(hmacEncryptedBytes);
        } catch (final InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void mainStart() {
        logger.info("ManagementMain starting up...");
        configureLog4j2();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdownServices));
        logger.info("ShutdownServices shutdownHook has been added!");

        //startDatabaseOperations();
        startConnectivityOperations();
        logger.info("ManagementMain is done setting everything up!");
    }

    private void startConnectivityOperations() {
        final ConnectivityOperationsService connectivityOperationsService = new ConnectivityOperationsService();
        services.add(connectivityOperationsService);
        connectivityOperationsService.start();
    }

    private void startDatabaseOperations() {
        final DatabaseOperationsService databaseOperationsService = new DatabaseOperationsService();
        services.add(databaseOperationsService);
        databaseOperationsService.start();

        try {
            Thread.sleep(60000);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        shutdownServices();
    }

    private void shutdownServices() {
        for (final Service s : services) {
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
                    .remoteAddress(new InetSocketAddress(host, port))
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(final SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                                        // Handle server response
                                        @Override
                                        protected void channelRead0(final ChannelHandlerContext ctx, final ByteBuf msg) {
                                            Objects.requireNonNull(msg, "The ByteBuf 'msg' is not allowed to be null!");

                                            final String hmacKey = "testHMAC123456789";
                                            final String aesKey = "testAES123456789";
                                            logger.debug("HMACKEY ::: %s", hmacKey);
                                            logger.debug("AESKEY ::: %s", aesKey);

                                            // Validate HMAC
                                            final byte[] encryptedBytes = SecurityUtil.validateHMAC(BufferUtil.getArray(msg), hmacKey.getBytes());

                                            if (encryptedBytes != null && encryptedBytes.length > 0) {
                                                final byte[] decryptedBytes = SecurityUtil.decrypt(encryptedBytes, aesKey.getBytes());

                                                if (decryptedBytes != null && decryptedBytes.length > 0) {
                                                    logger.printf(Level.INFO, "Got a response from server [%s], the response is: %s",
                                                            ctx.channel().remoteAddress(), Arrays.toString(decryptedBytes));
                                                } else {
                                                    logger.printf(Level.WARN, "Empty response?!? %s]", Arrays.toString(BufferUtil.getArray(msg)));
                                                }
                                            } else {
                                                logger.printf(Level.ERROR, "INVALID RESPONSE! [%s]", Arrays.toString(BufferUtil.getArray(msg)));
                                            }
                                        }
                                    });
                        }
                    });

            final ChannelFuture future = bootstrap.connect().sync();
            future.channel().writeAndFlush(Unpooled.copiedBuffer(data));
            future.channel().closeFuture().sync();
        }
    }
}
