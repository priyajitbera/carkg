package com.github.priyajitbera.carkg.service.model.client.gemini.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Builder
@Getter
public class Parameters {
    private String type;
    private Map<String, Object> properties;
}
