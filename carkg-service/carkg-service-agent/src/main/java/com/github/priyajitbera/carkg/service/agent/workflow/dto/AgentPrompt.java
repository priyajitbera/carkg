package com.github.priyajitbera.carkg.service.agent.workflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class AgentPrompt {
    private String role;
    private String roleDescription;
    private String chatGuidelines;
    private String taskDetail;
}
