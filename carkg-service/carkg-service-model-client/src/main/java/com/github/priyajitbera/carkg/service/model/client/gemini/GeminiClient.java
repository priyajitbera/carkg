package com.github.priyajitbera.carkg.service.model.client.gemini;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.priyajitbera.carkg.service.model.client.common.EmbeddingClient;
import com.github.priyajitbera.carkg.service.model.client.common.GenerativeClient;
import com.github.priyajitbera.carkg.service.model.client.gemini.request.*;
import com.github.priyajitbera.carkg.service.model.client.gemini.response.EmbeddingResponse;
import com.github.priyajitbera.carkg.service.model.client.gemini.response.FunctionCall;
import com.github.priyajitbera.carkg.service.model.client.gemini.response.GenerationResponse;
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
import java.util.Optional;

@Slf4j
@Component("geminiClient")
public class GeminiClient implements GenerativeClient, EmbeddingClient {

    public final static String HEADER_X_GOOG_API_KEY = "X-goog-api-key";

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
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .wiretap("reactor.netty.http.client.HttpClient",
                                io.netty.handler.logging.LogLevel.DEBUG,
                                reactor.netty.transport.logging.AdvancedByteBufFormat.TEXTUAL)))
                .baseUrl(GEMINI_BASE_URL)
                .build();
    }


    @Override
    public String generate(String prompt) {
        GenerationRequest generationRequest = GenerationRequest.builder()
                .contents(List.of(Content.builder().parts(List.of(Part.builder().text(prompt).build())).build()))
                .build();
        GenerationResponse generationResponse = generate(generationRequest);
        return generationResponse
                .getCandidates().get(0)
                .getContent()
                .getParts().get(0)
                .getText();
    }

    @Override
    public com.github.priyajitbera.carkg.service.model.client.common.response.GenerationResponse generate(String prompt, List<com.github.priyajitbera.carkg.service.model.client.common.request.Tool> tools) {
        GenerationRequest generationRequest = GenerationRequest.builder()
                .contents(List.of(Content.builder().parts(List.of(Part.builder().text(prompt).build())).build()))
                .tools(List.of(Tool.builder().functionDeclarations(tools.stream().map(this::map).toList()).build()))
                .build();
        GenerationResponse generationResponse = generate(generationRequest);
        StringBuilder contentBuilder = new StringBuilder();
        generationResponse.getCandidates().forEach(candidate -> {
            candidate.getContent().getParts().forEach(part -> {
                if (part.getText() != null)
                    contentBuilder.append(part.getText());
            });
        });
        final String content = contentBuilder.toString();

        Optional<FunctionCall> functionCallOpt = generationResponse.getCandidates().get(0).getContent().getParts().stream()
                .filter(part -> part.getFunctionCall() != null).findFirst().map(com.github.priyajitbera.carkg.service.model.client.gemini.response.Part::getFunctionCall);

        return com.github.priyajitbera.carkg.service.model.client.common.response.GenerationResponse.builder()
                .content(content)
                .toolCallDetails(
                        functionCallOpt.map(functionCall -> com.github.priyajitbera.carkg.service.model.client.common.response.ToolCallDetails.builder()
                                .name(functionCall.getName())
                                .parameters(functionCall.getArgs())
                                .build()).orElse(null)
                )
                .build();
    }

    private FunctionDeclaration map(com.github.priyajitbera.carkg.service.model.client.common.request.Tool tool) {
        return FunctionDeclaration.builder()
                .name(tool.getName())
                .description(tool.getDescription())
                .parameters(sanitizeInputSchema(tool.getInputSchema()))
                .build();
    }

    public static JsonNode removeAdditionalProperties(JsonNode node) {
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

    public static JsonNode sanitizeInputSchema(String inputJson) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(inputJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        JsonNode cleaned = removeAdditionalProperties(root);
        return cleaned;
    }

    private GenerationResponse generate(GenerationRequest generationRequest) {

        final String responseStr;
        try {
            responseStr = webClient.post()
                    .uri(GEMINI_GENERATIVE_MODEL_URI)
                    .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON.toString())
                    .header(HEADER_X_GOOG_API_KEY, GEMINI_API_KEY)
                    .bodyValue(generationRequest)
                    .retrieve()
                    .bodyToMono(String.class)
                    .log()
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error while calling Gemini Generative Model API: Status Code: {}, Response Body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new ResponseStatusException(e.getStatusCode(), e.getMessage());
        }
        log.info("Raw response from Gemini Generative Model API: {}", responseStr);
        final GenerationResponse generationResponse;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            generationResponse = objectMapper.readValue(responseStr, GenerationResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("promptTokenCount: {}, candidatesTokenCount: {}, totalTokenCount: {}",
                generationResponse.getUsageMetadata().getPromptTokenCount(),
                generationResponse.getUsageMetadata().getCandidatesTokenCount(),
                generationResponse.getUsageMetadata().getTotalTokenCount());
        return generationResponse;
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
