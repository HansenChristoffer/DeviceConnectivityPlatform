package io.miso;

import io.miso.core.DatabaseOperationsService;
import io.miso.core.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

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
        startDatabaseOperations();
        logger.info("ManagementMain is done setting everything up!");
    }

    private void startDatabaseOperations() {
        final DatabaseOperationsService databaseOperationsService = new DatabaseOperationsService();
        services.add(databaseOperationsService);
        databaseOperationsService.start();

        try {
            Thread.sleep(60000);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        shutdownServices();
    }

    private void shutdownServices() {
        for (final Service s : services) {
            s.stop();
        }
    }
}
