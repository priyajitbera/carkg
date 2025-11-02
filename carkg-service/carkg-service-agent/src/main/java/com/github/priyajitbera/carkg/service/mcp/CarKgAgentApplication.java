package com.github.priyajitbera.carkg.service.mcp;

import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class CarKgAgentApplication implements CommandLineRunner {

    @Autowired
    private List<McpSyncClient> mcpSyncClients;

    @Autowired
    private SyncMcpToolCallbackProvider toolCallbackProvider;

    public static void main(String[] args) {
        SpringApplication.run(CarKgAgentApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        mcpSyncClients.forEach(mcpSyncClient -> {
            System.out.println("ServerInstructions: " + mcpSyncClient.getServerInstructions());
            System.out.println("ClientInfo: " + mcpSyncClient.getClientInfo());
            System.out.println("ServerInfo: " + mcpSyncClient.getServerInfo());
        });

        System.out.println("ToolCallBack count: " + toolCallbackProvider.getToolCallbacks().length);
        Arrays.stream(toolCallbackProvider.getToolCallbacks()).forEach((callback) -> {
            System.out.println("ToolCallBack definition: " + callback.getToolDefinition() + "\n\n");
        });
    }
}