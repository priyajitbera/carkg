package com.github.priyajitbera.carkg.service.api.service;

import com.github.priyajitbera.carkg.service.api.client.GeminiClient;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingService {

    private final String GEMINI = "GEMINI";

    private final String EMBEDDING_STRATEGY;
    private final OllamaEmbeddingModel embeddingModel;
    private final GeminiClient geminiClient;

    public EmbeddingService(
            @Value("${embedding.strategy}")
            String embeddingStrategy,
            OllamaEmbeddingModel embeddingModel, GeminiClient geminiClient) {
        this.EMBEDDING_STRATEGY = embeddingStrategy;
        this.embeddingModel = embeddingModel;
        this.geminiClient = geminiClient;
    }

    public Embedding embedding(String text) {
        if (GEMINI.equals(EMBEDDING_STRATEGY)) {
            float[] vector = geminiClient.embed(text);
            return new Embedding(vector, -1);
        } else {
            EmbeddingResponse embeddingResponse = embeddingModel.call(
                    new EmbeddingRequest(
                            List.of(text),
                            OllamaOptions.builder().model("nomic-embed-text").truncate(false).build()));
            return embeddingResponse.getResults().get(0);
        }
    }
}
