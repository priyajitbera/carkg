package com.github.priyajitbera.carkg.service.model.client.gemini.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class GenerationRequest {
  private List<Content> contents;
  private List<Tool> tools;
}
