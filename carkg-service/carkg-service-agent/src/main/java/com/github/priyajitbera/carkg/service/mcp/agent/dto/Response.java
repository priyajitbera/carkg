package com.github.priyajitbera.carkg.service.mcp.agent.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Response {
    public enum InstructionType {
        MESSAGE,
        END_TASK
    }

    private InstructionType instructionType;
    private ToolCallDetails toolCallDetails;
    private MessageDetails messageDetails;
}
