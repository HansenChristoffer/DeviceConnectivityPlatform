package io.miso;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import io.miso.config.ManagementMainConfig;
import io.miso.core.DatabaseOperationsService;
import io.miso.core.Service;

public class ManagementMain {
    private static final Logger logger = LogManager.getFormatterLogger();
    private static final ManagementMainConfig MANAGEMENT_MAIN_CONFIG = io.miso.core.config.Configurator.getConfig(ManagementMainConfig.class);

    private final List<Service> services = new CopyOnWriteArrayList<>();

    private ExecutorService serviceExecutor;

    private static void configureLog4j2() {
        Configurator.initialize(null, "src/main/resources/log4j2.xml");
    }

    public static void main(final String... args) {
        new ManagementMain().mainStart();
    }

    private void mainStart() {
        logger.info("ManagementMain starting up...");
        serviceExecutor = new ScheduledThreadPoolExecutor(MANAGEMENT_MAIN_CONFIG.getServiceMaxCoreSize());

        configureLog4j2();
        configureShutdownHook();
        startDatabaseOperations();
        startConnectivityOperations();

        logger.info("ManagementMain is done setting everything up!");
    }

    private void startConnectivityOperations() {
        final ConnectivityOperationsService connectivityOperationsService = new ConnectivityOperationsService();
        services.add(connectivityOperationsService);
        serviceExecutor.submit(connectivityOperationsService);
    }

    private void startDatabaseOperations() {
        final DatabaseOperationsService databaseOperationsService = new DatabaseOperationsService();
        services.add(databaseOperationsService);
        serviceExecutor.submit(databaseOperationsService);
    }

    private void configureShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdownServices));
    }

    private void shutdownServices() {
        for (final Service s : this.services) {
            s.stop();
        }
    }
}
