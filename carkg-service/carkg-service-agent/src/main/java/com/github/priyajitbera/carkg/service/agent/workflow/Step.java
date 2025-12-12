package com.github.priyajitbera.carkg.service.agent.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Step {
    private String prompt;
}
