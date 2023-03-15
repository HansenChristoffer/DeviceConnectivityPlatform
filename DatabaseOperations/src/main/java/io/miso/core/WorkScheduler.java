package io.miso.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public class WorkScheduler implements Runnable, AutoCloseable {
    private static final Logger logger = LogManager.getFormatterLogger();
    private static final ExecutorService executorService = new ScheduledThreadPoolExecutor(5, new ThreadFactory() {
        private int c = 0;

        @Override
        public Thread newThread(final Runnable r) {
            return new Thread(r, "WorkScheduler-ExecutorThread-" + c++);
        }
    });

    private static WorkScheduler instance;
    private DataServer dataServer;
    private boolean shouldRun = false;

    private WorkScheduler() {
    }

    public static WorkScheduler getInstance() {
        if (instance == null) {
            instance = new WorkScheduler();
        }

        return instance;
    }

    @Override
    public void run() {
        shouldRun = true;
        dataServer = DataServer.getInstance();

        while (shouldRun) {
            checkForWork();
        }
    }

    private synchronized void checkForWork() {
        if (dataServer == null) {
            return;
        }

        try (final Connection connection = dataServer.getConnection()) {
            try (final PreparedStatement pstmt = connection.prepareStatement(
                    "SELECT * FROM pending_work_operations ORDER BY created_at ASC LIMIT 50")) {
                final ResultSet rs = pstmt.executeQuery();

            }
        } catch (final SQLException e) {
            logger.error("SQL error when trying to check for work!", e);
        }
    }

    @Override
    public void close() throws Exception {
        shouldRun = false;
        dataServer = null;
        instance = null;
    }
}
