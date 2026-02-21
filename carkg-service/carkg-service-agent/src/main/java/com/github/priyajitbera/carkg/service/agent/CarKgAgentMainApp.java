package com.github.priyajitbera.carkg.service.agent;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@ComponentScan(
    basePackages = {
      "com.github.priyajitbera.carkg.service.mcp",
      "com.github.priyajitbera.carkg.service.model.client.gemini",
      "com.github.priyajitbera.carkg.service.agent"
    })
@SpringBootApplication
public class CarKgAgentMainApp implements CommandLineRunner {

  @Autowired private final SyncMcpToolCallbackProvider syncMcpToolCallbackProvider;

  public CarKgAgentMainApp(SyncMcpToolCallbackProvider syncMcpToolCallbackProvider) {
    this.syncMcpToolCallbackProvider = syncMcpToolCallbackProvider;
  }

  public static void main(String[] args) {
    SpringApplication.run(CarKgAgentMainApp.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    // log the available MCP Sync Clients
    Arrays.stream(syncMcpToolCallbackProvider.getToolCallbacks())
        .forEach(
            toolCallback -> log.info("Tool Callback: {}", toolCallback.getToolDefinition().name()));
  }
}
