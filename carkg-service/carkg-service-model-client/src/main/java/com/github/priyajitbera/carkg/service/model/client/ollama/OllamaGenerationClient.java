package com.github.priyajitbera.carkg.service.model.client.ollama;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.priyajitbera.carkg.service.model.client.GenerationClient;
import com.github.priyajitbera.carkg.service.model.client.dto.request.Tool;
import com.github.priyajitbera.carkg.service.model.client.dto.response.GenerationResponse;
import com.github.priyajitbera.carkg.service.model.client.dto.response.ToolCallDetails;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Component("OllamaGenerationClient")
public class OllamaGenerationClient implements GenerationClient {

  private final String OLLAMA_GENERATION_MODEL;

  private final ChatModel chatModel;

  public OllamaGenerationClient(
      @Value("${ollama.generation.model}") String ollamaGenerationModel,
      OllamaChatModel chatModel) {
    this.OLLAMA_GENERATION_MODEL = ollamaGenerationModel;
    this.chatModel = chatModel;
  }

  @Override
  public String generate(String prompt) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    log.info("[generate] Before calling model");
    ChatResponse chatResponse =
        chatModel.call(
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
  public GenerationResponse generate(String promptText, List<Tool> tools) {
    List<ToolCallback> toolCallbacks =
        tools.stream()
            .map(
                tool ->
                    FunctionToolCallback.<String, String>builder(tool.getName(), (ip, op) -> "")
                        .inputType(String.class)
                        .inputSchema(tool.getInputSchema())
                        .description(tool.getDescription())
                        .build())
            .map(toolCallBack -> (ToolCallback) toolCallBack)
            .toList();

    ChatOptions chatOptions =
        ToolCallingChatOptions.builder()
            .model(OLLAMA_GENERATION_MODEL)
            .toolCallbacks(toolCallbacks)
            .build();
    Prompt prompt = new Prompt(promptText, chatOptions);
    ChatResponse chatResponse = chatModel.call(prompt);

    AssistantMessage assistantMessage = chatResponse.getResult().getOutput();

    GenerationResponse.GenerationResponseBuilder generationResponseBuilder =
        GenerationResponse.builder().content(assistantMessage.getText());

    // 5️⃣ If model called a tool
    if (assistantMessage.getToolCalls() != null && !assistantMessage.getToolCalls().isEmpty()) {

      AssistantMessage.ToolCall responseToolCall = assistantMessage.getToolCalls().get(0);
      final JsonNode paramsJsonNode;
      try {
        paramsJsonNode = new ObjectMapper().readTree(responseToolCall.arguments());
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }

      ToolCallDetails toolCallDetails =
          ToolCallDetails.builder()
              .name(responseToolCall.name())
              .parameters(paramsJsonNode)
              .build();
      generationResponseBuilder.toolCallDetails(toolCallDetails);
    }

    return generationResponseBuilder.build();
  }
}
