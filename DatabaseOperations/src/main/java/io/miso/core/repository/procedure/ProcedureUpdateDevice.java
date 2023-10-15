package io.miso.core.repository.procedure;

import com.mongodb.MongoClientSettings;
import io.miso.core.StorageManager;
import io.miso.core.model.SystemDeviceXO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.BsonElement;
import org.bson.BsonInt64;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.LongCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ProcedureUpdateDevice implements BaseProcedure<SystemDeviceXO> {
    private static final Logger logger = LogManager.getFormatterLogger();

    private final SystemDeviceXO systemDeviceXO;

    public ProcedureUpdateDevice(final SystemDeviceXO systemDeviceXO) {
        this.systemDeviceXO = Objects.requireNonNull(systemDeviceXO, "SystemDeviceXO is not allowed to be " +
                "null when trying to update device!");
    }

    @Override
    public Optional<SystemDeviceXO> execute() {
        StorageManager<SystemDeviceXO> sm = new StorageManager<>("device", SystemDeviceXO.class);

        BsonDocument bsonDocument = new BsonDocument();

        CodecRegistry codecRegistry = CodecRegistries.fromProviders(
                CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                        CodecRegistries.fromCodecs(new LongCodec()),
                        CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
                ));

        codecRegistry.get(SystemDeviceXO.class).encode(
                new BsonDocumentWriter(bsonDocument),
                systemDeviceXO,
                EncoderContext.builder().isEncodingCollectibleDocument(true).build()
        );

        long modifiedCount = sm.updateDocument(new BsonDocument(
                        List.of(new BsonElement("device_id", new BsonInt64(systemDeviceXO.getDeviceId())),
                                new BsonElement("cluster_id", new BsonInt64(systemDeviceXO.getClusterId())))),
                new BsonDocument("$set", bsonDocument));

        if (modifiedCount == 0) {
            logger.warn("Was unable to update device with id [%d] within the cluster with id [%d]",
                    systemDeviceXO.getDeviceId(), systemDeviceXO.getClusterId());
        }

        return Optional.empty();
    }
}
