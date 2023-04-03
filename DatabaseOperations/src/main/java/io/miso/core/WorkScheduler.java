package io.miso.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

class WorkScheduler implements Runnable, AutoCloseable {
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
    }

    @Override
    public void close() throws Exception {
        shouldRun = false;
        dataServer = null;
        instance = null;
    }
}
