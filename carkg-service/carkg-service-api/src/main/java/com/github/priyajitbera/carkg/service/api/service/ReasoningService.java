package com.github.priyajitbera.carkg.service.api.service;

import com.github.priyajitbera.carkg.service.api.client.GenerativeClient;
import com.github.priyajitbera.carkg.service.api.client.JenaFusekiClient;
import com.github.priyajitbera.carkg.service.api.model.request.AskFixedSchema;
import com.github.priyajitbera.carkg.service.api.model.response.RegisteredSparqlProjectionSchema;
import com.github.priyajitbera.carkg.service.jena.config.ProjectionRegistrar;
import com.github.priyajitbera.carkg.service.jena.mapper.SparqlJsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class ReasoningService {

    private final String FORMAT = "RDF/XML";
    private final GenerativeClient geminiClient;
    private final JenaFusekiClient jenaFusekiClient;
    private final SparqlJsonMapper sparqlJsonMapper;
    private final ProjectionRegistrar.ProjectionsContainer projectionsContainer;

    private final Map<String, String> ontologySchemas;

    private final String SPARQL_GENERATION_PROMPT_TEMPLATE;
    private final String SPARQL_GENERATION_FIX_SCHEMA_PROMPT_TEMPLATE;
    private final String SPARQL_RESPONSE_NL_RESPONSE_TEMPLATE;

    public ReasoningService(
            @Qualifier("geminiClient")
            GenerativeClient geminiClient,
            JenaFusekiClient jenaFusekiClient,
            SparqlJsonMapper sparqlJsonMapper,
            @Qualifier("registeredSparqlProjections")
            ProjectionRegistrar.ProjectionsContainer projectionsContainer,
            @Qualifier("ontologySchemas")
            Map<String, String> ontologySchemas
    ) {
        this.geminiClient = geminiClient;
        this.jenaFusekiClient = jenaFusekiClient;
        this.sparqlJsonMapper = sparqlJsonMapper;
        this.projectionsContainer = projectionsContainer;
        this.ontologySchemas = ontologySchemas;
        try {
            this.SPARQL_GENERATION_PROMPT_TEMPLATE = Files.readString(Path.of("carkg-service/carkg-service-api/src/main/resources/prompts/sparql_generation.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            this.SPARQL_GENERATION_FIX_SCHEMA_PROMPT_TEMPLATE = Files.readString(Path.of("carkg-service/carkg-service-api/src/main/resources/prompts/sparql_generation_fixed_schema.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            this.SPARQL_RESPONSE_NL_RESPONSE_TEMPLATE = Files.readString(Path.of("carkg-service/carkg-service-api/src/main/resources/prompts/sparql_response_to_nl_response.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateSparql(String question) {
        final String prompt = String.format(SPARQL_GENERATION_PROMPT_TEMPLATE, ontologySchemas.get(FORMAT), question);
        log.info("[generateSparql] prompts:\n{}", prompt);

        String generatedSparql = geminiClient.generate(prompt);
        log.info("[generateSparql] Generated SPARQL:\n{}", generatedSparql);

        return generatedSparql;
    }

    public Map.Entry<String, Class<?>> generateSparqlFixedSchema(String question, String projectionSchema) {
        Optional<Map.Entry<Class<?>, String>> projectionSchemaOpt = projectionsContainer.get(projectionSchema);
        if (projectionSchemaOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Requested projection schema %s not found", projectionSchemaOpt));

        final String projectionColumns = projectionSchemaOpt.get().getValue();
        log.info("[generateSparqlFixedSchema] projectionColumns:\n{}", projectionColumns);

        final String prompt = String.format(SPARQL_GENERATION_FIX_SCHEMA_PROMPT_TEMPLATE, ontologySchemas.get(FORMAT), question, projectionColumns);
        log.info("[generateSparqlFixedSchema] prompts:\n{}", prompt);

        String generatedSparql = geminiClient.generate(prompt);
        log.info("[generateSparqlFixedSchema] Generated SPARQL:\n{}", generatedSparql);
        return Map.entry(generatedSparql, projectionSchemaOpt.get().getKey());
    }

    public String askRaw(String question) {
        final String generatedSparql = generateSparql(question);
        log.info("[askRaw] Generated Sparql:\n{}", generatedSparql);

        final String sanitizedSparql = sanitizeSparql(generatedSparql);
        log.info("[askRaw] Sanitized Sparql:\n{}", sanitizedSparql);

        String result = String.join("\n", jenaFusekiClient.runSelectQuery(sanitizedSparql));
        final String response = "SPARQL:\n" + sanitizedSparql + "\n\nAnswer:\n" + result;

        log.info("[askRaw] response:\n{}", response);
        return response;
    }

    public String askHumanize(String question) {
        final String generatedSparql = generateSparql(question);
        log.info("[askHumanize] Generated Sparql:\n{}", generatedSparql);

        final String sanitizedSparql = sanitizeSparql(generatedSparql);
        log.info("[askHumanize] Sanitized Sparql:\n{}", sanitizedSparql);

        final String jenaFusekiResponse = String.join("\n", jenaFusekiClient.runSelectQuery(sanitizedSparql));
        final String humanizePrompt = String.format(SPARQL_RESPONSE_NL_RESPONSE_TEMPLATE, question, jenaFusekiResponse);
        log.info("[askHumanize] Humanize Prompt:\n{}", humanizePrompt);

        final String response = geminiClient.generate(humanizePrompt);
        log.info("[askRaw] response:\n{}", response);

        return response;
    }

    public Object askFixedSchema(AskFixedSchema askFixedSchema) {
        final Map.Entry<String, Class<?>> generatedSparql = generateSparqlFixedSchema(askFixedSchema.getAsk(), askFixedSchema.getSchema());
        log.info("[askFixedSchema] Generated Sparql:\n{}", generatedSparql.getKey());
        if (generatedSparql.getKey() == null || generatedSparql.getKey().contains("UNABLE_TO_GENERATE_SPARQL"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        final String sanitizedSparql = sanitizeSparql(generatedSparql.getKey());
        log.info("[askFixedSchema] Sanitized Sparql:\n{}", sanitizedSparql);

        final String jenaFusekiResponse = String.join("\n", jenaFusekiClient.runSelectQueryJson(sanitizedSparql));
        log.info("[askFixedSchema] Jena Fuseki response:\n{}", jenaFusekiResponse);

        Object fixedSchema = sparqlJsonMapper.map(jenaFusekiResponse, generatedSparql.getValue());
        log.info("[askFixedSchema] fixed schema response:\n{}", fixedSchema);
        return fixedSchema;
    }

    private String sanitizeSparql(String generatedSparql) {
        final String santized = generatedSparql.replace("```sparql", "").replace("```", "");
        return santized;
    }

    public List<RegisteredSparqlProjectionSchema> getAvailableProjections() {
        List<RegisteredSparqlProjectionSchema> availableProjections = projectionsContainer.getAll().stream()
                .map(schema -> RegisteredSparqlProjectionSchema.builder()
                        .name(schema.getKey().getName()).columns(schema.getValue()).build()).toList();
        return availableProjections;
    }
}
