package com.github.priyajitbera.carkg.service.model.client.gemini;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.priyajitbera.carkg.service.model.client.EmbeddingClient;
import com.github.priyajitbera.carkg.service.model.client.gemini.response.EmbeddingResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.netty.http.client.HttpClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Component("GeminiEmbeddingClient")
public class GeminiEmbeddingClient implements EmbeddingClient {

    public final static String HEADER_X_GOOG_API_KEY = "X-goog-api-key";

    private final String GEMINI_BASE_URL;

    private final String GEMINI_API_KEY;
    private final String GEMINI_EMBEDDING_MODEL_URI;

    private final WebClient webClient;

    public GeminiEmbeddingClient(
            @Value("${gemini.baseUrl}")
            String geminiBaseUrl,
            @Value("${gemini.apiKey}")
            String geminiApiKey,
            @Value("${gemini.embedding.modelUri}")
            String geminiEmbeddingModelUri
    ) {
        this.GEMINI_BASE_URL = geminiBaseUrl;
        this.GEMINI_API_KEY = geminiApiKey;
        this.GEMINI_EMBEDDING_MODEL_URI = geminiEmbeddingModelUri;
        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .wiretap("reactor.netty.http.client.HttpClient",
                                io.netty.handler.logging.LogLevel.DEBUG,
                                reactor.netty.transport.logging.AdvancedByteBufFormat.TEXTUAL)))
                .baseUrl(GEMINI_BASE_URL)
                .build();
    }

    private static JsonNode removeAdditionalProperties(JsonNode node) {
        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;
            obj.remove("additionalProperties"); // remove if exists

            // Recursively process children
            obj.fields().forEachRemaining(entry -> {
                JsonNode child = entry.getValue();
                removeAdditionalProperties(child);
            });
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                removeAdditionalProperties(child);
            }
        }
        return node;
    }

    @Override
    public float[] embed(String text) {
        final EmbeddingResponse embeddingResponse;
        try {
            embeddingResponse = webClient.post()
                    .uri(GEMINI_EMBEDDING_MODEL_URI)
                    .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON.toString())
                    .header(HEADER_X_GOOG_API_KEY, GEMINI_API_KEY)
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
