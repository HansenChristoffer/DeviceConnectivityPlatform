package io.miso.core;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.SslSettings;
import io.miso.config.DataServerConfig;
import io.miso.core.config.Configurator;
import io.miso.core.listener.CommandMetric;
import io.miso.util.CloseableReentrantLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

class DataServer implements Closeable {
    private static final Logger logger = LogManager.getFormatterLogger();
    private static DataServer instance;
    private final DataServerConfig config;
    private final List<Closeable> functions = new CopyOnWriteArrayList<>();
    private MongoClient mongoClient;

    private DataServer() {
        config = Configurator.getConfig(DataServerConfig.class);
        setupClient();
    }

    public static DataServer getInstance() {
        try (final CloseableReentrantLock lock = new CloseableReentrantLock()) {
            if (instance == null) {
                instance = new DataServer();
            }
        }

        return instance;
    }

    private void setupClient() {
        final CommandMetric cm = new CommandMetric();

        final MongoClientSettings settings = MongoClientSettings.builder()
                .addCommandListener(cm)
                .applyConnectionString(new ConnectionString(config.getUrl()))
                .serverApi(ServerApi.builder()
                        .deprecationErrors(config.isDeprecationErrors())
                        .version(ServerApiVersion.findByValue(config.getServerApiVersion()))
                        .build())
                .retryReads(config.isRetryRead())
                .retryWrites(config.isRetryWrite())
                .credential(MongoCredential.createCredential(config.getUser(), config.getDatabase(), config.getSecret().toCharArray()))
                .applyToConnectionPoolSettings(b -> {
                    b.maxSize(config.getMaxPoolSize());
                    b.maxConnecting(config.getMaxConnecting());
                    b.maxConnectionIdleTime(config.getMaxConnectionIdleTime(), TimeUnit.MILLISECONDS);
                    b.maxConnectionLifeTime(config.getMaxConnectionLifeTime(), TimeUnit.MILLISECONDS);
                    b.maintenanceFrequency(config.getMaintenanceFrequency(), TimeUnit.MILLISECONDS);
                    b.maintenanceInitialDelay(config.getMaintenanceInitialDelay(), TimeUnit.MILLISECONDS);
                })
                .applyToSocketSettings(s -> {
                    s.receiveBufferSize(config.getReceiveBufferSize());
                    s.sendBufferSize(config.getSendBufferSize());
                })
                .applyToServerSettings(s -> {
                    s.heartbeatFrequency(config.getHeartbeatFrequency(), TimeUnit.MILLISECONDS);
                    s.minHeartbeatFrequency(config.getMinHeartbeatFrequency(), TimeUnit.MILLISECONDS);
                })
                .applyToSslSettings(s -> s.applySettings(SslSettings.builder()
                        .enabled(config.isSSLEnabled())
                        .build()))
                .build();

        mongoClient = MongoClients.create(settings);

        functions.add(cm);
        functions.add(mongoClient);
    }

    public MongoDatabase getConnection() {
        try (final CloseableReentrantLock lock = new CloseableReentrantLock()) {
            return mongoClient.getDatabase(config.getDatabase());
        }
    }

    public MongoClient getMongoClient() {
        try (final CloseableReentrantLock lock = new CloseableReentrantLock()) {
            return mongoClient;
        }
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     */
    @Override
    public void close() {
        try (final CloseableReentrantLock lock = new CloseableReentrantLock()) {
            logger.debug("Closing DataServer and the resources it has!");
            for (final Closeable c : functions) {
                if (c != null) {
                    try {
                        c.close();
                    } catch (final IOException ioe) {
                        logger.error("Error closing resource!", ioe);
                    }
                }
            }
        }
    }
}
