package com.github.priyajitbera.carkg.service.model.client.common;

import com.github.priyajitbera.carkg.service.model.client.common.request.Tool;
import com.github.priyajitbera.carkg.service.model.client.common.response.GenerationResponse;

import java.util.List;

public interface GenerativeClient {
    String generate(String prompt);

    GenerationResponse generate(String prompt, List<Tool> tools);
}
