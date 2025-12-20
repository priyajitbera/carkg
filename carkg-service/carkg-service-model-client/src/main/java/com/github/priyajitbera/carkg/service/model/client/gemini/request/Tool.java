package com.github.priyajitbera.carkg.service.model.client.gemini.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class Tool {

  @JsonProperty("function_declarations")
  private List<FunctionDeclaration> functionDeclarations;
}
