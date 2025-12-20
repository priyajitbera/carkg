package com.github.priyajitbera.carkg.service.mcp;

import com.github.priyajitbera.carkg.service.mcp.tool.CarKgMcpTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpToolConfig {

  @Bean
  ToolCallbackProvider toolCallbackProvider(CarKgMcpTools tools) {
    return MethodToolCallbackProvider.builder().toolObjects(tools).build();
  }
}
