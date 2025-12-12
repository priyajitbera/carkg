package com.github.priyajitbera.carkg.service.model.client.common.request;

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
