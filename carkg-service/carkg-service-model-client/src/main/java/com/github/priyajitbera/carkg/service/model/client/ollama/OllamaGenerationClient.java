package com.github.priyajitbera.carkg.service.model.client.ollama;

import com.github.priyajitbera.carkg.service.model.client.GenerationClient;
import com.github.priyajitbera.carkg.service.model.client.dto.request.Tool;
import com.github.priyajitbera.carkg.service.model.client.dto.response.GenerationResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Component("OllamaGenerationClient")
public class OllamaGenerationClient implements GenerationClient {

  private final String OLLAMA_GENERATION_MODEL;

  private final OllamaChatModel model;

  public OllamaGenerationClient(
      @Value("${ollama.generation.model}") String ollamaGenerationModel, OllamaChatModel model) {
    this.OLLAMA_GENERATION_MODEL = ollamaGenerationModel;
    this.model = model;
  }

  @Override
  public String generate(String prompt) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    log.info("[generate] Before calling model");
    ChatResponse chatResponse =
        model.call(
            Prompt.builder()
                .chatOptions(ChatOptions.builder().model(OLLAMA_GENERATION_MODEL).build())
                .content(prompt)
                .build());
    stopWatch.stop();
    log.info("[generate] After calling model, took {} ms", stopWatch.getTotalTimeMillis());
    String response =
        chatResponse.getResults().stream()
            .map(generation -> generation.getOutput().getText())
            .collect(Collectors.joining());
    return response;
  }

  @Override
  public GenerationResponse generate(String prompt, List<Tool> tools) {
    throw new RuntimeException("Not implemented yet");
  }
}
