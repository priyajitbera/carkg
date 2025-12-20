package com.github.priyajitbera.carkg.service.jena.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectionConfiguration {

  // The List that will be injected and populated
  private final ProjectionRegistrar.ProjectionsContainer registeredSparqlProjections =
      new ProjectionRegistrar.ProjectionsContainer();

  // Expose the List as a Spring Bean
  @Bean("registeredSparqlProjections")
  public ProjectionRegistrar.ProjectionsContainer registeredSparqlProjections() {
    return registeredSparqlProjections;
  }

  // A method for the post-processor to register classes
  public void registerSparqlSelectProjection(
      Class<?> projectionSchemaType, String extractedProjectionColumns) {
    registeredSparqlProjections.add(projectionSchemaType, extractedProjectionColumns);
  }
}
