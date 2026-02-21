package com.github.priyajitbera.carkg.service.agent.workflow.dto;

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
