package com.github.priyajitbera.carkg.service.api.prompt.impl;

import com.github.priyajitbera.carkg.service.api.prompt.PromptRepository;
import com.github.priyajitbera.carkg.service.api.prompt.Prompts;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClassPathPromptRepository implements PromptRepository {

  private Map<String, String> prompts;

  public ClassPathPromptRepository() {
    this.prompts =
        Arrays.stream(Prompts.values())
            .reduce(
                new HashMap<>(),
                (map, prompt) -> {
                  map.put(prompt.name(), loadPromptFromFile(prompt.getFileName()));
                  return map;
                },
                (map1, map2) -> {
                  map1.putAll(map2);
                  return map1;
                });
    log.info("Loaded {} prompts, {}", prompts.size(), prompts.keySet());
  }

  private String loadPromptFromFile(String fileName) {
    final String path = "/prompts/" + fileName;
    try {
      return new String(new ClassPathResource(path).getInputStream().readAllBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getPrompt(String promptName) {
    return prompts.get(promptName);
  }
}
