package com.github.priyajitbera.carkg.service.mcp.agent.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatItem {
    public enum ChatItemType {
        CLIENT_TO_MODEL,
        TOOL_CALL_RESULT,
        MODEL_TO_CLIENT
    }

    private Integer index;
    private String timeStampUtc;
    private ChatItemType type;

    private ClientToModelChatItem clientToModel;
    private ToolCallChatItem toolCall;
    private ModelToClientChatItem modelToClient;
}
