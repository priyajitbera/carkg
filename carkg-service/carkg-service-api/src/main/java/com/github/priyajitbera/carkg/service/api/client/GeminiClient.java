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

    private final String GEMINI_MODEL_URI;

    private final WebClient webClient;

    public GeminiClient(
            @Value("${gemini.baseUrl}")
            String geminiBaseUrl,
            @Value("${gemini.apiKey}")
            String geminiApiKey,
            @Value("${gemini.modelUri}")
            String geminiModelUri
    ) {
        this.GEMINI_BASE_URL = geminiBaseUrl;
        this.GEMINI_API_KEY = geminiApiKey;
        this.GEMINI_MODEL_URI = geminiModelUri;
        this.webClient = WebClient.builder()
                .baseUrl(GEMINI_BASE_URL)
                .build();
    }

    @Data
    public static class Response {
        List<Candidate> candidates;
        UsageMetadata usageMetadata;
    }

    @Data
    public static class Candidate {
        public Content content;
    }

    @Data
    public static class UsageMetadata {
        public Long promptTokenCount;
        public Long candidatesTokenCount;
        public Long totalTokenCount;
    }

    @Data
    public static class Content {
        public List<Part> parts;
    }

    @Data
    public static class Part {
        public String text;
    }

    public String generate(String prompt) {
        final Response response;
        try {
            response = webClient.post()
                    .uri(GEMINI_MODEL_URI)
                    .header("Content-Type", "application/json")
                    .header("X-goog-api-key", GEMINI_API_KEY)
                    .bodyValue(Map.of(
                            "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
                    ))
                    .retrieve()
                    .bodyToMono(Response.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getMessage());
        }
        if (response != null) {
            log.info("promptTokenCount: {}, candidatesTokenCount: {}, totalTokenCount: {}",
                    response.getUsageMetadata().getPromptTokenCount(),
                    response.getUsageMetadata().getCandidatesTokenCount(),
                    response.getUsageMetadata().getTotalTokenCount());
            final String content = response.getCandidates().get(0).getContent().getParts().get(0).getText();
            return content;
        } else {
            throw new RuntimeException("Unexpected or incomplete Gemini response received");
        }
    }
}
