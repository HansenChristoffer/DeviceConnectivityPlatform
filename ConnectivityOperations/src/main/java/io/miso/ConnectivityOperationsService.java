package io.miso;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.miso.core.Service;

public class ConnectivityOperationsService implements Service {
    private static final Logger logger = LogManager.getFormatterLogger();

    private final TcpServerManager inboundTcpServerManager;

    public ConnectivityOperationsService() {
        inboundTcpServerManager = new TcpServerManager();
    }

    public ConnectivityOperationsService(final TcpServerManager inboundTcpServerManager) {
        this.inboundTcpServerManager = inboundTcpServerManager;
    }

    @Override
    public void run() {
        logger.info("ConnectivityOperationsService starting up!");
        inboundTcpServerManager.start();
    }

    @Override
    public void stop() {
        logger.info("Shutting down InboundTcpServerManager!");
        inboundTcpServerManager.stop();
    }
}
