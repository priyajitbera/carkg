package com.github.priyajitbera.carkg.service.agent.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkflowInvoke {
  private String name;
  private String taskPrompt;
}
