package com.github.priyajitbera.carkg.service.model.client.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class GenerationResponse {
    private String content;
    private ToolCallDetails toolCallDetails;
}
