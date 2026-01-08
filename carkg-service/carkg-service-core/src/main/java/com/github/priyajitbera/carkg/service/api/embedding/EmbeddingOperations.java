package com.github.priyajitbera.carkg.service.api.embedding;

import java.util.function.Function;

public class EmbeddingOperations {

  public static <T> float[] generate(
      T candidate, Function<T, String> formatter, Function<String, float[]> embedder) {
    final String embeddableFormat = formatter.apply(candidate);
    return embedder.apply(embeddableFormat);
  }
}
