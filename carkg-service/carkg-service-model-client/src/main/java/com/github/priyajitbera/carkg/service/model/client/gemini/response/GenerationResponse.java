package com.github.priyajitbera.carkg.service.model.client.gemini.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class GenerationResponse {
  private List<Candidate> candidates;
  private UsageMetadata usageMetadata;
}
