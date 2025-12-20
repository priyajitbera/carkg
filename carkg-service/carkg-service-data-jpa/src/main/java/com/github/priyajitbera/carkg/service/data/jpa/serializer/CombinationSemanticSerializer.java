package com.github.priyajitbera.carkg.service.data.jpa.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.priyajitbera.carkg.service.data.jpa.entity.Combination;
import java.io.IOException;

public class CombinationSemanticSerializer extends SemanticSerializer<Combination> {
  @Override
  public void serialize(Combination value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    String display =
        value.getVariant().getName()
            + " with "
            + value.getEngineOption().getName()
            + " and "
            + value.getTransmissionType().getName()
            + " in "
            + value.getColorOption().getName();
    gen.writeString(display);
  }
}
