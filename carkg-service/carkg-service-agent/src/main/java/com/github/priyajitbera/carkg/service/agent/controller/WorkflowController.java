package com.github.priyajitbera.carkg.service.agent.controller;

import com.github.priyajitbera.carkg.service.agent.config.WorkflowContainer;
import com.github.priyajitbera.carkg.service.agent.dto.request.WorkflowCreate;
import com.github.priyajitbera.carkg.service.agent.dto.response.Workflow;
import com.github.priyajitbera.carkg.service.agent.workflow.AbstractWorkflow;
import com.github.priyajitbera.carkg.service.model.client.GenerationClient;
import java.util.List;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("workflow")
public class WorkflowController {

  private final WorkflowContainer workflowContainer;
  private final GenerationClient generationClient;
  private final SyncMcpToolCallbackProvider toolCallbackProvider;

  public WorkflowController(
      WorkflowContainer workflowContainer,
      @Qualifier("GeminiGenerationClient") GenerationClient generationClient,
      SyncMcpToolCallbackProvider toolCallbackProvider) {
    this.workflowContainer = workflowContainer;
    this.generationClient = generationClient;
    this.toolCallbackProvider = toolCallbackProvider;
  }

  @PostMapping
  public List<Workflow> createWorkflow(@RequestBody List<WorkflowCreate> workflowCreates) {
    // Implementation goes here
    return workflowCreates.stream()
        .map(
            workflowCreate -> {
              AbstractWorkflow workflow =
                  new AbstractWorkflow(generationClient, toolCallbackProvider) {
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
                  };
              workflowContainer.registerWorkflow(workflow);
              return workflow;
            })
        .map(this::map)
        .toList();
  }

  @GetMapping
  public List<Workflow> getWorkflows() {
    return workflowContainer.getWorkflows().stream().map(this::map).toList();
  }

  private Workflow map(AbstractWorkflow workflow) {
    return Workflow.builder()
        .name(workflow.getName())
        .role(workflow.getRole())
        .description(workflow.getRoleDescription())
        .build();
  }
}
