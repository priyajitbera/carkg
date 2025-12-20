package com.github.priyajitbera.carkg.service.model.client.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class GenerationResponse {
  private String content;
  private ToolCallDetails toolCallDetails;
}
