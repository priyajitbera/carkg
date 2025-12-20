package com.github.priyajitbera.carkg.service.api.model.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarCreate {

  private String name;
  private String brandId;
  private List<EngineOptionCreate> engineOptions;
  private List<VariantCreate> variants;
  private List<ColorOptionCreate> colorOptions;
  private List<TransmissionTypeCreate> transmissionTypes;
  private List<CombinationCreate> combinations;
}
