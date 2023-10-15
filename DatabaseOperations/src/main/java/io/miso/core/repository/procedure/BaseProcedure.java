package io.miso.core.repository.procedure;

import io.miso.core.model.SystemBaseModel;

import java.util.Optional;

public interface BaseProcedure<T extends SystemBaseModel> {
    Optional<T> execute();
}
