package io.miso.tcp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.miso.core.config.Configurator;
import io.miso.core.config.NettyConfig;
import io.miso.core.handler.PipelineStep;
import io.miso.exceptions.InvalidConfiguration;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TcpNettyServer implements Runnable {
    private static final Logger logger = LogManager.getFormatterLogger();

    private final int tcpPort;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private ChannelFuture channelFuture;

    public TcpNettyServer() {
        final NettyConfig nettyConfig = Configurator.getConfig(NettyConfig.class);

        if (nettyConfig.getTcpPort() == null) {
            throw new InvalidConfiguration("Did not find 'tcpPort' from NettyConfig!");
        }

        this.tcpPort = nettyConfig.getTcpPort();
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
    }

    @Override
    public void run() {
        try {
            // Create a new ServerBootstrap to configure and start the server
            logger.info("%s being established!", this.getClass().getSimpleName());

            System.setProperty("io.netty.leakDetection.level", "PARANOID");

            final ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(final SocketChannel ch) throws Exception {
                            // Create a new pipeline for the Netty server
                            final ChannelPipeline pipeline = ch.pipeline();

                            // Add the acknowledgment handler to the pipeline
                            pipeline.addLast(PipelineStep.ACK.name(), new TcpAckHandler());
                            // Add the announcer handler to the pipeline
                            pipeline.addLast(PipelineStep.ANNOUNCER.name(), new TcpAnnounceHandler());
                            // Add the authentication handler to the pipeline
                            pipeline.addLast(PipelineStep.AUTH.name(), new TcpAuthHandler());
                            // Add the message handler to the pipeline
                            pipeline.addLast(PipelineStep.MESSAGE.name(), new TcpMessageHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Start the server and bind to the specified port
            channelFuture = b.bind(this.tcpPort).sync();
            logger.info("%s has been started and is listening on port %d!",
                    this.getClass().getSimpleName(), this.tcpPort);

            // Wait until the server socket is closed
            channelFuture.channel().closeFuture().sync();
        } catch (final InterruptedException e) {
            logger.fatal("Thread got interrupted, will try to re-interrupt stalled thread!", e);
            Thread.currentThread().interrupt();
        } finally {
            shutdownEventGroups();
        }
    }

    public void stopServer() {
        if (this.channelFuture != null && !this.channelFuture.isVoid() && !this.channelFuture.isDone()
                && !this.channelFuture.isCancelled()) {
            this.channelFuture.channel().close().syncUninterruptibly();
        }
        shutdownEventGroups();
    }

    private void shutdownEventGroups() {
        // Shut down the event loop groups when the server is stopped
        logger.info("Trying to gracefully shutdown both worker and boss groups for %s!",
                this.getClass().getSimpleName());

        if (workerGroup != null && !workerGroup.isShuttingDown() && !workerGroup.isShutdown()
                && !workerGroup.isTerminated()) {
            workerGroup.shutdownGracefully();
        }

        if (bossGroup != null && !bossGroup.isShuttingDown() && !bossGroup.isShutdown()
                && !bossGroup.isTerminated()) {
            bossGroup.shutdownGracefully();
        }
    }
}
