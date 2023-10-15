package io.miso.core.model;

import org.bson.codecs.pojo.annotations.BsonProperty;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

public abstract class SystemBaseModel implements Serializable {
    @BsonProperty(value = "internal_id")
    private String internalId;

    @BsonProperty(value = "internal_revision")
    private String internalRevision;

    SystemBaseModel() {
        // For de-/serialization
        this.internalId = UUID.randomUUID().toString();
    }

    public SystemBaseModel(final String internalRevision) {
        this.internalId = UUID.randomUUID().toString();
        this.internalRevision = internalRevision;
    }

    SystemBaseModel(final String internalId, final String internalRevision) {
        this.internalId = internalId;
        this.internalRevision = internalRevision;
    }

    public String getInternalId() {
        return this.internalId;
    }

    public SystemBaseModel setInternalId(final String internalId) {
        this.internalId = internalId;
        return this;
    }

    public String getInternalRevision() {
        return this.internalRevision;
    }

    public SystemBaseModel setInternalRevision(final String internalRevision) {
        this.internalRevision = internalRevision;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final SystemBaseModel systemBaseModel)) {
            return false;
        }
        return this.internalId.equals(systemBaseModel.internalId) && this.internalRevision.equals(systemBaseModel.internalRevision);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.internalId, this.internalRevision);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SystemBaseModel.class.getSimpleName() + "[", "]")
                .add("internalId='" + this.internalId + "'")
                .add("internalRevision='" + this.internalRevision + "'")
                .toString();
    }
}
