package com.github.priyajitbera.carkg.service.mcp.agent.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ToolCallChatItem<IP, OP> {
    private String name;
    private IP input;
    private OP output;
}
