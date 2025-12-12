package com.github.priyajitbera.carkg.service.model.client.gemini.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class Content {
    private List<Part> parts;
}
