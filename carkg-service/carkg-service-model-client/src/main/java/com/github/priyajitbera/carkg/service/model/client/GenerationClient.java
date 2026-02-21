package com.github.priyajitbera.carkg.service.model.client;

import com.github.priyajitbera.carkg.service.model.client.dto.request.Tool;
import com.github.priyajitbera.carkg.service.model.client.dto.response.GenerationResponse;
import java.util.List;

public interface GenerationClient {
  String generate(String prompt);

  GenerationResponse generate(String prompt, List<Tool> tools);
}
