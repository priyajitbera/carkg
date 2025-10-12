package com.github.priyajitbera.carkg.service.api.service;

import com.github.priyajitbera.carkg.service.api.client.EmbeddingClient;
import com.github.priyajitbera.carkg.service.api.client.GeminiClient;
import com.github.priyajitbera.carkg.service.api.client.OllamaClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class EmbeddingService {

    private final String OLLAMA = "OLLAMA";
    private final String GEMINI = "GEMINI";

    private final String EMBEDDING_STRATEGY;
    private final Map<String, EmbeddingClient> embeddingClientMap;

    public EmbeddingService(
            @Value("${embedding.strategy}")
            String embeddingStrategy,
            @Qualifier("ollamaClient")
            OllamaClient ollamaClient,
            @Qualifier("geminiClient")
            GeminiClient geminiClient) {
        this.EMBEDDING_STRATEGY = embeddingStrategy;
        embeddingClientMap = Map.of(
                OLLAMA, ollamaClient,
                GEMINI, geminiClient
        );
    }

    public float[] embed(String text) {
        log.info("Using embedding strategy: {}", EMBEDDING_STRATEGY);
        log.info("Text to embed:\n{}", text);
        return embeddingClientMap.get(EMBEDDING_STRATEGY).embed(text);
    }
}
