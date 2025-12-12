package com.github.priyajitbera.carkg.service.agent.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class Tool {
    private String name;
    private String description;
    private String inputSchema;
}
