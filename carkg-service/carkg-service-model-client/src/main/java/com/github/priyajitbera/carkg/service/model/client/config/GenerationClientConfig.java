package com.github.priyajitbera.carkg.service.model.client.config;

import com.github.priyajitbera.carkg.service.model.client.GenerationClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class GenerationClientConfig {

  private final String activeGenerationClient;
  private final ApplicationContext applicationContext;

  public GenerationClientConfig(
      @Value("${generation-client-config.active}") String activeGenerationClient,
      ApplicationContext applicationContext) {
    this.activeGenerationClient = activeGenerationClient;
    this.applicationContext = applicationContext;
  }

  @Primary
  @Bean
  public GenerationClient generationClient() {
    return applicationContext.getBean(activeGenerationClient, GenerationClient.class);
  }
}
