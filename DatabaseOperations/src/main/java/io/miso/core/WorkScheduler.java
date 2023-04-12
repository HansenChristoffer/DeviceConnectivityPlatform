package io.miso.core;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.miso.util.CloseableReentrantLock;

// TODO: Either put it in the WorkScheduler or somewhere else to query database for work
public class WorkScheduler implements Runnable, AutoCloseable {
    private static final Logger logger = LogManager.getFormatterLogger();
    private static final ExecutorService executorService = new ScheduledThreadPoolExecutor(5, new ThreadFactory() {
        private int c = 0;

        @Override
        public Thread newThread(final Runnable r) {
            return new Thread(r, "WorkScheduler-ExecutorThread-" + this.c++);
        }
    });

    private final Queue<Work> workQueue;

    private boolean shouldRun;

    private WorkScheduler() {
        this.workQueue = new PriorityBlockingQueue<>();
        this.shouldRun = false;
    }

    public static WorkScheduler getInstance() {
        return SingleToneHelper.INSTANCE;
    }

    public synchronized void addWork(final Work work) {
        try (final CloseableReentrantLock lock = new CloseableReentrantLock()) {
            if (!workQueue.offer(work)) {
                logger.warn("Was unable to add new work to WorkScheduler because of size restrictions! [%d]",
                        workQueue.size());
            }
        }
    }

    private synchronized void checkForWork() {
        try (final CloseableReentrantLock lock = new CloseableReentrantLock()) {
            if (!workQueue.isEmpty()) {
                for (int i = 0; i < workQueue.size(); i++) {
                    executorService.submit(workQueue.poll().getTask());
                }
            }
        }
    }

    @Override
    public void run() {
        shouldRun = true;

        while (shouldRun) {
            checkForWork();

            try {
                Thread.sleep(500L);
            } catch (final InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(ie);
            }
        }
    }

    @Override
    public void close() throws Exception {
        shouldRun = false;

        if (!executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
            executorService.shutdownNow();
        }
    }

    private static class SingleToneHelper {
        private static final WorkScheduler INSTANCE = new WorkScheduler();
    }
}
