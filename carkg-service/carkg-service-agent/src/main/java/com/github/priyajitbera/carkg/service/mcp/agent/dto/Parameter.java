package com.github.priyajitbera.carkg.service.mcp.agent.dto;

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
