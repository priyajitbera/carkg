package com.github.priyajitbera.carkg.service.data.jpa.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;

public class FloatArrayToJson implements AttributeConverter<float[], String> {
    @Override
    public String convertToDatabaseColumn(float[] arr) {
        if(arr == null) return null;
        try {
            return new ObjectMapper().writeValueAsString(arr);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public float[] convertToEntityAttribute(String serialized) {
        if(serialized == null) return null;
        try {
            return new ObjectMapper().readValue(serialized, float[].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
