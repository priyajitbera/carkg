package com.github.priyajitbera.carkg.service.data.jpa.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.priyajitbera.carkg.service.data.jpa.entity.TransmissionType;

import java.io.IOException;

public class TransmissionTypeSemanticSerializer extends SemanticSerializer<TransmissionType> {
    @Override
    public void serialize(TransmissionType value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        String display = value.getName();
        gen.writeString(display);
    }
}

