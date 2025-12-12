package com.github.priyajitbera.carkg.service.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


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
