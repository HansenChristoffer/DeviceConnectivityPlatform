package io.miso.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.miso.config.DatabaseOperationsServiceConfig;
import io.miso.core.config.Configurator;
import io.miso.exception.ConnectException;

public class DatabaseOperationsService implements Service {
    private static final Logger logger = LogManager.getFormatterLogger();
    private final long waitTime;
    private DataServer dataServer;
    private DatabaseAliveChecker databaseAliveChecker;

    public DatabaseOperationsService() {
        final DatabaseOperationsServiceConfig config = Configurator.getConfig(DatabaseOperationsServiceConfig.class);
        waitTime = config.getDatabaseAliveCheckerWaitTime();

        if (waitTime <= 10000L || waitTime >= 200000L) {
            throw new IllegalArgumentException("DatabaseAliveCheckerWaitTime has to be within 10001 and 199999. It is " +
                    "now at, " + this.waitTime);
        }
    }

    private void startDatabaseAliveChecker() {
        databaseAliveChecker = new DatabaseAliveChecker(waitTime);
        final Thread databaseAliveCheckerThread = new Thread(databaseAliveChecker);
        databaseAliveCheckerThread.setName("DatabaseAliveChecker-thread");

        databaseAliveCheckerThread.start();
    }

    @Override
    public void stop() {
        if (this.dataServer != null) {
            logger.info("%s shutting down...", this.getClass().getSimpleName());

            this.databaseAliveChecker.stop();
            this.dataServer.close();
        }
    }

    @Override
    public void run() {
        logger.info("%s starting up...", this.getClass().getSimpleName());

        dataServer = DataServer.getInstance();

        if (dataServer.getConnection() == null) {
            throw new ConnectException("Unable to fetch connection to database!");
        }

        logger.info("%s is now up and running!", this.getClass().getSimpleName());

        startDatabaseAliveChecker();
    }
}
