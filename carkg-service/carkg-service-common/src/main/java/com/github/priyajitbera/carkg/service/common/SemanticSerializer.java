package com.github.priyajitbera.carkg.service.common;

public interface SemanticSerializer<T> {
    String serialize(T object);

    Class<T> getType();
}
