package io.miso.core;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.miso.config.DataServerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

class DataServerTest {
    private DataServerConfig mockConfig;
    private DataServer dataServer;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        mockConfig = Mockito.mock(DataServerConfig.class);
        dataServer = DataServer.getInstance();

        final Field configField = dataServer.getClass().getDeclaredField("config");
        configField.setAccessible(true);
        configField.set(dataServer, mockConfig);
    }

    @Test
    void testGetInstance() {
        final DataServer dataServer2 = DataServer.getInstance();
        assertSame(dataServer, dataServer2, "DataServer should be a singleton.");
    }

    @Test
    void testGetConnection() {
        final MongoClient mockMongoClient = Mockito.mock(MongoClient.class);
        final MongoDatabase mockDatabase = Mockito.mock(MongoDatabase.class);

        // Create a mock of DataServer
        final DataServer mockDataServer = Mockito.mock(DataServer.class);

        // Stub the methods on the mocked objects
        when(mockDataServer.getMongoClient()).thenReturn(mockMongoClient);
        when(mockMongoClient.getDatabase(anyString())).thenReturn(mockDatabase);
        when(mockDataServer.getConnection()).thenReturn(mockDatabase);

        // Call the method on the mocked DataServer object
        final MongoDatabase database = mockDataServer.getConnection();
        assertSame(mockDatabase, database, "The database instance should match.");

        mockDataServer.close();
    }

    @Test
    void testClose() throws IOException {
        final Closeable mockFunction1 = Mockito.mock(Closeable.class);
        final Closeable mockFunction2 = Mockito.mock(Closeable.class);
        when(mockConfig.getDatabase()).thenReturn("mockDatabase");

        try {
            final Field functionsField = dataServer.getClass().getDeclaredField("functions");
            functionsField.setAccessible(true);
            functionsField.set(dataServer, List.of(mockFunction1, mockFunction2));
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to set the 'functions' field.");
        }

        dataServer.close();

        verify(mockFunction1, times(1)).close();
        verify(mockFunction2, times(1)).close();
    }

}
