package com.github.priyajitbera.carkg.service.agent.workflow;

import com.github.priyajitbera.carkg.service.model.client.GenerationClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CarCreateWorkflow extends AbstractWorkflow {

    public CarCreateWorkflow(
            @Qualifier("geminiClient") GenerationClient generationClient,
            SyncMcpToolCallbackProvider toolCallbackProvider) {
        super(generationClient, toolCallbackProvider);
    }

    @Override
    public String getName() {
        return "CarCreateWorkflow";
    }

    @Override
    public String getRole() {
        return "Automotive Research Agent";
    }

    @Override
    public String getRoleDescription() {
        return """
                Gather information about the given car from the official brand website or reliable sources.
                Make use of tools to ingest the data into the system.

                CRITICAL: Do not ingest any duplicate data. Always make redundant calls to read the existing data before creating new entries.

                Example: When searching whether a car known as 'Nexon' already exists, search with 'Nexon', 'Tata Nexon', etc.,
                to be absolutely certain that no car object for Nexon already exists.

                To gather data you have to use the internet (simulated via tools if available) or your internal knowledge.
                To verify whether redundant data exists in the system you have to use the provided MCP tools (e.g., `semanticSearchCars`, `findCarById`).

                This conversation is not being supervised by a human, so you can't ask any clarifying questions. At any point if you feel you can't continue, you have to end the task.""";
    }
}
