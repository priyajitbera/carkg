package com.github.priyajitbera.carkg.service.mcp.agent.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ToolCallDetails {
    private String name;
    private JsonNode parameters;

}
