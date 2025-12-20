package com.github.priyajitbera.carkg.service.model.client.ollama;

import com.github.priyajitbera.carkg.service.model.client.EmbeddingClient;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("OllamaEmbeddingClient")
public class OllamaEmbeddingClient implements EmbeddingClient {

    private final String OLLAMA_EMBEDDING_MODEL;

    private final OllamaEmbeddingModel embeddingModel;

    public OllamaEmbeddingClient(
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
}
