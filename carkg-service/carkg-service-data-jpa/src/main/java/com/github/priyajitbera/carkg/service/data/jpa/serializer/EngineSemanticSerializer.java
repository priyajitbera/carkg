package com.github.priyajitbera.carkg.service.data.jpa.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Engine;

import java.io.IOException;

public class EngineSemanticSerializer extends SemanticSerializer<Engine> {
    @Override
    public void serialize(Engine value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        String display = value.getName() + " " + value.getCapacityCc() + " cc";
        gen.writeString(display);
    }
}

