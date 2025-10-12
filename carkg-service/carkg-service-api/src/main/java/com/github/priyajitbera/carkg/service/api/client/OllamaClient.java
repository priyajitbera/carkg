package com.github.priyajitbera.carkg.service.api.client;

import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("ollamaClient")
public class OllamaClient implements GenerativeClient, EmbeddingClient {

    private final String OLLAMA_EMBEDDING_MODEL;

    private final OllamaEmbeddingModel embeddingModel;

    public OllamaClient(
            @Value("${ollama.embedding.model}")
            String ollamaEmbeddingModel,
            OllamaEmbeddingModel embeddingModel) {
        this.OLLAMA_EMBEDDING_MODEL = ollamaEmbeddingModel;
        this.embeddingModel = embeddingModel;
    }

    public float[] embed(String text) {
        EmbeddingResponse embeddingResponse = embeddingModel.call(
                new EmbeddingRequest(
                        List.of(text),
                        OllamaOptions.builder().model(OLLAMA_EMBEDDING_MODEL).truncate(false).build()));
        return embeddingResponse.getResults().get(0).getOutput();
    }

    @Override
    public String generate(String promp) {
        throw new UnsupportedOperationException("OllamaClient does not support text generation yet.");
    }
}
