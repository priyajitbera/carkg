package com.github.priyajitbera.carkg.service.api.embedding;

public interface EmbeddableFormatter<T> {
  String format(T candidate);
}
