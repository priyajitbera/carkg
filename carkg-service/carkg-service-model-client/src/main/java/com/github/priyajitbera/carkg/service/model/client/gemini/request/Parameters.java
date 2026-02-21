package com.github.priyajitbera.carkg.service.model.client.gemini.request;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class Parameters {
  private String type;
  private Map<String, Object> properties;
}
