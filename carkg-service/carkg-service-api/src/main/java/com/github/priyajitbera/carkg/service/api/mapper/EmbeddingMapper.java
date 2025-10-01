package com.github.priyajitbera.carkg.service.api.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Embedding;
import org.springframework.stereotype.Component;

@Component
public class EmbeddingMapper {

    public float[] to(Embedding embedding) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(embedding.getEmbedding(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
