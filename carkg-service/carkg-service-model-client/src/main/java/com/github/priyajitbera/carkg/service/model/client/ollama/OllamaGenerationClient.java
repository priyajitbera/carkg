package com.github.priyajitbera.carkg.service.model.client.ollama;

import com.github.priyajitbera.carkg.service.model.client.GenerationClient;
import com.github.priyajitbera.carkg.service.model.client.dto.request.Tool;
import com.github.priyajitbera.carkg.service.model.client.dto.response.GenerationResponse;
import java.util.List;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("OllamaGenerationClient")
public class OllamaGenerationClient implements GenerationClient {

  private final String OLLAMA_GENERATION_MODEL;

  private final OllamaEmbeddingModel embeddingModel;

  public OllamaGenerationClient(
      @Value("${ollama.generation.model}") String ollamaGenerationModel,
      OllamaEmbeddingModel embeddingModel) {
    this.OLLAMA_GENERATION_MODEL = ollamaGenerationModel;
    this.embeddingModel = embeddingModel;
  }

  public float[] embed(String text) {
    EmbeddingResponse embeddingResponse =
        embeddingModel.call(
            new EmbeddingRequest(
                List.of(text),
                OllamaOptions.builder().model(OLLAMA_GENERATION_MODEL).truncate(false).build()));
    return embeddingResponse.getResults().get(0).getOutput();
  }

  @Override
  public String generate(String promp) {
    throw new UnsupportedOperationException("OllamaClient does not support text generation yet.");
  }

  @Override
  public GenerationResponse generate(String prompt, List<Tool> tools) {
    throw new RuntimeException("Not implemented yet");
  }
}
