package com.github.priyajitbera.carkg.service.api.embedding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public abstract class DefaultEmbeddableFormatter<T> implements EmbeddableFormatter<T> {

  private ObjectWriter objectWriter;

  public DefaultEmbeddableFormatter() {
    YAMLFactory yamlFactory = new YAMLFactory();
    yamlFactory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
    yamlFactory.disable(YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS);
    yamlFactory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
    yamlFactory.disable(YAMLGenerator.Feature.SPLIT_LINES);

    ObjectMapper mapper = new ObjectMapper(yamlFactory);
    mapper.registerModule(new JavaTimeModule());
    mapper.setAnnotationIntrospector(new RdfLabelIntrospector());
    mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
    mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, false);
    mapper.configure(MapperFeature.USE_STD_BEAN_NAMING, true);

    this.objectWriter = mapper.writerWithView(getView()).withDefaultPrettyPrinter();
  }

  @Override
  public String format(T candidate) {
    try {
      return objectWriter.writeValueAsString(candidate);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public abstract Class<?> getView();
}
