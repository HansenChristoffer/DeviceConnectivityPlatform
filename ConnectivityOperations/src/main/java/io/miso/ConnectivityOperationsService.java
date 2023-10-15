package io.miso;

import io.miso.core.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectivityOperationsService implements Service {
    private static final Logger logger = LogManager.getFormatterLogger();

    private final InboundTcpServerManager inboundTcpServerManager;

    public ConnectivityOperationsService() {
        inboundTcpServerManager = new InboundTcpServerManager();
    }

    public ConnectivityOperationsService(final InboundTcpServerManager inboundTcpServerManager) {
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
