package io.miso.core.repository;

import com.mongodb.client.ClientSession;
import org.bson.conversions.Bson;

import java.util.List;

public interface StorageRepository<T> {
    long replaceDocument(final T model);

    String insertDocument(final T model);

    String insertDocument(final ClientSession session, final T model);

    List<T> find(final Bson filter);

    long updateDocument(final Bson filter, final Bson update);

    long deleteDocument(final Bson filter);
}
