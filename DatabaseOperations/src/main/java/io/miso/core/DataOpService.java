package io.miso.core;

// TODO create a service that is the "main" of DatabaseOperations
public class DataOpService implements Service {

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
