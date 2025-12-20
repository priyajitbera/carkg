package com.github.priyajitbera.carkg.service.data.jpa.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Variant;
import java.io.IOException;

public class VariantSemanticSerializer extends SemanticSerializer<Variant> {
  @Override
  public void serialize(Variant value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    String display = value.getName();
    gen.writeString(display);
  }
}
