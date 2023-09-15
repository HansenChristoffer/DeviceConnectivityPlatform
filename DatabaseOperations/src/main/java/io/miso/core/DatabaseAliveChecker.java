package io.miso.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class DatabaseAliveChecker implements Runnable {
    private static final Logger logger = LogManager.getFormatterLogger();
    
    private final long waitTime;

    private boolean shouldRun = true;

    public DatabaseAliveChecker(final long waitTime) {
        this.waitTime = waitTime;
    }

    @Override
    public void run() {
        logger.info("DatabaseChecker will be started now with a interval of %d ms!", waitTime);

        DataServer dataServer = Objects.requireNonNull(DataServer.getInstance(), "DataServer can not be null!");

        while (shouldRun) {
            if (dataServer.isDatabaseAlive()) {
                logger.info("Database is still up and available!");
            } else {
                logger.warn("Database is not responding!");
            }

            try {
                Thread.sleep(waitTime);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }

    public void stop() {
        logger.info("Shutting down DatabaseAliveChecker!");
        shouldRun = false;
    }
}
