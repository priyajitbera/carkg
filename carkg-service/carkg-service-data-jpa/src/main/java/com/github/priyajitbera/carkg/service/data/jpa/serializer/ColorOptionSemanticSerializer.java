package com.github.priyajitbera.carkg.service.data.jpa.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.priyajitbera.carkg.service.data.jpa.entity.ColorOption;
import java.io.IOException;

public class ColorOptionSemanticSerializer extends SemanticSerializer<ColorOption> {
  @Override
  public void serialize(ColorOption value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    String display = value.getName();
    gen.writeString(display);
  }
}
