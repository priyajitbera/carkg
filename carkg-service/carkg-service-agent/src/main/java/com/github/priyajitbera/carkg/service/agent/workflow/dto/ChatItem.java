package com.github.priyajitbera.carkg.service.agent.workflow.dto;

import lombok.Builder;
import lombok.Data;

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
