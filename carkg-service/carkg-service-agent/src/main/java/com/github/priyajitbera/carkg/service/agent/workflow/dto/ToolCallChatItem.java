package com.github.priyajitbera.carkg.service.agent.workflow.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ToolCallChatItem<IP, OP> {
  private String name;
  private IP input;
  private OP output;
}
