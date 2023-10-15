package io.miso.core.repository.procedure;

import io.miso.core.model.SystemBaseModel;

import java.util.Collection;

public interface BaseMultiProcedure<T extends SystemBaseModel> {
    Collection<T> execute();
}
