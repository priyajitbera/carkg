package com.github.priyajitbera.carkg.service.api.client;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GeminiClient {

    private final String GEMINI_BASE_URL;

    private final String GEMINI_API_KEY;

    private final String GEMINI_GENERATIVE_MODEL_URI;
    private final String GEMINI_EMBEDDING_MODEL_URI;

    private final WebClient webClient;

    public GeminiClient(
            @Value("${gemini.baseUrl}")
            String geminiBaseUrl,
            @Value("${gemini.apiKey}")
            String geminiApiKey,
            @Value("${gemini.generative.modelUri}")
            String geminiGenerativeModelUri,
            @Value("${gemini.embedding.modelUri}")
            String geminiEmbeddingModelUri
    ) {
        this.GEMINI_BASE_URL = geminiBaseUrl;
        this.GEMINI_API_KEY = geminiApiKey;
        this.GEMINI_GENERATIVE_MODEL_URI = geminiGenerativeModelUri;
        this.GEMINI_EMBEDDING_MODEL_URI = geminiEmbeddingModelUri;
        this.webClient = WebClient.builder()
                .baseUrl(GEMINI_BASE_URL)
                .build();
    }

    @Data
    public static class GenerationResponse {
        private List<Candidate> candidates;
        private UsageMetadata usageMetadata;
    }

    @Data
    public static class Candidate {
        private Content content;
    }

    @Data
    public static class UsageMetadata {
        private Long promptTokenCount;
        private Long candidatesTokenCount;
        private Long totalTokenCount;
    }

    @Data
    public static class Content {
        private List<Part> parts;
    }

    @Data
    public static class Part {
        private String text;
    }

    @Data
    public static class EmbeddingResponse {
        private Embedding embedding;
    }

    @Data
    public static class Embedding {
        private float[] values;
    }

    public String generate(String prompt) {
        final GenerationResponse generationResponse;
        try {
            generationResponse = webClient.post()
                    .uri(GEMINI_GENERATIVE_MODEL_URI)
                    .header("Content-Type", "application/json")
                    .header("X-goog-api-key", GEMINI_API_KEY)
                    .bodyValue(Map.of(
                            "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
                    ))
                    .retrieve()
                    .bodyToMono(GenerationResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getMessage());
        }
        if (generationResponse != null) {
            log.info("promptTokenCount: {}, candidatesTokenCount: {}, totalTokenCount: {}",
                    generationResponse.getUsageMetadata().getPromptTokenCount(),
                    generationResponse.getUsageMetadata().getCandidatesTokenCount(),
                    generationResponse.getUsageMetadata().getTotalTokenCount());
            final String content = generationResponse.getCandidates().get(0).getContent().getParts().get(0).getText();
            return content;
        } else {
            throw new RuntimeException("Unexpected or incomplete Gemini response received");
        }
    }

    public float[] embed(String text) {
        final EmbeddingResponse embeddingResponse;
        try {
            embeddingResponse = webClient.post()
                    .uri(GEMINI_EMBEDDING_MODEL_URI)
                    .header("Content-Type", "application/json")
                    .header("X-goog-api-key", GEMINI_API_KEY)
                    .bodyValue(
                            Map.of(
                                    "content", Map.of("parts", List.of(Map.of("text", text)))
                            )
                    )
                    .retrieve()
                    .bodyToMono(EmbeddingResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getMessage());
        }
        if (embeddingResponse != null) {
            return embeddingResponse.getEmbedding().getValues();
        } else {
            throw new RuntimeException("Unexpected or incomplete Gemini response received");
        }
    }
}
