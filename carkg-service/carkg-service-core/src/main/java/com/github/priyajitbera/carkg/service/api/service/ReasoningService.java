package com.github.priyajitbera.carkg.service.api.service;

import com.github.priyajitbera.carkg.service.api.client.JenaFusekiClient;
import com.github.priyajitbera.carkg.service.api.model.request.AskFixedSchema;
import com.github.priyajitbera.carkg.service.api.model.response.RegisteredSparqlProjectionSchema;
import com.github.priyajitbera.carkg.service.api.prompt.PromptRepository;
import com.github.priyajitbera.carkg.service.api.prompt.Prompts;
import com.github.priyajitbera.carkg.service.jena.config.ProjectionRegistrar;
import com.github.priyajitbera.carkg.service.jena.mapper.SparqlJsonMapper;
import com.github.priyajitbera.carkg.service.model.client.GenerationClient;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class ReasoningService {

  private final String ontologyFormat;
  private final GenerationClient generationClient;
  private final JenaFusekiClient jenaFusekiClient;
  private final SparqlJsonMapper sparqlJsonMapper;
  private final ProjectionRegistrar.ProjectionsContainer projectionsContainer;

  private final Map<String, String> ontologySchemas;

  private final PromptRepository promptRepository;

  public ReasoningService(
      @Value("${reasoning-service.ontology-format}") String ontologyFormat,
      GenerationClient generationClient,
      JenaFusekiClient jenaFusekiClient,
      SparqlJsonMapper sparqlJsonMapper,
      @Qualifier("registeredSparqlProjections")
          ProjectionRegistrar.ProjectionsContainer projectionsContainer,
      @Qualifier("ontologySchemas") Map<String, String> ontologySchemas,
      PromptRepository promptRepository) {
    this.ontologyFormat = ontologyFormat;
    this.generationClient = generationClient;
    this.jenaFusekiClient = jenaFusekiClient;
    this.sparqlJsonMapper = sparqlJsonMapper;
    this.projectionsContainer = projectionsContainer;
    this.ontologySchemas = ontologySchemas;
    this.promptRepository = promptRepository;
  }

  public String generateSparql(String question) {
    final String promptTemplate =
        promptRepository.getPrompt(Prompts.SPARQL_GENERATION_PROMPT.name());
    final String ontologySchema = ontologySchemas.get(ontologyFormat);
    final String prompt = String.format(promptTemplate, ontologySchema, question);
    log.info("[generateSparql] prompt: {}", prompt);

    String generatedSparql = generationClient.generate(prompt);
    log.info("[generateSparql] Generated SPARQL:\n{}", generatedSparql);

    return generatedSparql;
  }

  public Map.Entry<String, Class<?>> generateSparqlFixedSchema(
      String question, String projectionSchema) {
    Optional<Map.Entry<Class<?>, String>> projectionSchemaOpt =
        projectionsContainer.get(projectionSchema);
    if (projectionSchemaOpt.isEmpty())
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          String.format("Requested projection schema %s not found", projectionSchemaOpt));

    final String projectionColumns = projectionSchemaOpt.get().getValue();
    log.info("[generateSparqlFixedSchema] projectionColumns:\n{}", projectionColumns);

    final String prompt =
        String.format(
            promptRepository.getPrompt(Prompts.SPARQL_GENERATION_FIX_SCHEMA_PROMPT.name()),
            ontologySchemas.get(ontologyFormat),
            question,
            projectionColumns);
    log.info("[generateSparqlFixedSchema] prompts:\n{}", prompt);

    String generatedSparql = generationClient.generate(prompt);
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

    final String jenaFusekiResponse =
        String.join("\n", jenaFusekiClient.runSelectQuery(sanitizedSparql));
    final String humanizePrompt =
        String.format(
            promptRepository.getPrompt(Prompts.SPARQL_RESPONSE_NL_RESPONSE.name()),
            question,
            jenaFusekiResponse);
    log.info("[askHumanize] Humanize Prompt:\n{}", humanizePrompt);

    final String response = generationClient.generate(humanizePrompt);
    log.info("[askRaw] response:\n{}", response);

    return response;
  }

  public Object askFixedSchema(AskFixedSchema askFixedSchema) {
    final Map.Entry<String, Class<?>> generatedSparql =
        generateSparqlFixedSchema(askFixedSchema.getAsk(), askFixedSchema.getSchema());
    log.info("[askFixedSchema] Generated Sparql:\n{}", generatedSparql.getKey());
    if (generatedSparql.getKey() == null
        || generatedSparql.getKey().contains("UNABLE_TO_GENERATE_SPARQL"))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);

    final String sanitizedSparql = sanitizeSparql(generatedSparql.getKey());
    log.info("[askFixedSchema] Sanitized Sparql:\n{}", sanitizedSparql);

    final String jenaFusekiResponse =
        String.join("\n", jenaFusekiClient.runSelectQueryJson(sanitizedSparql));
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
    List<RegisteredSparqlProjectionSchema> availableProjections =
        projectionsContainer.getAll().stream()
            .map(
                schema ->
                    RegisteredSparqlProjectionSchema.builder()
                        .name(schema.getKey().getName())
                        .columns(schema.getValue())
                        .build())
            .toList();
    return availableProjections;
  }
}
