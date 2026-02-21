package com.github.priyajitbera.carkg.service.api.config;

import com.github.priyajitbera.carkg.service.jena.config.ProjectionRegistrar;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomProjectionRegistrar extends ProjectionRegistrar {

  @Override
  public String getSparqlProjectionsBasePackage() {
    return "com.github.priyajitbera.carkg.service.api.projection.sparql";
  }
}
