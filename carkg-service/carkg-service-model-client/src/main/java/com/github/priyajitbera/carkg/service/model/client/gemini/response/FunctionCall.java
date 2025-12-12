package com.github.priyajitbera.carkg.service.model.client.gemini.response;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class FunctionCall {
    private String name;
    private JsonNode args;
}
