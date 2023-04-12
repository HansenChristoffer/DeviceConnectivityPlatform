package io.miso;

import io.miso.core.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

public class ConnectivityOperationsService implements Service {
    private static final Logger logger = LogManager.getFormatterLogger();

    private List<InboundTcpServerManager> serverManagers;

    @Override
    public void run() {
        logger.info("ConnectivityOperationsService starting up!");

        final InboundTcpServerManager inboundTcpServerManager = new InboundTcpServerManager();
        serverManagers = Collections.singletonList(inboundTcpServerManager);
        inboundTcpServerManager.start();
    }

    @Override
    public void stop() {
        if (serverManagers != null) {
            logger.info("Shutting down InboundTcpServerManagers!");
            for (final InboundTcpServerManager sm : serverManagers) {
                sm.stop();
            }
        }
    }
}
