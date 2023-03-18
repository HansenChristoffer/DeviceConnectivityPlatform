package io.miso.core;

import io.miso.config.DataServerConfig;
import io.miso.core.config.Configurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

// Singleton
// TODO Refactor
public class DataServer {
    private static final Logger logger = LogManager.getFormatterLogger();

    private static DataServer instance;

    private static DataServerConfig config;

    private static final String URL = "jdbc:mysql://localhost:3306/mydatabase";
    private static final String USERNAME = "myuser";
    private static final String PASSWORD = "mypassword";
    private static final int MAX_POOL_SIZE = 10;

    private BlockingQueue<Connection> connectionQueue;

    private DataServer() {
//        connectionQueue = new LinkedBlockingQueue<>();
//        for (int i = 0; i < MAX_POOL_SIZE; i++) {
//            final Connection connection;
//
//            try {
//                connection = createConnection();
//            } catch (final SQLException e) {
//                logger.fatal("Failed to create connection!");
//                throw new RuntimeException(e);
//            }
//
//            connectionQueue.add(connection);
//        }
    }

    public static synchronized DataServer getInstance() {
        if (instance == null) {
            instance = new DataServer();

            config = Configurator.getConfig(DataServerConfig.class);
        }

        return instance;
    }

    private Connection createConnection() throws SQLException {
        final Properties properties = new Properties();
        properties.setProperty("user", USERNAME);
        properties.setProperty("password", PASSWORD);

        return DriverManager.getConnection(URL, properties);
    }

    public synchronized Connection getConnection() throws SQLException {
        try {
            Connection connection = connectionQueue.take();
            if (connection.isClosed()) {
                connection = createConnection();
            }
            return connection;
        } catch (final InterruptedException e) {
            throw new SQLException("Failed to get connection from pool", e);
        }
    }

    public synchronized void releaseConnection(final Connection connection) {
        connectionQueue.add(connection);
    }
}
