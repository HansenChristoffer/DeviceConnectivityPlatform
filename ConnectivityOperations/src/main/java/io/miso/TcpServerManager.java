package io.miso;

import io.miso.tcp.TcpNettyServer;

public class TcpServerManager {
    private static final TcpNettyServer tcpNettyServer = new TcpNettyServer();
    private static final Thread tcpNettyServerThread = new Thread(tcpNettyServer);

    public TcpServerManager() {
        tcpNettyServerThread.setName("TcpNettyServer-Thread-1");
    }

    public void start() {
        tcpNettyServerThread.start();
    }

    public void stop() {
        tcpNettyServer.stopServer();
    }
}
