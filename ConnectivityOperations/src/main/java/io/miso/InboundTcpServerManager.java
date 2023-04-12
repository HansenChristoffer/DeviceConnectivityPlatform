package io.miso;

import io.miso.tcp.TcpNettyServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InboundTcpServerManager {
    private static final int THREAD_POOL_SIZE = 1;

    private final ExecutorService executor;

    public InboundTcpServerManager() {
        this.executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public void start() {
        for (int i = 0; i < THREAD_POOL_SIZE; i++) {
            final TcpNettyServer server = new TcpNettyServer();
            executor.execute(server);
        }
    }

    public void stop() {
        executor.shutdown();
    }
}
