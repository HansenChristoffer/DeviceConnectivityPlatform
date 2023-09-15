package io.miso;

import io.miso.core.DatabaseOperationsService;
import io.miso.core.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.net.ConnectException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ManagementMain {
    private static final Logger logger = LogManager.getFormatterLogger();

    private final List<Service> services = new CopyOnWriteArrayList<>();

    private static void configureLog4j2() {
        Configurator.initialize(null, "src/main/resources/log4j2.xml");
    }

    public static void main(final String... args) {
        new ManagementMain().mainStart();
    }

    private void mainStart() {
        logger.info("ManagementMain starting up...");

        configureLog4j2();
        configureShutdownHook();
        startDatabaseOperations();

        logger.info("ManagementMain is done setting everything up!");
    }

    private void startDatabaseOperations() {
        final DatabaseOperationsService databaseOperationsService = new DatabaseOperationsService();
        services.add(databaseOperationsService);

        try {
            databaseOperationsService.start();
        } catch (final ConnectException ce) {
            logger.error("Failed to connect to database!", ce);
        }
    }

    private void configureShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdownServices));
    }

    private void shutdownServices() {
        for (final Service s : services) {
            s.stop();
        }
    }
}
