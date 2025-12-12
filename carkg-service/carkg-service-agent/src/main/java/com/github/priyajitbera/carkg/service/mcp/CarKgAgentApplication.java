package com.github.priyajitbera.carkg.service.mcp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;

@Slf4j
@ComponentScan(basePackages = {"com.github.priyajitbera.carkg.service.mcp",
        "com.github.priyajitbera.carkg.service.model.client"})
@SpringBootApplication
public class CarKgAgentApplication implements CommandLineRunner {

    @Autowired
    private final SyncMcpToolCallbackProvider syncMcpToolCallbackProvider;

    public CarKgAgentApplication(SyncMcpToolCallbackProvider syncMcpToolCallbackProvider) {
        this.syncMcpToolCallbackProvider = syncMcpToolCallbackProvider;
    }

    public static void main(String[] args) {
        SpringApplication.run(CarKgAgentApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // log the available MCP Sync Clients
        Arrays.stream(syncMcpToolCallbackProvider.getToolCallbacks()).forEach(toolCallback -> log.info("Tool Callback: {}",
                toolCallback.getToolDefinition().name()));
    }
}