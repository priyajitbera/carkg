package com.github.priyajitbera.carkg.service.model.client.gemini.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class Part {
  private String text;
}
