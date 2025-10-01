package com.github.priyajitbera.carkg.service.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingService {

    private final OllamaEmbeddingModel embeddingModel;

    public EmbeddingService(OllamaEmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public Embedding embedding(String text) {
        EmbeddingResponse embeddingResponse = embeddingModel.call(
                new EmbeddingRequest(
                        List.of(text),
                        OllamaOptions.builder().model("nomic-embed-text").truncate(false).build()));
        return embeddingResponse.getResults().get(0);
    }

    public String serializeEmbedding(Embedding embedding) {
        final String embeddingJson;
        try {
            embeddingJson = new ObjectMapper().writeValueAsString(embedding.getOutput());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return embeddingJson;
    }

    public String serializeEmbeddable(Object object) {
        final String carEntitySer;
        try {
            carEntitySer = new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return carEntitySer;
    }
}
