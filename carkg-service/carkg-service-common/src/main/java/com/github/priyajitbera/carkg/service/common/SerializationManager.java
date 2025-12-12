package com.github.priyajitbera.carkg.service.common;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SerializationManager {
    private final List<SemanticSerializer> semanticSerializers;

    private final Map<Class, SemanticSerializer> typeToSerializerMap;

    public SerializationManager(List<SemanticSerializer> semanticSerializers) {
        this.semanticSerializers = semanticSerializers;
        typeToSerializerMap = semanticSerializers.stream()
                .collect(java.util.stream.Collectors.toMap(SemanticSerializer::getType, serializer -> serializer));
    }

    public <T> String serialize(T object) {
        SemanticSerializer serializer = typeToSerializerMap.get(object.getClass());
        if (serializer == null) {
            throw new IllegalArgumentException("No serializer found for type: " + object.getClass());
        }
        return serializer.serialize(object);
    }
}
