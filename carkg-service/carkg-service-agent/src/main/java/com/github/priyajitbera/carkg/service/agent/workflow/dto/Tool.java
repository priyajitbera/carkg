package com.github.priyajitbera.carkg.service.agent.workflow.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Tool {
    private String name;

    @JsonRawValue
    private String inputSchema;

    @JsonRawValue
    private String description;
}
