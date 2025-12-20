package com.github.priyajitbera.carkg.service.agent.workflow;

import com.github.priyajitbera.carkg.service.model.client.GenerationClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BrandCreateWorkflow extends AbstractWorkflow {

  public BrandCreateWorkflow(
      @Qualifier("GeminiGenerationClient") GenerationClient generationClient,
      SyncMcpToolCallbackProvider toolCallbackProvider) {
    super(generationClient, toolCallbackProvider);
  }

  @Override
  public String getName() {
    return "BrandCreateWorkflow";
  }

  @Override
  public String getRole() {
    return "Automotive Brand Research Agent";
  }

  @Override
  public String getRoleDescription() {
    return """
                Gather information about the given car brand from the official brand website or reliable sources.
                Make use of tools to ingest the data into the system.

                CRITICAL: Do not ingest any duplicate data. Always make redundant calls to read the existing data before creating new entries.

                Example: When searching whether a brand known as 'Maruti' already exists, search with 'Maruti', 'Maruti Suzuki', etc.,
                to be absolutely certain that no brand object for Maruti already exists.

                To gather data you have to use the internet (simulated via tools if available) or your internal knowledge.
                To verify whether redundant data exists in the system you have to use the provided MCP tools (e.g., `semanticSearchBrands`, `findBrandById`).

                This conversation is not being supervised by a human, so you can't ask any clarifying questions. At any point if you feel you can't continue, you have to end the task.""";
  }
}
