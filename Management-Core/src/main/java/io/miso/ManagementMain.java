package io.miso;

import io.miso.core.DataServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class ManagementMain {
    private static final Logger logger = LogManager.getFormatterLogger();

    private static void configureLog4j2() {
        Configurator.initialize(null, "src/main/resources/log4j2.xml");
    }

    public static void main(final String... args) {
        configureLog4j2();
        startDatabaseOperations();
    }

    private static void startDatabaseOperations() {
        logger.info("ManagementMain starting up...");
        final DataServer ds = DataServer.getInstance();
    }
}
