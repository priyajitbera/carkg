package com.github.priyajitbera.carkg.service.agent.workflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.priyajitbera.carkg.service.agent.workflow.dto.*;
import com.github.priyajitbera.carkg.service.common.Serializer;
import com.github.priyajitbera.carkg.service.model.client.common.GenerativeClient;
import com.github.priyajitbera.carkg.service.model.client.common.response.GenerationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.execution.ToolExecutionException;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public abstract class AbstractWorkflow implements Workflow {

    protected final GenerativeClient generativeClient;
    protected final SyncMcpToolCallbackProvider toolCallbackProvider;

    public AbstractWorkflow(GenerativeClient generativeClient, SyncMcpToolCallbackProvider toolCallbackProvider) {
        this.generativeClient = generativeClient;
        this.toolCallbackProvider = toolCallbackProvider;
    }

    public abstract String getName();

    public abstract String getRole();

    public abstract String getRoleDescription();

    List<Tool> getTools() {
        return Arrays.stream(toolCallbackProvider.getToolCallbacks())
                .map(toolCallBack -> Tool.builder()
                        .name(toolCallBack.getToolDefinition().name())
                        .description(toolCallBack.getToolDefinition().description())
                        .inputSchema((toolCallBack.getToolDefinition().inputSchema()))
                        .build()).toList();
    }

    String getChatGuidelines() {
        return """
                # Chat Guidelines
                You are an autonomous agent. Your goal is to complete the assigned task using the available tools.
                You can request only one tool call in one response, for multiple tool calls your need to plan following responses.
                When finished with the given task or to abort at any step, only response with message 'END_TASK'""";
    }

    String buildFirstPrompt(String taskDetail) {
        AgentPrompt agentPrompt = AgentPrompt.builder()
                .role(getRole())
                .roleDescription(getRoleDescription())
                .chatGuidelines(getChatGuidelines())
                .taskDetail(taskDetail)
                .build();
        return Serializer.toTokenOriented(agentPrompt);
    }

    public void run(String taskPrompt) {
        Queue<Step> stepsToExecute = new LinkedList<>();

        final String firstPrompt = buildFirstPrompt(taskPrompt);
        final Step firstStep = Step.builder().prompt(firstPrompt).build();
        stepsToExecute.add(firstStep);

        Chat chat = Chat.builder().chatItems(new ArrayList<>()).build();

        while (!stepsToExecute.isEmpty()) {
            final Step step = stepsToExecute.poll();
            final String stepPrompt = step.getPrompt();

            chat.getChatItems().add(ChatItem.builder()
                    .index(chat.getChatItems().size() + 1)
                    .timeStampUtc(nowUtc())
                    .type(ChatItem.ChatItemType.CLIENT_TO_MODEL)
                    .clientToModel(ClientToModelChatItem.builder().prompt(stepPrompt).build()).build());

            String chatToon = Serializer.toTokenOriented(chat);
            log.info("prompt:\n{}", chatToon);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            List<com.github.priyajitbera.carkg.service.model.client.common.request.Tool> tools = mapTools(getTools());
            GenerationResponse generationResponse = generativeClient.generate(chatToon, tools);
            log.info("generationResponse:\n{}", Serializer.toTokenOriented(generationResponse));

            chat.getChatItems().add(ChatItem.builder()
                    .index(chat.getChatItems().size() + 1)
                    .timeStampUtc(nowUtc())
                    .type(ChatItem.ChatItemType.MODEL_TO_CLIENT)
                    .modelToClient(ModelToClientChatItem.builder().response(Serializer.toTokenOriented(generationResponse)).build()).build());

            Response.InstructionType instructionType = (generationResponse.getContent() == null && generationResponse.getToolCallDetails() == null) ||
                    (generationResponse.getContent() != null && generationResponse.getContent().contains("END_TASK"))
                    ? Response.InstructionType.END_TASK
                    : Response.InstructionType.MESSAGE;
            final Response response = Response.builder()
                    .instructionType(instructionType)
                    .messageDetails(MessageDetails.builder().message(generationResponse.getContent()).build())
                    .toolCallDetails(generationResponse.getToolCallDetails() == null ? null : mapToolCallDetails(generationResponse.getToolCallDetails()))
                    .build();

            switch (response.getInstructionType()) {
                case END_TASK:
                    handleInstructionTypeEndTask(response, chat, stepsToExecute);
                    break;
                case MESSAGE:
                    handleInstructionTypeMessage(response, chat, stepsToExecute);
                    break;
                default:
                    break;
            }
        }
    }

    private String nowUtc() {
        return ZonedDateTime.now(ZoneId.of("UTC")).toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    List<com.github.priyajitbera.carkg.service.model.client.common.request.Tool> mapTools(List<Tool> tools) {
        return tools.stream().map(tool -> com.github.priyajitbera.carkg.service.model.client.common.request.Tool.builder()
                .name(tool.getName())
                .description(tool.getDescription())
                .inputSchema(tool.getInputSchema())
                .build()).toList();
    }

    ToolCallDetails mapToolCallDetails(com.github.priyajitbera.carkg.service.model.client.common.response.ToolCallDetails toolCallDetails) {
        return ToolCallDetails.builder()
                .name(toolCallDetails.getName())
                .parameters(toolCallDetails.getParameters())
                .build();
    }

    void handleInstructionTypeEndTask(Response response, Chat chat, Queue<Step> stepsToExecute) {
        log.info("END_TASK received, ending workflow execution.");
    }

    void handleInstructionTypeMessage(Response response, Chat chat, Queue<Step> stepsToExecute) {
        String nextStepPrompt = "Plan upcoming steps from conversation context.";
        if (response.getToolCallDetails() != null) {
            try {
                executeToolCall(response.getToolCallDetails(), chat);
                nextStepPrompt = "Based on the tool execution result, plan the next steps accordingly.";
            } catch (IllegalArgumentException e) {
                String errorMessage = "Error during tool execution: " + e.getMessage();
                nextStepPrompt = "toolCallDetails is invalid: " + errorMessage;
            }
        }
        Step nextStep = Step.builder().prompt(nextStepPrompt).build();
        stepsToExecute.add(nextStep);
    }

    void executeToolCall(ToolCallDetails toolCallDetails, Chat chat) {
        if (toolCallDetails.getName() == null || toolCallDetails.getName().isEmpty()) {
            throw new IllegalArgumentException("Tool name can not be null or empty.");
        }
        var toolCallback = Arrays.stream(toolCallbackProvider.getToolCallbacks())
                .filter(callback -> callback.getToolDefinition().name().equals(toolCallDetails.getName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No tool found with name: " + toolCallDetails.getName()));

        String toolInput = buildToolInput(toolCallDetails);
        log.info("Executing Tool: {} with input: {}", toolCallDetails.getName(), toolInput);
        final ToolResponse toolResponse;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String toolCallOutputText = toolCallback.call(toolInput);
            log.info("After executing Tool: {} toolCallOutputText: {}", toolCallDetails.getName(), toolCallOutputText);
            toolResponse = deserializeToolResponses(toolCallOutputText).get(0);
        } catch (ToolExecutionException e) {
            // tool call error
            log.error("Error while executing Tool: {} {}", toolCallDetails.getName(), e.getMessage());
            String errorMessage = "Error occurred during tool execution: " + e.getMessage();
            final ChatItem chatItem;
            try {
                chatItem = ChatItem.builder()
                        .type(ChatItem.ChatItemType.TOOL_CALL_RESULT)
                        // tool input : JSON, tool error output: string
                        .toolCall(ToolCallChatItem.<JsonNode, String>builder().input(objectMapper.readTree(toolInput)).output(errorMessage).build())
                        .build();
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
            chat.getChatItems()
                    .add(chatItem);
            return;
        }

        // tool call success
        final ChatItem chatItem;
        try {
            chatItem = ChatItem.builder()
                    .type(ChatItem.ChatItemType.TOOL_CALL_RESULT)
                    // tool input : JSON, tool output: JSON
                    .toolCall(ToolCallChatItem.<JsonNode, JsonNode>builder().input(objectMapper.readTree(toolInput)).output(objectMapper.readTree((toolResponse.getText()))).build())
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        chat.getChatItems()
                .add(chatItem);
    }

    String buildToolInput(ToolCallDetails toolCallDetails) {
        return toolCallDetails.getParameters().toString();
    }

    List<ToolResponse> deserializeToolResponses(String toolResponseText) {
        try {
            return new ObjectMapper().readValue(toolResponseText, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
