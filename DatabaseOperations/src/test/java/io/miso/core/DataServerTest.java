package io.miso.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.miso.config.DataServerConfig;
import io.miso.core.model.Cluster;
import io.miso.core.model.Device;

class DataServerTest {
    private DataServerConfig mockConfig;
    private DataServer dataServer;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        this.mockConfig = Mockito.mock(DataServerConfig.class);
        this.dataServer = DataServer.getInstance();

        final Field configField = this.dataServer.getClass().getDeclaredField("config");
        configField.setAccessible(true);
        configField.set(this.dataServer, this.mockConfig);
    }

    @Test
    void testGetInstance() {
        final DataServer dataServer2 = DataServer.getInstance();
        assertSame(this.dataServer, dataServer2, "DataServer should be a singleton.");
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
    void testCodec() {
        final Cluster cluster = (Cluster) new Cluster()
                .setClusterId(123456L)
                .setDevices(List.of(new Device()))
                .setInternalRevision("THIS.IS.INTERNAL.REVISION")
                .setInternalId("THIS.IS.INTERNAL.ID");

        final MongoDatabase mockDatabase = mock(MongoDatabase.class);
        final MongoCollection<Cluster> mockCollection = mock(MongoCollection.class);

        final CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );

        when(mockDatabase.getCollection(anyString(), eq(Cluster.class)))
                .thenReturn(mockCollection);
        when(mockDatabase.withCodecRegistry(pojoCodecRegistry))
                .thenReturn(mockDatabase);

        final MongoCollection<Cluster> collection = mockDatabase
                .withCodecRegistry(pojoCodecRegistry)
                .getCollection("Clusters", Cluster.class);
        collection.insertOne(cluster);

        // Verify that the insertOne method was called on the mock collection
        verify(mockCollection).insertOne(cluster);

        // Mock the FindIterable and its first method
        final FindIterable<Cluster> mockIterable = mock(FindIterable.class);
        when(mockIterable.first()).thenReturn(cluster);
        when(mockCollection.find()).thenReturn(mockIterable);

        final Cluster retrievedCluster = collection.find().first();
        assertEquals(cluster, retrievedCluster);
    }

    @Test
    void testClose() throws IOException {
        final Closeable mockFunction1 = Mockito.mock(Closeable.class);
        final Closeable mockFunction2 = Mockito.mock(Closeable.class);
        when(this.mockConfig.getDatabase()).thenReturn("mockDatabase");

        try {
            final Field functionsField = this.dataServer.getClass().getDeclaredField("functions");
            functionsField.setAccessible(true);
            functionsField.set(this.dataServer, List.of(mockFunction1, mockFunction2));
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to set the 'functions' field.");
        }

        this.dataServer.close();

        verify(mockFunction1, times(1)).close();
        verify(mockFunction2, times(1)).close();
    }

}
