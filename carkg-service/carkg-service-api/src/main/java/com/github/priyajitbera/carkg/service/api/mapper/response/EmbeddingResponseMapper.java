package com.github.priyajitbera.carkg.service.api.mapper.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Embedding;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public abstract class EmbeddingMapper {

    @Named("embeddingToFloatArray")
    float[] map(Embedding embedding) {
        if (embedding == null || embedding.getEmbedding() == null) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(embedding.getEmbedding(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
