package com.github.priyajitbera.carkg.service.model.client.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ToolCallDetails {
  private String name;
  private JsonNode parameters;
}
