package com.github.priyajitbera.carkg.service.agent.config;

import com.github.priyajitbera.carkg.service.agent.workflow.Workflow;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WorkflowContainer {

    private final List<Workflow> workflows = new ArrayList<>();

    public void registerWorkflow(Workflow workflow) {
        workflows.add(workflow);
    }

    public List<Workflow> getWorkflows() {
        return List.copyOf(workflows);
    }
}
