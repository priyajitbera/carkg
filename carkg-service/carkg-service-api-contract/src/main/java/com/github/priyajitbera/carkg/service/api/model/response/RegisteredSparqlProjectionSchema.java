package com.github.priyajitbera.carkg.service.api.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisteredSparqlProjectionSchema {
  private String name;
  private String columns;
}
