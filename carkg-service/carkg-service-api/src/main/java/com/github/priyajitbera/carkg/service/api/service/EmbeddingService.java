package com.github.priyajitbera.carkg.service.api.service;

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
}
