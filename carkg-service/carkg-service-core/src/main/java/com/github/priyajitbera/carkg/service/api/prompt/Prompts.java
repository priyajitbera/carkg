package com.github.priyajitbera.carkg.service.api.prompt;

public enum Prompts {
  SPARQL_GENERATION_PROMPT("sparql_generation.txt"),
  SPARQL_GENERATION_FIX_SCHEMA_PROMPT("sparql_generation_fixed_schema.txt"),
  SPARQL_RESPONSE_NL_RESPONSE("sparql_response_to_nl_response.txt");

  private final String fileName;

  Prompts(String fileName) {
    this.fileName = fileName;
  }

  public String getFileName() {
    return fileName;
  }
}
