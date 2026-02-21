package com.github.priyajitbera.carkg.service.model.client.gemini.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Candidate {
  private Integer index;
  private Content content;
  private String finishReason;
  private String finishMessage;
}
