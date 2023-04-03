package io.miso.core;

import com.mongodb.client.MongoClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseOperationsService implements Service {
    private static final Logger logger = LogManager.getFormatterLogger();

    private DataServer dataServer;

    @Override
    public void start() {
        logger.info("%s starting up...", this.getClass().getSimpleName());
        dataServer = DataServer.getInstance();
        final MongoClient client = dataServer.getMongoClient();
        logger.info("%s is now up and running!", this.getClass().getSimpleName());
        client.listDatabaseNames().forEach(logger::info);
    }

    @Override
    public void stop() {
        if (dataServer != null) {
            logger.info("%s shutting down...", this.getClass().getSimpleName());
            dataServer.close();
        }
    }
}
