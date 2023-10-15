package io.miso.core;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import io.miso.core.model.SystemBaseModel;
import io.miso.core.model.SystemDeviceXO;
import io.miso.core.repository.StorageRepository;
import org.bson.BsonDocument;
import org.bson.BsonElement;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.miso.util.ConditionUtils.isNotNullAndNotBlank;

public class StorageManager<T extends SystemBaseModel> implements StorageRepository<T> {
    private final MongoCollection<T> collection;

    public StorageManager(final String collectionName, final Class<T> typeParameterClass) {
        final DataServer dataServer = DataServer.getInstance();
        final MongoDatabase database = dataServer.getConnection();
        this.collection = database.getCollection(collectionName, typeParameterClass);
    }

    @Override
    public long replaceDocument(final T model) {
        Objects.requireNonNull(model, "Model used when replacing document is not allowed to be null!");
        UpdateResult updateResult = null;

        if (isNotNullAndNotBlank(model.getInternalId())) {
            updateResult = collection.replaceOne(new BsonDocument("internal_id",
                    new BsonString(model.getInternalId())), model);
        } else if (model instanceof SystemDeviceXO systemDeviceXO
                && systemDeviceXO.getDeviceId() > 0
                && systemDeviceXO.getClusterId() > 0) {
            updateResult = collection.replaceOne(
                    new BsonDocument(List.of(
                            new BsonElement("device_id", new BsonInt64(systemDeviceXO.getDeviceId())),
                            new BsonElement("cluster_id", new BsonInt64(systemDeviceXO.getClusterId())))),
                    model);
        }

        if (updateResult != null && updateResult.wasAcknowledged()) {
            return updateResult.getModifiedCount();
        } else {
            return 0;
        }
    }

    @Override
    public String insertDocument(final T model) {
        Objects.requireNonNull(model, "Model used when insert document is not allowed to be null!");

        InsertOneResult insertOneResult = collection.insertOne(model);

        if (insertOneResult.wasAcknowledged()) {
            return Objects.requireNonNull(insertOneResult.getInsertedId()).asObjectId().getValue().toHexString();
        }

        return "";
    }

    @Override
    public String insertDocument(final ClientSession session, final T model) {
        Objects.requireNonNull(model, "Model used when insert document is not allowed to be null!");

        InsertOneResult insertOneResult = collection.insertOne(session, model);
        if (insertOneResult.wasAcknowledged()) {
            return Objects.requireNonNull(insertOneResult.getInsertedId()).asObjectId().getValue().toHexString();
        }

        return "";
    }

    @Override
    public List<T> find(final Bson filter) {
        Objects.requireNonNull(filter, "Filter used when finding document(s) is not allowed to be null!");
        return collection.find(filter).into(new ArrayList<>());
    }

    @Override
    public long updateDocument(final Bson filter, final Bson update) {
        Objects.requireNonNull(filter, "Filter used when updating document is not allowed to be null!");
        Objects.requireNonNull(update, "Bson used when updating document is not allowed to be null!");

        UpdateResult updateResult = collection.updateOne(filter, update);

        if (updateResult.wasAcknowledged()) {
            return updateResult.getModifiedCount();
        }

        return 0;
    }

    @Override
    public long deleteDocument(final Bson filter) {
        Objects.requireNonNull(filter, "Filter used when updating document is not allowed to be null!");

        DeleteResult deleteResult = collection.deleteOne(filter);

        if (deleteResult.wasAcknowledged()) {
            return deleteResult.getDeletedCount();
        }

        return 0;
    }
}
