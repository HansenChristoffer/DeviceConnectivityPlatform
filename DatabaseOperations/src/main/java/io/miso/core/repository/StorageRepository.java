package io.miso.core.repository;

import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.client.ClientSession;

public interface StorageRepository<T> {
    void insertDocument(final T model);

    void insertDocument(final ClientSession session, final T model);

    List<T> find(final Bson filter);

    void updateDocument(final Bson filter, final Bson update);

    void deleteDocument(final Bson filter);
}
