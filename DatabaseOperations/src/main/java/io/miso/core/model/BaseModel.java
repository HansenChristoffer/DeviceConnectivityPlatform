package io.miso.core.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

public abstract class BaseModel implements Serializable {
    @BsonId
    @BsonProperty(value = "internal_id")
    private String internalId;

    @BsonProperty(value = "internal_revision")
    private String internalRevision;

    BaseModel() {
        // For de-/serialization
        this.internalId = UUID.randomUUID().toString();
    }

    BaseModel(final String internalId, final String internalRevision) {
        this.internalId = internalId;
        this.internalRevision = internalRevision;
    }

    public String getInternalId() {
        return this.internalId;
    }

    public BaseModel setInternalId(final String internalId) {
        this.internalId = internalId;
        return this;
    }

    public String getInternalRevision() {
        return this.internalRevision;
    }

    public BaseModel setInternalRevision(final String internalRevision) {
        this.internalRevision = internalRevision;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final BaseModel baseModel)) {
            return false;
        }
        return this.internalId.equals(baseModel.internalId) && this.internalRevision.equals(baseModel.internalRevision);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.internalId, this.internalRevision);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BaseModel.class.getSimpleName() + "[", "]")
                .add("internalId='" + this.internalId + "'")
                .add("internalRevision='" + this.internalRevision + "'")
                .toString();
    }
}
