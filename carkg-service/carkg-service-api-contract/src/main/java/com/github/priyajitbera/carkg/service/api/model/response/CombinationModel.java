package com.github.priyajitbera.carkg.service.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CombinationModel {
  private String id;
  private VariantModel variant;
  private EngineModel engine;
  private TransmissionTypeModel transmissionType;
  private ColorOptionModel colorOption;
}
