package com.github.priyajitbera.carkg.service.agent.config;

import com.github.priyajitbera.carkg.service.agent.workflow.AbstractWorkflow;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class WorkflowContainer {

  private final List<AbstractWorkflow> workflows = new ArrayList<>();

  public void registerWorkflow(AbstractWorkflow workflow) {
    workflows.add(workflow);
  }

  public List<AbstractWorkflow> getWorkflows() {
    return List.copyOf(workflows);
  }
}
