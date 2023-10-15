package io.miso;

import io.miso.tcp.TcpNettyServer;
import org.apache.commons.lang3.NotImplementedException;

public class InboundTcpServerManager {
    private static final TcpNettyServer TCP_NETTY_SERVER = new TcpNettyServer();
    private static final Thread TCP_THREAD = new Thread(TCP_NETTY_SERVER);

    public InboundTcpServerManager() {
        TCP_THREAD.setName("TcpNettyServer-Thread-1");
    }

    public void start() {
        TCP_THREAD.start();
    }

    public void stop() {
        throw new NotImplementedException("stop() has not been implemented yet");
    }
}
