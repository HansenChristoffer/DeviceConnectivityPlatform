package io.miso.core.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.miso.core.InboundCommand;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface InboundCommandHandler {
    InboundCommand value();
}
