package com.github.priyajitbera.carkg.service.model.client.gemini.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class Tool {

    @JsonProperty("function_declarations")
    private List<FunctionDeclaration> functionDeclarations;
}
