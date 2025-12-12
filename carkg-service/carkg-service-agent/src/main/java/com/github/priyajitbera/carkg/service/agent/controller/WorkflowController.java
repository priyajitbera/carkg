package com.github.priyajitbera.carkg.service.agent.controller;

import com.github.priyajitbera.carkg.service.agent.config.WorkflowContainer;
import com.github.priyajitbera.carkg.service.agent.dto.WorkflowCreate;
import com.github.priyajitbera.carkg.service.agent.workflow.AbstractWorkflow;
import com.github.priyajitbera.carkg.service.model.client.common.GenerativeClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workflow")
public class WorkflowController {

    private final WorkflowContainer workflowContainer;
    private final GenerativeClient generativeClient;
    private final SyncMcpToolCallbackProvider toolCallbackProvider;

    public WorkflowController(
            WorkflowContainer workflowContainer, @Qualifier("geminiClient") GenerativeClient generativeClient,
            SyncMcpToolCallbackProvider toolCallbackProvider) {
        this.workflowContainer = workflowContainer;
        this.generativeClient = generativeClient;
        this.toolCallbackProvider = toolCallbackProvider;
    }

    @PostMapping
    public void createWorkflow(@RequestBody WorkflowCreate workflowCreate) {
        // Implementation goes here
        workflowContainer.registerWorkflow(new AbstractWorkflow(generativeClient, toolCallbackProvider) {
            @Override
            public String getName() {
                return workflowCreate.getName();
            }

            @Override
            public String getRole() {
                return workflowCreate.getRole();
            }

            @Override
            public String getRoleDescription() {
                return workflowCreate.getDescription();
            }
        });
    }
}
