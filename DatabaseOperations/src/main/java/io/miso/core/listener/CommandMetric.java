package io.miso.core.listener;

import com.mongodb.event.CommandFailedEvent;
import com.mongodb.event.CommandListener;
import com.mongodb.event.CommandSucceededEvent;
import io.miso.util.CloseableReentrantLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandMetric implements CommandListener, Closeable {
    private static final Logger logger = LogManager.getFormatterLogger();
    private final Map<String, Integer> metrics = new ConcurrentHashMap<>();
    private boolean isActivated = true;
    private boolean isAnnouncerRunning = false;

    /**
     * Listener for command completed events
     *
     * @param evt the evt
     */
    @Override
    public void commandSucceeded(final CommandSucceededEvent evt) {
        try (final CloseableReentrantLock lock = new CloseableReentrantLock()) {
            if (this.isActivated && !evt.getCommandName().isBlank()) {
                this.metrics.put(evt.getCommandName(), this.metrics.getOrDefault(evt.getCommandName(), 0) + 1);
            }
        }
    }

    /**
     * Listener for command failure events
     *
     * @param evt the evt
     */
    @Override
    public void commandFailed(final CommandFailedEvent evt) {
        logger.error("Failed execution of command '%s' with id '%d'!", evt.getCommandName(), evt.getRequestId());
    }

    public void startAnnouncer() {
        try (final CloseableReentrantLock lock = new CloseableReentrantLock()) {
            if (this.isActivated && !this.isAnnouncerRunning) {
                logger.debug("Starting Metric announcer...");
                final Thread announcerThread = new Thread(new MetricAnnouncer());
                announcerThread.start();
            } else {
                logger.debug("Metric announcer already running!");
            }
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
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        try (final CloseableReentrantLock lock = new CloseableReentrantLock()) {
            this.isActivated = false;
            this.metrics.clear();
            this.isAnnouncerRunning = false;
        }
    }

    class MetricAnnouncer implements Runnable {
        /**
         * When an object implementing interface {@code Runnable} is used
         * to create a thread, starting the thread causes the object's
         * {@code run} method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method {@code run} is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            while (CommandMetric.this.isActivated) {
                try (final CloseableReentrantLock lock = new CloseableReentrantLock()) {
                    final StringBuilder sb = new StringBuilder();
                    CommandMetric.this.isAnnouncerRunning = true;

                    for (final Map.Entry<String, Integer> e : CommandMetric.this.metrics.entrySet()) {
                        sb.append(String.format("%s:%d%n", e.getKey(), e.getValue()));
                    }

                    if (!sb.isEmpty() && logger.isInfoEnabled()) {
                        logger.info("MongoDB Command metrics -> %n%s", sb.toString());
                    }
                }
                try {
                    Thread.sleep(15000);
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("MetricAnnouncers wait failed!", e);
                }
            }

            try (final CloseableReentrantLock lock = new CloseableReentrantLock()) {
                CommandMetric.this.isAnnouncerRunning = false;
            }
        }
    }
}
