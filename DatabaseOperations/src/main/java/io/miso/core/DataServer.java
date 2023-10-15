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
import org.bson.Document;
import org.bson.codecs.LongCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

class DataServer implements Closeable {
    private static final Logger logger = LogManager.getFormatterLogger();

    private final List<Closeable> functions = new CopyOnWriteArrayList<>();
    private final DataServerConfig config;
    private final String database;

    private MongoClient mongoClient;

    private DataServer() {
        this.config = Configurator.getConfig(DataServerConfig.class);
        this.database = config.getDatabase();
        setupClient();
    }

    public static synchronized DataServer getInstance() {
        return SingletonHelper.INSTANCE;
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
                .credential(MongoCredential.createCredential(config.getUser(), config.getDatabase(),
                        config.getSecret().toCharArray()))
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
                .codecRegistry(CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                        CodecRegistries.fromCodecs(new LongCodec()),
                        CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())))
                .build();

        mongoClient = MongoClients.create(settings);

        functions.add(cm);
        functions.add(mongoClient);
    }

    public synchronized MongoDatabase getConnection() {
        try (final CloseableReentrantLock lock = new CloseableReentrantLock()) {
            return mongoClient.getDatabase(database);
        }
    }

    public synchronized MongoClient getMongoClient() {
        try (final CloseableReentrantLock lock = new CloseableReentrantLock()) {
            return mongoClient;
        }
    }

    public synchronized boolean isDatabaseAlive() {
        try {
            final Document ping = new Document("ping", 1);
            final Document result = getConnection().runCommand(ping);
            return result.getDouble("ok") == 1.0;
        } catch (final Exception e) {
            return false;
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

    private static class SingletonHelper {
        private static final DataServer INSTANCE = new DataServer();
    }
}
