package com.github.priyajitbera.carkg.service.model.client.gemini.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class Embedding {
    private float[] values;
}
