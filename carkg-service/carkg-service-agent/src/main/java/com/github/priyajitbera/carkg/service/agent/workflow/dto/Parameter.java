package com.github.priyajitbera.carkg.service.agent.workflow.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Parameter {
  private String name;
  private Object value;
}
