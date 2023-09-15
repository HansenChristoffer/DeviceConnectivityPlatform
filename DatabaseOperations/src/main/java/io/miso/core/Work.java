package io.miso.core;

public class Work {
    private final Runnable task;

    public Work(final Runnable runnable) {
        this.task = runnable;
    }

    public Runnable getTask() {
        return this.task;
    }
}
