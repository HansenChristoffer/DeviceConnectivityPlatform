package io.miso.core.repository;

import com.mongodb.client.ClientSession;
import org.bson.conversions.Bson;

import java.util.List;

public interface StorageRepository<T> {
    void insertDocument(final T model);

    void insertDocument(final ClientSession session, final T model);

    List<T> find(final Bson filter);

    void updateDocument(final Bson filter, final Bson update);

    void deleteDocument(final Bson filter);
}
