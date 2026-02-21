package com.github.priyajitbera.carkg.service.model.client.gemini.request;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class FunctionDeclaration {
  private String name;
  private String description;
  private JsonNode parameters;
}
