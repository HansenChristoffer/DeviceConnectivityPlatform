package io.miso.core;

import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.miso.core.model.BaseModel;
import io.miso.core.repository.StorageRepository;

public class StorageManager<T extends BaseModel> implements StorageRepository<T> {
    private final MongoCollection<T> collection;

    public StorageManager(final String collectionName, final Class<T> typeParameterClass) {
        final DataServer dataServer = DataServer.getInstance();
        final MongoDatabase database = dataServer.getConnection();
        this.collection = database.getCollection(collectionName, typeParameterClass);
    }

    @Override
    public void insertDocument(final T model) {
        collection.insertOne(model);
    }

    @Override
    public void insertDocument(final ClientSession session, final T model) {
        collection.insertOne(session, model);
    }

    @Override
    public List<T> find(final Bson filter) {
        return collection.find(filter).into(new ArrayList<>());
    }

    @Override
    public void updateDocument(final Bson filter, final Bson update) {
        collection.updateOne(filter, update);
    }

    @Override
    public void deleteDocument(final Bson filter) {
        collection.deleteOne(filter);
    }
}
