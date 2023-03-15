package io.miso.core;

import io.miso.config.DataOpConfig;

// TODO create a service that is the "main" of DatabaseOperations
public class DataOpService implements Service {
    private DataOpConfig config;

    public DataOpService() {
    }


    @Override
    public void start() {
        final DataServer ds = DataServer.getInstance();
    }

    @Override
    public void stop() {

    }
}
