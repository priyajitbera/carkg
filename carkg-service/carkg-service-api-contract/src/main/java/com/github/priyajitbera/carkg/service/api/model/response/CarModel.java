package com.github.priyajitbera.carkg.service.api.model.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarModel {
  private String id;
  private String name;
  private BrandModel brand;
  private List<ColorOptionModel> colorOptions;
  private List<VariantModel> variants;
  private List<EngineModel> engines;
  private List<TransmissionTypeModel> transmissionTypes;
  private List<CombinationModel> combinations;
}
