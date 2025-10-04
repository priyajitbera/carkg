package com.github.priyajitbera.carkg.service.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarModel extends EmbeddableModel {
    private String id;
    private String name;
    private BrandModel brand;
    private List<ColorOptionModel> colorOptions;
    private List<VariantModel> variants;
    private List<EngineModel> engines;
    private List<TransmissionTypeModel> transmissionTypes;
}
