package io.miso;

import io.miso.tcp.TcpNettyServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Fix this whole thing. I am not sure why I ever thought it would be smart to have TcpNettyServer
// being ran async when only one is suppose to be running at one given time...
public class InboundTcpServerManager {
    private static final int THREAD_POOL_SIZE = 1;

    private final ExecutorService executor;

    public InboundTcpServerManager() {
        this.executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public void start() {
        for (int i = 0; i < THREAD_POOL_SIZE; i++) {
            final TcpNettyServer server = new TcpNettyServer();
            this.executor.execute(server);
        }
    }

    public void stop() {
        this.executor.shutdown();
    }
}
